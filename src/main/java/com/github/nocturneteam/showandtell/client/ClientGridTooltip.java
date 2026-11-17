package com.github.nocturneteam.showandtell.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
//? if >26.3 {
/*import net.minecraft.client.gui.GuiPipeline;
*///?} else {
import net.minecraft.client.renderer.RenderPipelines;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** Draws shared items as a grid of vanilla inventory slots inside the tooltip. */
public class ClientGridTooltip implements ClientTooltipComponent {
	private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
	private static final Component HINT = Component.translatableWithFallback("showandtell.preview.hint", "Click to open").withStyle(ChatFormatting.GRAY);
	private static final int HINT_HEIGHT = 11;

	private final GridTooltip grid;

	public ClientGridTooltip(GridTooltip grid) {
		this.grid = grid;
	}

	@Override
	public int getHeight(Font font) {
		return grid.height() + 2 + HINT_HEIGHT;
	}

	@Override
	public int getWidth(Font font) {
		return Math.max(grid.width(), font.width(HINT));
	}

	@Override
	public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
		drawSlots(graphics, font, grid, x, y);
	}

	@Override
	public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
		graphics.text(font, HINT, x, y + grid.height() + 3, -1, true);
	}

	/** Slot backgrounds, empty-slot icons and items; also used by the preview screen. */
	static void drawSlots(GuiGraphicsExtractor graphics, Font font, GridTooltip grid, int x, int y) {
		for (int i = 0; i < grid.slotCount(); i++) {
			int sx = x + grid.slotX(i);
			int sy = y + grid.slotY(i);
			blit(graphics, SLOT_SPRITE, sx, sy, GridTooltip.SLOT);
			ItemStack stack = grid.stack(i);
			if (stack.isEmpty()) {
				grid.emptyIcon(i).ifPresent(icon -> blit(graphics, icon, sx + 1, sy + 1, 16));
				continue;
			}
			graphics.item(stack, sx + 1, sy + 1);
			graphics.itemDecorations(font, stack, sx + 1, sy + 1);
		}
	}

	static void blit(GuiGraphicsExtractor graphics, Identifier sprite, int x, int y, int size) {
		// 26.4 moved GUI pipelines to their own type
		//? if >26.3 {
		/*graphics.blitSprite(GuiPipeline.TEXTURED, sprite, x, y, size, size);
		*///?} else {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, size, size);
		//?}
	}

	static void blit(GuiGraphicsExtractor graphics, Identifier sprite, int x, int y, int width, int height) {
		//? if >26.3 {
		/*graphics.blitSprite(GuiPipeline.TEXTURED, sprite, x, y, width, height);
		*///?} else {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
		//?}
	}
}
