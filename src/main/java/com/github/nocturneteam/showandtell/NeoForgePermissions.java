package com.github.nocturneteam.showandtell;

//? if neoforge {
/*import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// NeoForge wants every node registered up front; LuckPerms and co. hook in through PermissionAPI.
public final class NeoForgePermissions {
	private static final String PREFIX = ShowAndTellCommon.MOD_ID + ".";
	private static final Map<String, PermissionNode<Boolean>> NODES = TagPermissions.nodes().stream()
		.map(NeoForgePermissions::create)
		.collect(Collectors.toUnmodifiableMap(PermissionNode::getNodeName, Function.identity()));

	private NeoForgePermissions() {}

	private static PermissionNode<Boolean> create(TagPermissions.Node node) {
		return new PermissionNode<>(ShowAndTellCommon.MOD_ID, node.name().substring(PREFIX.length()), PermissionTypes.BOOLEAN,
			(player, uuid, context) -> !node.opOnly() || (player != null && TagPermissions.isOp(player)));
	}

	public static void register(PermissionGatherEvent.Nodes event) {
		event.addNodes(NODES.values().toArray(PermissionNode[]::new));
	}

	public static boolean check(ServerPlayer player, String node, boolean opOnly) {
		PermissionNode<Boolean> registered = NODES.get(node);
		if (registered == null) return !opOnly || TagPermissions.isOp(player);
		return PermissionAPI.getPermission(player, registered);
	}
}
*///?}
