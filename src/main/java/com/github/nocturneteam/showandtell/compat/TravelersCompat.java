package com.github.nocturneteam.showandtell.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

// Only loaded when Traveler's Backpack is installed; callers check first.
// No Traveler's Backpack build for the 26.4 snapshots yet, so those get no-ops.
final class TravelersCompat {
	private TravelersCompat() {}

	static ItemStack worn(ServerPlayer player) {
		//? if >26.3 {
		/*return ItemStack.EMPTY;
		*///?} else {
		return com.tiviacz.travelersbackpack.attachment.AttachmentUtils.getWearingBackpack(player);
		//?}
	}

	static boolean isBackpack(ItemStack stack) {
		//? if >26.3 {
		/*return false;
		*///?} else {
		return stack.getItem() instanceof com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
		//?}
	}

	// Backpacks only store their size once upgraded; until then it's the leather tier's
	static int defaultSlots() {
		//? if >26.3 {
		/*return 0;
		*///?} else {
		return com.tiviacz.travelersbackpack.inventory.Tiers.LEATHER.getStorageSlots();
		//?}
	}
}
