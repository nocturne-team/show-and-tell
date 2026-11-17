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

	boolean hasPermission(net.minecraft.server.level.ServerPlayer player, String node, boolean opOnly);

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

		@Override
		public boolean hasPermission(net.minecraft.server.level.ServerPlayer player, String node, boolean opOnly) {
			if (loader.isModLoaded("fabric-permissions-api-v0")) return FabricPermissions.check(player, node, opOnly);
			return !opOnly || TagPermissions.isOp(player);
		}
	}

	// Separate class so fabric-permissions-api only loads when it's installed
	final class FabricPermissions {
		static boolean check(net.minecraft.server.level.ServerPlayer player, String node, boolean opOnly) {
			// No fabric-permissions-api build for the 26.4 snapshots yet
			//? if >26.3 {
			/*return !opOnly || TagPermissions.isOp(player);
			*///?} else {
			return me.lucko.fabric.api.permissions.v0.Permissions.check(player, node,
				opOnly ? net.minecraft.server.permissions.PermissionLevel.GAMEMASTERS : net.minecraft.server.permissions.PermissionLevel.ALL);
			//?}
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

		@Override
		public boolean hasPermission(net.minecraft.server.level.ServerPlayer player, String node, boolean opOnly) {
			return NeoForgePermissions.check(player, node, opOnly);
		}
	}
	*///?}
}
