package com.github.nocturneteam.showandtell.mixin.client;

import com.github.nocturneteam.showandtell.chat.SharedContainer;
import com.github.nocturneteam.showandtell.client.GridTooltip;
import com.github.nocturneteam.showandtell.client.PreviewScreen;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Clicking an [inv]/[ender]/... tag opens it in a preview screen.
@Mixin(ChatScreen.class)
public class ChatScreenMixin {
	@Inject(method = "handleComponentClicked", at = @At("HEAD"), cancellable = true)
	private void showandtell$openPreview(Style style, boolean insert, CallbackInfoReturnable<Boolean> cir) {
		// Shift-click keeps vanilla's insert behaviour
		if (insert || style == null || !(style.getClickEvent() instanceof ClickEvent.Custom(var id, var payload)) || !id.equals(SharedContainer.OPEN)) return;
		if (!(style.getHoverEvent() instanceof HoverEvent.ShowItem(var template))) return;
		ItemStack carrier = template.create();
		GridTooltip.of(carrier).ifPresent(grid -> {
			PreviewScreen.open(carrier.getHoverName(), grid);
			cir.setReturnValue(true);
		});
	}
}
