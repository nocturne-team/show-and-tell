package com.github.nocturneteam.showandtell.client;

import com.github.nocturneteam.showandtell.chat.ChatTag;
import com.github.nocturneteam.showandtell.chat.SharedContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;
import java.util.Optional;

/**
 * {@code emptyIcons} gives a silhouette per slot (like armor slots), or is empty for none.
 * {@code gapBeforeRow} adds a small gap above that row (the hotbar), or -1 for none.
 * Layout is shared by the chat tooltip and the preview screen.
 */
public record GridTooltip(List<ItemStack> items, int columns, List<Identifier> emptyIcons, int gapBeforeRow) implements TooltipComponent {
	public static final int SLOT = 18;
	// Same spacing as the hotbar in the inventory screen
	public static final int GAP = 4;

	// Head, chest, legs, feet, offhand: same silhouettes as the inventory screen
	private static final List<Identifier> ARMOR_ICONS = List.of(
		Identifier.withDefaultNamespace("container/slot/helmet"),
		Identifier.withDefaultNamespace("container/slot/chestplate"),
		Identifier.withDefaultNamespace("container/slot/leggings"),
		Identifier.withDefaultNamespace("container/slot/boots"),
		Identifier.withDefaultNamespace("container/slot/shield")
	);

	/** The grid for a shared container carrier, if it is one. */
	public static Optional<GridTooltip> of(ItemStack carrier) {
		return SharedContainer.kind(carrier).map(kind -> {
			// Sized up front since trailing empty slots aren't stored
			int slots = SharedContainer.slots(carrier, kind);
			NonNullList<ItemStack> items = NonNullList.withSize(slots, ItemStack.EMPTY);
			carrier.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
			List<Identifier> icons = switch (kind) {
				case ARMOR -> ARMOR_ICONS;
				case ACCESSORIES -> SharedContainer.icons(carrier, slots);
				default -> List.of();
			};
			// Inventory is sent main rows first, hotbar last
			int gapBeforeRow = kind == ChatTag.INVENTORY ? 3 : -1;
			return new GridTooltip(items, SharedContainer.columns(kind, slots), icons, gapBeforeRow);
		});
	}

	public int rows() {
		return Math.max(1, (items.size() + columns - 1) / columns);
	}

	private boolean hasGap() {
		return gapBeforeRow > 0 && gapBeforeRow < rows();
	}

	public int width() {
		return columns * SLOT;
	}

	public int height() {
		return rows() * SLOT + (hasGap() ? GAP : 0);
	}

	public int slotCount() {
		return rows() * columns;
	}

	public int slotX(int i) {
		return (i % columns) * SLOT;
	}

	public int slotY(int i) {
		int row = i / columns;
		return row * SLOT + (hasGap() && row >= gapBeforeRow ? GAP : 0);
	}

	public ItemStack stack(int i) {
		return i < items.size() ? items.get(i) : ItemStack.EMPTY;
	}

	public Optional<Identifier> emptyIcon(int i) {
		return i < emptyIcons.size() ? Optional.of(emptyIcons.get(i)) : Optional.empty();
	}
}
