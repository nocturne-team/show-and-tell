package com.github.nocturneteam.showandtell.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** Draws shared items as a grid of vanilla inventory slots inside the tooltip. */
public class ClientGridTooltip implements ClientTooltipComponent {
	private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
	private static final int SLOT = 18;
	// Same spacing as the hotbar in the inventory screen
	private static final int GAP = 4;

	private final GridTooltip grid;
	private final int rows;

	public ClientGridTooltip(GridTooltip grid) {
		this.grid = grid;
		this.rows = Math.max(1, (grid.items().size() + grid.columns() - 1) / grid.columns());
	}

	@Override
	public int getHeight(Font font) {
		return rows * SLOT + 2 + (hasGap() ? GAP : 0);
	}

	private boolean hasGap() {
		return grid.gapBeforeRow() > 0 && grid.gapBeforeRow() < rows;
	}

	@Override
	public int getWidth(Font font) {
		return grid.columns() * SLOT;
	}

	@Override
	public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
		for (int i = 0; i < rows * grid.columns(); i++) {
			int sx = x + (i % grid.columns()) * SLOT;
			int row = i / grid.columns();
			int sy = y + row * SLOT + (hasGap() && row >= grid.gapBeforeRow() ? GAP : 0);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, sx, sy, SLOT, SLOT);
			ItemStack stack = i < grid.items().size() ? grid.items().get(i) : ItemStack.EMPTY;
			if (stack.isEmpty()) {
				if (i < grid.emptyIcons().size()) {
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, grid.emptyIcons().get(i), sx + 1, sy + 1, 16, 16);
				}
				continue;
			}
			graphics.item(stack, sx + 1, sy + 1);
			graphics.itemDecorations(font, stack, sx + 1, sy + 1);
		}
	}
}
