import dev.kikugie.stonecutter.controller.task.StonecutterTaskMutexService

plugins {
	id("net.neoforged.moddev") version "2.0.148"
	id("com.modrinth.minotaur") version "2.10.0"
}

version = "${sc.properties["mod.version"]}+${sc.current.version}"
base.archivesName = "${sc.properties["mod.id"]}-neoforge"

val requiredJava = when {
	sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
	sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
	sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
	sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
	else -> JavaVersion.VERSION_1_8
}

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
	strictMaven("https://maven.theillusivec4.top", "Illusive", "top.theillusivec4.curios")
}

// Dev-run only mods, never in the built jar
val localRuntime: Configuration by configurations.creating
configurations.runtimeClasspath { extendsFrom(localRuntime) }

dependencies {
	val curios = "top.theillusivec4.curios:curios-neoforge:${sc.properties["deps.curios"]}"
	compileOnly("$curios:api")
	// `-Pcurios` adds Curios to runClient/runServer for testing [curios]
	if (providers.gradleProperty("curios").isPresent) localRuntime(curios)
	// Optional [backpack] support
	val backpacks = listOf(
		"maven.modrinth:travelersbackpack:${sc.properties["deps.travelers"]}",
		"maven.modrinth:sophisticated-backpacks:${sc.properties["deps.sophisticated"]}",
		"maven.modrinth:sophisticated-core:${sc.properties["deps.sophisticated_core"]}"
	)
	backpacks.forEach { compileOnly(it) }
	// `-Pbackpacks` adds both backpack mods to dev runs
	if (providers.gradleProperty("backpacks").isPresent) backpacks.forEach { localRuntime(it) }
	// `-Pluckperms` does the same for LuckPerms, to test permission nodes
	val luckperms = findProperty("deps.luckperms") as String?
	if (providers.gradleProperty("luckperms").isPresent && luckperms != null) localRuntime("maven.modrinth:luckperms:$luckperms")

	testImplementation(platform("org.junit:junit-bom:5.11.3"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// MDG leaves Minecraft's libraries off the unit test classpath
	testRuntimeOnly("com.google.code.gson:gson:2.14.0")
	testRuntimeOnly("org.slf4j:slf4j-api:2.0.20")
}

neoForge {
	version = sc.properties["deps.neo_loader"]

	mods {
		register("showandtell") {
			sourceSet(sourceSets.main.get())
		}
	}

	runs {
		register("client") {
			gameDirectory = file("../../run/")
			client()
		}

		register("server") {
			gameDirectory = file("../../run/")
			server()
		}
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
			// 26.2+ shows square icons from iconFile; logoFile there is a banner
			register("icon_key", if (sc.current.parsed >= "26.2") "iconFile" else "logoFile")
		}

		filesMatching("META-INF/neoforge.mods.toml") { expand(props) }

		val mixinJava = "JAVA_${requiredJava.majorVersion}"
		filesMatching("*.mixins.json") { expand("java" to mixinJava) }

		exclude("fabric.mod.json", "*.ct", "*.classtweaker")
	}

	named("createMinecraftArtifacts") {
		dependsOn("stonecutterGenerate")
		usesService(StonecutterTaskMutexService.create(gradle, name))
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
		from(jar.flatMap { it.archiveFile }, named<Jar>("sourcesJar").flatMap { it.archiveFile })
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
	versionNumber = "$modVersion+${sc.current.version}-neoforge"
	versionName = "Show and Tell $modVersion (NeoForge ${sc.current.version})"
	versionType = when {
		"alpha" in modVersion -> "alpha"
		"beta" in modVersion || "rc" in modVersion -> "beta"
		else -> "release"
	}
	changelog = providers.environmentVariable("CHANGELOG").orElse("")
	uploadFile.set(tasks.named("jar"))
	// Stonecutter flattens TOML lists into mod.mc_releases.0, .1, ...
	gameVersions.addAll(generateSequence(0) { it + 1 }.map { findProperty("mod.mc_releases.$it") as String? }.takeWhile { it != null }.filterNotNull().toList())
	// Server does the work; the client mod only adds the slot grid and tag suggestions
	environment = "server_only_client_optional"
	loaders.add("neoforge")
	dependencies {
		optional.project("curios")
		optional.project("travelersbackpack")
		optional.project("sophisticated-backpacks")
	}
}
