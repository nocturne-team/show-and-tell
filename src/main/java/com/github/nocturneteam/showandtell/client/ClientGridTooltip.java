package com.github.nocturneteam.showandtell.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

/** Draws shared items as a slot grid inside the tooltip. */
public class ClientGridTooltip implements ClientTooltipComponent {
	private static final int SLOT = 18;
	private static final int SLOT_BORDER = 0x40FFFFFF;
	private static final int SLOT_FILL = 0x30000000;

	private final GridTooltip grid;
	private final int rows;

	public ClientGridTooltip(GridTooltip grid) {
		this.grid = grid;
		this.rows = Math.max(1, (grid.items().size() + grid.columns() - 1) / grid.columns());
	}

	@Override
	public int getHeight(Font font) {
		return rows * SLOT + 2;
	}

	@Override
	public int getWidth(Font font) {
		return grid.columns() * SLOT;
	}

	@Override
	public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
		for (int i = 0; i < rows * grid.columns(); i++) {
			int sx = x + (i % grid.columns()) * SLOT;
			int sy = y + (i / grid.columns()) * SLOT;
			graphics.fill(sx, sy, sx + SLOT, sy + SLOT, SLOT_BORDER);
			graphics.fill(sx + 1, sy + 1, sx + SLOT - 1, sy + SLOT - 1, SLOT_FILL);
			if (i >= grid.items().size()) continue;
			ItemStack stack = grid.items().get(i);
			if (stack.isEmpty()) continue;
			graphics.item(stack, sx + 1, sy + 1);
			graphics.itemDecorations(font, stack, sx + 1, sy + 1);
		}
	}
}
