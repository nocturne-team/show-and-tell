package com.github.nocturneteam.showandtell.mixin.client;

import com.github.nocturneteam.showandtell.chat.SharedContainer;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

// The grid replaces vanilla's "N x Item" lines on shared containers.
@Mixin(ItemContainerContents.class)
public class ItemContainerContentsMixin {
	@Inject(method = "addToTooltip", at = @At("HEAD"), cancellable = true)
	private void showandtell$hideTextList(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components, CallbackInfo ci) {
		if (SharedContainer.kind(components).isPresent()) ci.cancel();
	}
}
