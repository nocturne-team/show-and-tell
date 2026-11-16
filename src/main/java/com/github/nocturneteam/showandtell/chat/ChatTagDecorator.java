package com.github.nocturneteam.showandtell.chat;

import com.github.nocturneteam.showandtell.chat.ChatTagParser.Segment;
import com.github.nocturneteam.showandtell.chat.ChatTagParser.Tag;
import com.github.nocturneteam.showandtell.chat.ChatTagParser.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Server-side: replaces chat tags with components. Only vanilla hover events
 * are used, so players without the mod still get a tooltip.
 */
public final class ChatTagDecorator {
	private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

	private ChatTagDecorator() {}

	public static Component decorate(@Nullable ServerPlayer player, Component message) {
		if (player == null) return message;
		List<Segment> segments = ChatTagParser.parse(message.getString());
		if (segments.stream().noneMatch(s -> s instanceof Tag)) return message;

		MutableComponent out = Component.empty();
		for (Segment segment : segments) {
			switch (segment) {
				case Text(String text) -> out.append(text);
				case Tag(ChatTag tag) -> out.append(render(player, tag));
			}
		}
		return out;
	}

	private static Component render(ServerPlayer player, ChatTag tag) {
		return switch (tag) {
			case ITEM -> item(player.getMainHandItem());
			case INVENTORY -> container(player, tag, "%s's Inventory", inventory(player));
			case ENDER -> container(player, tag, "%s's Ender Chest", contents(player.getEnderChestInventory()));
			case ARMOR -> container(player, tag, "%s's Armor", ARMOR_SLOTS.stream().map(player::getItemBySlot).toList());
		};
	}

	private static Component item(ItemStack stack) {
		// Empty hand: leave the tag as typed
		if (stack.isEmpty()) return Component.literal("[item]");
		MutableComponent name = stack.getDisplayName().copy();
		if (stack.getCount() > 1) name.append(" x" + stack.getCount());
		return name;
	}

	private static Component container(ServerPlayer player, ChatTag tag, String fallback, List<ItemStack> items) {
		Component label = Component.translatableWithFallback("showandtell.tag." + tag.primaryName(), fallback, player.getDisplayName());
		ItemStack carrier = SharedContainer.create(tag, label, items);
		return Component.literal("[").append(label).append("]")
			.withStyle(ChatFormatting.AQUA)
			.withStyle(s -> s.withHoverEvent(new HoverEvent.ShowItem(ItemStackTemplate.fromNonEmptyStack(carrier))));
	}

	/** Main rows first, hotbar last, like the inventory screen. */
	private static List<ItemStack> inventory(ServerPlayer player) {
		List<ItemStack> slots = player.getInventory().getNonEquipmentItems();
		List<ItemStack> out = new ArrayList<>(slots.subList(9, 36));
		out.addAll(slots.subList(0, 9));
		return out;
	}

	private static List<ItemStack> contents(Container container) {
		List<ItemStack> out = new ArrayList<>(container.getContainerSize());
		for (int i = 0; i < container.getContainerSize(); i++) out.add(container.getItem(i));
		return out;
	}
}
