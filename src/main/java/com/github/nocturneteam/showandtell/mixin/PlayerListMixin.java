package com.github.nocturneteam.showandtell.mixin;

import com.github.nocturneteam.showandtell.TagCompletions;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {
	@Inject(method = "placeNewPlayer", at = @At("TAIL"))
	private void showandtell$sendTagCompletions(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
		TagCompletions.send(player);
	}
}
