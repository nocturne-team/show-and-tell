plugins {
	// This plugin applies the correct loom variant based on the Minecraft version
	id("dev.kikugie.loom-back-compat")
	id("com.modrinth.minotaur") version "2.10.0"
}

// DO NOT set group = ...!
version = "${sc.properties["mod.version"]}+${sc.current.version}"
base.archivesName = "${sc.properties["mod.id"]}-fabric"

val requiredJava: JavaVersion = when {
	sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
	sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
	sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
	sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
	else -> JavaVersion.VERSION_1_8
}

// This can be used for publishing on Modrinth and Curseforge
val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
	?.asList().orEmpty().map { it.toString() }

repositories {
	/**
	 * Restricts dependency search of the given [groups] to the [maven URL][url],
	 * improving the setup speed.
	 */
	fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
		forRepository { maven(url) { name = alias } }
		filter { groups.forEach(::includeGroup) }
	}
	strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
	strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
	/**
	 * Fetches only the required Fabric API modules to not waste time downloading all of them for each version.
	 * @see <a href="https://github.com/FabricMC/fabric">List of Fabric API modules</a>
	 */
	fun fapi(vararg modules: String) {
		for (it in modules) modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"]))
	}

	minecraft("com.mojang:minecraft:${sc.current.version}")
	// Applies Mojang Mappings on obfuscated versions
	loomx.applyMojangMappings()

	// Use `mod{dependency type}` even on 26.1+ - loom-back-compat converts them
	modImplementation("net.fabricmc:fabric-loader:${sc.properties["deps.fabric_loader"]}")
	fapi("fabric-message-api-v1", "fabric-rendering-v1", "fabric-command-api-v2")
	// Optional: per-tag permissions through LuckPerms and co.
	modCompileOnly("maven.modrinth:fabric-permissions-api:${sc.properties["deps.fabric_permissions"]}")
	// Optional [backpack] support; absent on snapshot nodes
	(findProperty("deps.travelers") as String?)?.let { modCompileOnly("maven.modrinth:travelersbackpack:$it") }
	// Optional [accessories] support through Trinkets Updated; absent on snapshot nodes
	(findProperty("deps.trinkets") as String?)?.let {
		modCompileOnly("maven.modrinth:trinkets-updated:$it")
		// `-Ptrinkets` adds it to dev runs; it needs the full Fabric API
		if (providers.gradleProperty("trinkets").isPresent) {
			modLocalRuntime("maven.modrinth:trinkets-updated:$it")
			modLocalRuntime("net.fabricmc.fabric-api:fabric-api:${sc.properties["deps.fabric_api"]}")
		}
	}
	// `-Pbackpacks` runs Traveler's Backpack in dev (26.3 only); it needs the full Fabric API and Forge Config API Port
	val configPort = findProperty("deps.forge_config_port") as String?
	if (providers.gradleProperty("backpacks").isPresent && configPort != null) {
		modLocalRuntime("maven.modrinth:travelersbackpack:${sc.properties["deps.travelers"]}")
		modLocalRuntime("maven.modrinth:forge-config-api-port:$configPort")
		modLocalRuntime("net.fabricmc.fabric-api:fabric-api:${sc.properties["deps.fabric_api"]}")
	}

	testImplementation(platform("org.junit:junit-bom:5.11.3"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

loom {
	fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
	accessWidenerPath = sc.process(
		rootProject.file("src/main/resources/showandtell.ct"),
		"build/processed.ct"
	)

	decompilerOptions.named("vineflower") {
		options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
	}

	runConfigs.all {
		preferGradleTask = true
		generateRunConfig = true
		runDirectory = rootProject.file("run") // Shares the run directory between versions
		jvmArguments.add("-Dmixin.debug.export=true") // Exports transformed classes for debugging
	}
}

java {
	withSourcesJar()
	targetCompatibility = requiredJava
	sourceCompatibility = requiredJava

	toolchain {
		vendor = JvmVendorSpec.ADOPTIUM
		languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
	}
}

tasks {
	test {
		useJUnitPlatform()
	}

	processResources {
		fun MutableMap<String, String>.register(key: String, value: String) {
			inputs.property(key, value)
			set(key, value)
		}

		val props = buildMap {
			register("id", sc.properties["mod.id"])
			register("name", sc.properties["mod.name"])
			register("version", sc.properties["mod.version"])
			register("minecraft", sc.properties["mod.mc_compat"])
			register("loader", Regex("\\d+\\.\\d+").find(sc.properties["deps.fabric_loader"])!!.value)
		}

		filesMatching("fabric.mod.json") { expand(props) }

		val mixinJava = "JAVA_${requiredJava.majorVersion}"
		filesMatching("*.mixins.json") { expand("java" to mixinJava) }

		exclude("META-INF/neoforge.mods.toml", "*.neoforge.mixins.json")
	}

	// Includes the license file in the built mod
	withType<Jar> {
		val name = sc.properties["mod.id"]
		inputs.property("mod_id", name)
		from("../../LICENSE") { rename { "$it-$name" } }
	}

	register<Copy>("buildAndCollect") {
		group = "build"
		description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

		inputs.property("version", sc.properties["mod.version"])
		// loomx.mod(Sources)Jar returns the jar task for the applied loom variant
		from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
		into(rootProject.layout.buildDirectory.file("libs/${sc.properties["mod.version"]}"))
	}
}

// Publishing: `./gradlew modrinth` uploads every loader/version jar. Needs MODRINTH_TOKEN; CHANGELOG is optional.
modrinth {
	val modVersion = sc.properties["mod.version"]
	token = providers.environmentVariable("MODRINTH_TOKEN")
	projectId = "p229Avz1"
	// MODRINTH_DEBUG=true prints what would be uploaded instead of uploading.
	debugMode = providers.environmentVariable("MODRINTH_DEBUG").map { it == "true" }.orElse(false)
	versionNumber = "$modVersion+${sc.current.version}-fabric"
	versionName = "Show and Tell $modVersion (Fabric ${sc.current.version})"
	versionType = when {
		"alpha" in modVersion -> "alpha"
		"beta" in modVersion || "rc" in modVersion -> "beta"
		else -> "release"
	}
	changelog = providers.environmentVariable("CHANGELOG").orElse("")
	uploadFile.set(tasks.named(if (tasks.names.contains("remapJar")) "remapJar" else "jar"))
	// Stonecutter flattens TOML lists into mod.mc_releases.0, .1, ...
	gameVersions.addAll(generateSequence(0) { it + 1 }.map { findProperty("mod.mc_releases.$it") as String? }.takeWhile { it != null }.filterNotNull().toList())
	// Server does the work; the client mod only adds the slot grid and tag suggestions
	environment = "server_only_client_optional"
	loaders.add("fabric")
	dependencies {
		required.project("fabric-api")
		optional.project("fabric-permissions-api")
		optional.project("travelersbackpack")
		optional.project("trinkets-updated")
	}
	// `modrinthSyncBody` uploads the project page
	syncBodyFrom = rootProject.file("modrinth.md").readText()
}
