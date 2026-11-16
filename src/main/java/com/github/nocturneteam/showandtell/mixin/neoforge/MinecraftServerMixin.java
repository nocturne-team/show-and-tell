package com.github.nocturneteam.showandtell.mixin.neoforge;

//? if neoforge {
/*import com.github.nocturneteam.showandtell.chat.ChatTagDecorator;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// NeoForge routes chat through ServerChatEvent, but /msg, /me, /say and /teammsg
// still use this decorator. Fabric API already hooks it, so this is NeoForge-only.
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(method = "getChatDecorator", at = @At("RETURN"), cancellable = true)
	private void showandtell$decorateCommands(CallbackInfoReturnable<ChatDecorator> cir) {
		ChatDecorator original = cir.getReturnValue();
		cir.setReturnValue((player, message) -> ChatTagDecorator.decorate(player, original.decorate(player, message)));
	}
}
*///?}
