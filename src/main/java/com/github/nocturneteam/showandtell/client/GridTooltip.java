package com.github.nocturneteam.showandtell.client;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record GridTooltip(List<ItemStack> items, int columns) implements TooltipComponent {}
