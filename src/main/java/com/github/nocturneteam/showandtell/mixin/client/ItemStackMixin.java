package com.github.nocturneteam.showandtell.mixin.client;

import com.github.nocturneteam.showandtell.client.GridTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

// Shared containers get a slot grid instead of a text list.
@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
	private void showandtell$gridTooltip(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
		GridTooltip.of((ItemStack) (Object) this).ifPresent(grid -> cir.setReturnValue(Optional.of(grid)));
	}
}
