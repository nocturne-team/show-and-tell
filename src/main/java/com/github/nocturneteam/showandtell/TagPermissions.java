package com.github.nocturneteam.showandtell;

import com.github.nocturneteam.showandtell.chat.ChatTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import java.util.Arrays;
import java.util.List;

/** Permission nodes; without a permissions mod, tags are open to everyone and the bypass is ops only. */
public final class TagPermissions {
	public static final String COOLDOWN_BYPASS = "showandtell.bypass.cooldown";

	private TagPermissions() {}

	public static String node(ChatTag tag) {
		return "showandtell.tag." + tag.primaryName();
	}

	/** Every node, with whether it defaults to ops only. */
	public static List<Node> nodes() {
		List<Node> out = new java.util.ArrayList<>(Arrays.stream(ChatTag.values()).map(t -> new Node(node(t), false)).toList());
		out.add(new Node(COOLDOWN_BYPASS, true));
		return out;
	}

	public record Node(String name, boolean opOnly) {}

	public static boolean canUse(ServerPlayer player, ChatTag tag) {
		return ModLoaderAccess.INSTANCE.hasPermission(player, node(tag), false);
	}

	public static boolean bypassesCooldown(ServerPlayer player) {
		return ModLoaderAccess.INSTANCE.hasPermission(player, COOLDOWN_BYPASS, true);
	}

	public static boolean isOp(ServerPlayer player) {
		return player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
	}
}
