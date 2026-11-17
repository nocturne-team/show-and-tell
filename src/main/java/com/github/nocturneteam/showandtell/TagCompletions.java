package com.github.nocturneteam.showandtell;

import com.github.nocturneteam.showandtell.chat.ChatTag;
import com.github.nocturneteam.showandtell.chat.ChatTagDecorator;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket.Action;
import net.minecraft.server.level.ServerPlayer;

/** Tells each client which chat tags they may use; vanilla clients complete them with Tab. */
public final class TagCompletions {
	private TagCompletions() {}

	public static void send(ServerPlayer player) {
		player.connection.send(new ClientboundCustomChatCompletionsPacket(Action.ADD, ChatTag.completions(tag -> ChatTagDecorator.canUse(player, tag))));
	}

	/** Swaps in a fresh list, e.g. after the config changed which tags are enabled. */
	public static void resend(ServerPlayer player) {
		player.connection.send(new ClientboundCustomChatCompletionsPacket(Action.REMOVE, ChatTag.completions(tag -> true)));
		send(player);
	}
}
