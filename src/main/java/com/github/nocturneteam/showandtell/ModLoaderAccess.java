package com.github.nocturneteam.showandtell;

import java.nio.file.Path;

// Loader-specific bits behind one interface.
public sealed interface ModLoaderAccess {
	ModLoaderAccess INSTANCE =
		/*? if fabric {*/new FabricLoaderAccess();
		/*?} elif neoforge *///new NeoForgeLoaderAccess();

	boolean isClient();

	boolean isModLoaded(String id);

	Path configDir();

	//? if fabric {
	final class FabricLoaderAccess implements ModLoaderAccess {
		private final net.fabricmc.loader.api.FabricLoader loader = net.fabricmc.loader.api.FabricLoader.getInstance();

		@Override
		public boolean isClient() {
			return loader.getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT;
		}

		@Override
		public boolean isModLoaded(String id) {
			return loader.isModLoaded(id);
		}

		@Override
		public Path configDir() {
			return loader.getConfigDir();
		}
	}
	//?} elif neoforge {
	/*final class NeoForgeLoaderAccess implements ModLoaderAccess {
		private final net.neoforged.api.distmarker.Dist dist = net.neoforged.fml.loading.FMLEnvironment.getDist();
		private final net.neoforged.fml.loading.LoadingModList mods = net.neoforged.fml.loading.FMLLoader.getCurrent().getLoadingModList();

		@Override
		public boolean isClient() {
			return dist.isClient();
		}

		@Override
		public boolean isModLoaded(String id) {
			return mods.getModFileById(id) != null;
		}

		@Override
		public Path configDir() {
			return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();
		}
	}
	*///?}
}
