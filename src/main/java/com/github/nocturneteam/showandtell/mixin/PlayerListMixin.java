package com.github.nocturneteam.showandtell.mixin;

import com.github.nocturneteam.showandtell.chat.ChatTag;
import com.github.nocturneteam.showandtell.chat.ChatTagDecorator;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Tells every client which chat tags this server allows; vanilla clients complete them with Tab.
@Mixin(PlayerList.class)
public class PlayerListMixin {
	@Inject(method = "placeNewPlayer", at = @At("TAIL"))
	private void showandtell$sendTagCompletions(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
		player.connection.send(new ClientboundCustomChatCompletionsPacket(ClientboundCustomChatCompletionsPacket.Action.ADD, ChatTag.completions(ChatTagDecorator::isAvailable)));
	}
}
