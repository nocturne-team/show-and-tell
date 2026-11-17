package com.github.nocturneteam.showandtell.chat;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A shared inventory travels as a carrier item (chest, ender chest, armor stand)
 * holding the contents. Vanilla clients render it like a shulker box tooltip;
 * modded clients spot the marker and draw a slot grid.
 */
public final class SharedContainer {
	/** Click event id on container tags; modded clients open a preview, the server ignores it. */
	public static final Identifier OPEN = Identifier.fromNamespaceAndPath("showandtell", "open");
	private static final String MARKER = "showandtell";
	private static final String SLOTS = "showandtell_slots";
	private static final String ICONS = "showandtell_icons";
	// Vanilla's container component limit
	private static final int MAX_SLOTS = 256;

	private SharedContainer() {}

	/** Slot contents plus a sprite per slot, shown while it's empty. */
	public record Slots(List<ItemStack> items, List<Identifier> icons) {}

	public static ItemStack create(ChatTag tag, Component name, List<ItemStack> items) {
		return create(tag, name, new Slots(items, List.of()));
	}

	public static ItemStack create(ChatTag tag, Component name, Slots slots) {
		List<ItemStack> items = slots.items();
		ItemStack carrier = new ItemStack(switch (tag) {
			case ENDER -> Items.ENDER_CHEST;
			case ARMOR -> Items.ARMOR_STAND;
			default -> Items.CHEST;
		});
		carrier.set(DataComponents.ITEM_NAME, name);
		carrier.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
		CompoundTag data = new CompoundTag();
		data.putString(MARKER, tag.primaryName());
		// Slot count varies (armor + optional offhand) and trailing empties aren't stored
		data.putInt(SLOTS, items.size());
		if (!slots.icons().isEmpty()) {
			ListTag icons = new ListTag();
			slots.icons().forEach(icon -> icons.add(StringTag.valueOf(icon.toString())));
			data.put(ICONS, icons);
		}
		carrier.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
		return carrier;
	}

	public static Optional<ChatTag> kind(DataComponentGetter components) {
		CustomData data = components.get(DataComponents.CUSTOM_DATA);
		if (data == null) return Optional.empty();
		return data.copyTag().getString(MARKER).flatMap(ChatTag::byName);
	}

	public static int slots(DataComponentGetter components, ChatTag kind) {
		CustomData data = components.get(DataComponents.CUSTOM_DATA);
		int fallback = kind == ChatTag.ENDER ? 27 : kind == ChatTag.ARMOR ? 4 : 36;
		if (data == null) return fallback;
		// Clamped so a crafted item can't make the client allocate something huge
		return Math.clamp(data.copyTag().getIntOr(SLOTS, fallback), 1, MAX_SLOTS);
	}

	/** Per-slot icons sent by the server; empty when missing or not all valid. */
	public static List<Identifier> icons(DataComponentGetter components, int slots) {
		CustomData data = components.get(DataComponents.CUSTOM_DATA);
		if (data == null) return List.of();
		ListTag list = data.copyTag().getListOrEmpty(ICONS);
		List<Identifier> out = new ArrayList<>();
		for (int i = 0; i < Math.min(list.size(), slots); i++) {
			Identifier icon = list.getString(i).map(Identifier::tryParse).orElse(null);
			if (icon == null) return List.of();
			out.add(icon);
		}
		return out;
	}

	public static int columns(ChatTag kind, int slots) {
		return GridShape.columns(kind, slots);
	}
}
