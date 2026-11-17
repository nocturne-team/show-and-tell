package com.github.nocturneteam.showandtell.compat;

//? if fabric {
import com.github.nocturneteam.showandtell.ModLoaderAccess;
import com.github.nocturneteam.showandtell.chat.SharedContainer.Slots;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

// Trinkets Updated; no build for the 26.4 snapshots yet, so those show nothing.
public final class TrinketsCompat {
	private TrinketsCompat() {}

	// Every visible slot, groups and slots in Trinkets' own order, empty ones with their slot icon
	public static Slots slots(ServerPlayer player) {
		List<ItemStack> items = new ArrayList<>();
		List<Identifier> icons = new ArrayList<>();
		//? if <=26.3 {
		if (ModLoaderAccess.INSTANCE.isModLoaded("trinkets")) Api.collect(player, items, icons);
		//?}
		return new Slots(items, icons);
	}

	//? if <=26.3 {
	// Separate class so Trinkets types only load when the mod is there
	private static final class Api {
		static void collect(ServerPlayer player, List<ItemStack> items, List<Identifier> icons) {
			var groups = eu.pb4.trinkets.api.TrinketsApi.getAttachment(player).getInventory();
			var groupOrder = eu.pb4.trinkets.api.TrinketsApi.getPlayerSlots(player);
			groups.entrySet().stream()
				.sorted(java.util.Comparator.comparingInt((java.util.Map.Entry<String, ?> e) -> {
					var group = groupOrder.get(e.getKey());
					return group != null ? group.order() : Integer.MAX_VALUE;
				}).thenComparing(java.util.Map.Entry::getKey))
				.forEach(group -> group.getValue().values().stream()
					.filter(inv -> !inv.slotType().isHidden())
					.sorted(java.util.Comparator.comparingInt((eu.pb4.trinkets.api.TrinketInventory inv) -> inv.slotType().order())
						.thenComparing(inv -> inv.slotType().name()))
					.forEach(inv -> {
						for (int i = 0; i < inv.getContainerSize(); i++) {
							items.add(inv.getItem(i));
							icons.add(inv.slotType().icon());
						}
					}));
		}
	}
	//?}
}
//?}
