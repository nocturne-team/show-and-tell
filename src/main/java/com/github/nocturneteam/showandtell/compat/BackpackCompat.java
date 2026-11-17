package com.github.nocturneteam.showandtell.compat;

import com.github.nocturneteam.showandtell.ModLoaderAccess;
import com.github.nocturneteam.showandtell.chat.SharedContainer.Slots;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;
import java.util.Optional;

/**
 * Finds the backpack to show: the one in the main hand, else the one worn.
 * Traveler's Backpack keeps items in a data component, read through the registry;
 * Sophisticated Backpacks stores them server-side, so that goes through its API.
 */
public final class BackpackCompat {
	private static final String TRAVELERS = "travelersbackpack";
	private static final String SOPHISTICATED = "sophisticatedbackpacks";
	// Contents trim trailing empties, so the size comes from its own component (or the tier default)
	private static final Identifier TRAVELERS_CONTAINER = Identifier.fromNamespaceAndPath(TRAVELERS, "backpack_container");
	private static final Identifier TRAVELERS_SLOTS = Identifier.fromNamespaceAndPath(TRAVELERS, "storage_slots");

	private BackpackCompat() {}

	public static Optional<Slots> find(ServerPlayer player) {
		Optional<Slots> held = contents(player, player.getMainHandItem());
		if (held.isPresent()) return held;
		if (loaded(TRAVELERS)) {
			Optional<Slots> worn = travelers(TravelersCompat.worn(player));
			if (worn.isPresent()) return worn;
		}
		//? if neoforge {
		/*if (loaded(SOPHISTICATED)) return SophisticatedCompat.worn(player);
		*///?}
		return Optional.empty();
	}

	private static Optional<Slots> contents(ServerPlayer player, ItemStack stack) {
		if (stack.isEmpty()) return Optional.empty();
		Optional<Slots> travelers = loaded(TRAVELERS) ? travelers(stack) : Optional.empty();
		//? if neoforge {
		/*if (travelers.isEmpty() && loaded(SOPHISTICATED)) return SophisticatedCompat.contents(stack);
		*///?}
		return travelers;
	}

	@SuppressWarnings("unchecked")
	private static Optional<Slots> travelers(ItemStack stack) {
		if (stack.isEmpty()) return Optional.empty();
		var container = (DataComponentType<ItemContainerContents>) BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(TRAVELERS_CONTAINER);
		var size = (DataComponentType<Integer>) BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(TRAVELERS_SLOTS);
		if (container == null || size == null || !TravelersCompat.isBackpack(stack)) return Optional.empty();
		NonNullList<ItemStack> items = NonNullList.withSize(Math.max(1, stack.getOrDefault(size, TravelersCompat.defaultSlots())), ItemStack.EMPTY);
		stack.getOrDefault(container, ItemContainerContents.EMPTY).copyInto(items);
		return Optional.of(new Slots(List.copyOf(items), List.of()));
	}

	private static boolean loaded(String mod) {
		return ModLoaderAccess.INSTANCE.isModLoaded(mod);
	}
}
