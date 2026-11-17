package com.github.nocturneteam.showandtell.chat;

import com.github.nocturneteam.showandtell.chat.ChatTagParser.Segment;
import com.github.nocturneteam.showandtell.chat.ChatTagParser.Tag;
import com.github.nocturneteam.showandtell.chat.ChatTagParser.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import com.github.nocturneteam.showandtell.ModLoaderAccess;
import com.github.nocturneteam.showandtell.ShowAndTellConfig;
import com.github.nocturneteam.showandtell.TagPermissions;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side: replaces chat tags with components. Only vanilla hover events
 * are used, so players without the mod still get a tooltip.
 */
public final class ChatTagDecorator {
	private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
	// Player -> when they last used a tag, in nanoTime
	private static final Map<UUID, Long> LAST_USE = new ConcurrentHashMap<>();

	private ChatTagDecorator() {}

	/** Enabled in the config and, for mod tags, the mod is loaded. */
	public static boolean isAvailable(ChatTag tag) {
		return ShowAndTellConfig.get().allows(tag)
			&& tag.requiredMod().map(ModLoaderAccess.INSTANCE::isModLoaded).orElse(true);
	}

	/** Available on this server and the player has its permission. */
	public static boolean canUse(ServerPlayer player, ChatTag tag) {
		return isAvailable(tag) && TagPermissions.canUse(player, tag);
	}

	public static Component decorate(@Nullable ServerPlayer player, Component message) {
		if (player == null) return message;
		ShowAndTellConfig config = ShowAndTellConfig.get();
		List<Segment> segments = ChatTagParser.parse(message.getString(), tag -> canUse(player, tag), config.maxTagsPerMessage());
		if (segments.stream().noneMatch(s -> s instanceof Tag)) return message;
		if (config.cooldownSeconds() > 0 && !TagPermissions.bypassesCooldown(player) && onCooldown(player, config.cooldownSeconds())) return message;

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
			case ITEM -> item(player.getMainHandItem(), tag);
			case OFFHAND -> item(player.getOffhandItem(), tag);
			case INVENTORY -> container(player, tag, "%s's Inventory", inventory(player));
			case ENDER -> container(player, tag, "%s's Ender Chest", contents(player.getEnderChestInventory()));
			case ARMOR -> container(player, tag, "%s's Armor", armor(player));
			case POS -> position(player);
			case CURIOS -> container(player, tag, "%s's Curios", curios(player));
		};
	}

	/** Block position and dimension; clicking suggests a teleport for whoever is allowed to run it. */
	private static Component position(ServerPlayer player) {
		BlockPos pos = player.blockPosition();
		Identifier dimension = player.level().dimension().identifier();
		Component dimensionName = Component.translatableWithFallback(dimension.toLanguageKey("showandtell.dimension"), DimensionNames.fallback(dimension.getPath()));
		Component label = Component.translatableWithFallback("showandtell.tag.pos", "%s, %s, %s in %s", pos.getX(), pos.getY(), pos.getZ(), dimensionName);
		String command = "/execute in " + dimension + " run tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
		return Component.literal("[").append(label).append("]")
			.withStyle(ChatFormatting.GREEN)
			.withStyle(s -> s
				.withHoverEvent(new HoverEvent.ShowText(Component.translatableWithFallback("showandtell.pos.hover", "Click to suggest a teleport here")))
				.withClickEvent(new ClickEvent.SuggestCommand(command)));
	}

	/** On cooldown the tags stay as typed and the player gets told how long is left. */
	private static boolean onCooldown(ServerPlayer player, int cooldownSeconds) {
		if (cooldownSeconds <= 0) return false;
		long now = System.nanoTime();
		Long last = LAST_USE.get(player.getUUID());
		long cooldown = cooldownSeconds * 1_000_000_000L;
		if (last != null && now - last < cooldown) {
			long left = Math.ceilDiv(cooldown - (now - last), 1_000_000_000L);
			player.sendSystemMessage(Component.translatableWithFallback("showandtell.cooldown", "Wait %s s before showing things again", left)
				.withStyle(ChatFormatting.RED), true);
			return true;
		}
		LAST_USE.put(player.getUUID(), now);
		return false;
	}

	private static SharedContainer.Slots curios(ServerPlayer player) {
		//? if neoforge {
		/*return com.github.nocturneteam.showandtell.compat.CuriosCompat.slots(player);
		*///?} else {
		return new SharedContainer.Slots(List.of(), List.of());
		//?}
	}

	/** Head to feet, plus the offhand only when something is in it. */
	private static List<ItemStack> armor(ServerPlayer player) {
		List<ItemStack> out = new ArrayList<>(ARMOR_SLOTS.stream().map(player::getItemBySlot).toList());
		ItemStack offhand = player.getOffhandItem();
		if (!offhand.isEmpty()) out.add(offhand);
		return out;
	}

	private static Component item(ItemStack stack, ChatTag tag) {
		// Empty hand: leave the tag as typed
		if (stack.isEmpty()) return Component.literal("[" + tag.primaryName() + "]");
		MutableComponent name = stack.getDisplayName().copy();
		if (stack.getCount() > 1) name.append(" x" + stack.getCount());
		return name;
	}

	private static Component container(ServerPlayer player, ChatTag tag, String fallback, List<ItemStack> items) {
		return container(player, tag, fallback, new SharedContainer.Slots(items, List.of()));
	}

	private static Component container(ServerPlayer player, ChatTag tag, String fallback, SharedContainer.Slots slots) {
		Component label = Component.translatableWithFallback("showandtell.tag." + tag.primaryName(), fallback, player.getDisplayName());
		ItemStack carrier = SharedContainer.create(tag, label, slots);
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
