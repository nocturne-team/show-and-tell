package com.github.nocturneteam.showandtell.client;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** {@code emptyIcons} gives a silhouette per slot (like armor slots), or is empty for none. */
public record GridTooltip(List<ItemStack> items, int columns, List<Identifier> emptyIcons) implements TooltipComponent {}
