package com.github.nocturneteam.showandtell.client;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * {@code emptyIcons} gives a silhouette per slot (like armor slots), or is empty for none.
 * {@code gapBeforeRow} adds a small gap above that row (the hotbar), or -1 for none.
 */
public record GridTooltip(List<ItemStack> items, int columns, List<Identifier> emptyIcons, int gapBeforeRow) implements TooltipComponent {}
