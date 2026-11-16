package com.github.nocturneteam.showandtell.chat;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;
import java.util.Optional;

/**
 * A shared inventory travels as a carrier item (chest, ender chest, armor stand)
 * holding the contents. Vanilla clients render it like a shulker box tooltip;
 * modded clients spot the marker and draw a slot grid.
 */
public final class SharedContainer {
	private static final String MARKER = "showandtell";

	private SharedContainer() {}

	public static ItemStack create(ChatTag tag, Component name, List<ItemStack> items) {
		ItemStack carrier = new ItemStack(switch (tag) {
			case ENDER -> Items.ENDER_CHEST;
			case ARMOR -> Items.ARMOR_STAND;
			default -> Items.CHEST;
		});
		carrier.set(DataComponents.ITEM_NAME, name);
		carrier.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
		CompoundTag data = new CompoundTag();
		data.putString(MARKER, tag.primaryName());
		carrier.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
		return carrier;
	}

	public static Optional<ChatTag> kind(DataComponentGetter components) {
		CustomData data = components.get(DataComponents.CUSTOM_DATA);
		if (data == null) return Optional.empty();
		return data.copyTag().getString(MARKER).flatMap(ChatTag::byName);
	}

	public static int columns(ChatTag tag) {
		return tag == ChatTag.ARMOR ? 4 : 9;
	}

	public static int slots(ChatTag tag) {
		return switch (tag) {
			case INVENTORY -> 36;
			case ENDER -> 27;
			case ARMOR -> 4;
			case ITEM -> 1;
		};
	}
}
