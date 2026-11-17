package com.github.nocturneteam.showandtell;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

// /showandtell reload
public final class ShowAndTellCommand {
	private ShowAndTellCommand() {}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal(ShowAndTellCommon.MOD_ID)
			.requires(ShowAndTellCommand::canReload)
			.then(Commands.literal("reload").executes(context -> {
				CommandSourceStack source = context.getSource();
				if (!ShowAndTellConfig.load(ModLoaderAccess.INSTANCE.configDir())) {
					source.sendFailure(Component.translatableWithFallback("showandtell.command.reload.failed",
						"Couldn't read config/showandtell.json, kept the previous settings (see the server log)"));
					return 0;
				}
				// Enabled tags may have changed
				source.getServer().getPlayerList().getPlayers().forEach(TagCompletions::resend);
				source.sendSuccess(() -> Component.translatableWithFallback("showandtell.command.reload.done", "Show and Tell config reloaded"), true);
				return 1;
			})));
	}

	private static boolean canReload(CommandSourceStack source) {
		ServerPlayer player = source.getPlayer();
		// Console and command blocks go by their own permission level
		if (player == null) return source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
		return ModLoaderAccess.INSTANCE.hasPermission(player, TagPermissions.RELOAD, true);
	}
}
