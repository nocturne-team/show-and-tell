package com.github.nocturneteam.showandtell.mixin.client;

import com.github.nocturneteam.showandtell.chat.SharedContainer;
import com.github.nocturneteam.showandtell.client.GridTooltip;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
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
		ItemStack self = (ItemStack) (Object) this;
		SharedContainer.kind(self).ifPresent(kind -> {
			// Sized up front since trailing empty slots aren't stored
			int slots = SharedContainer.slots(self, kind);
			NonNullList<ItemStack> items = NonNullList.withSize(slots, ItemStack.EMPTY);
			self.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
			cir.setReturnValue(Optional.of(new GridTooltip(items, SharedContainer.columns(kind, slots))));
		});
	}
}
