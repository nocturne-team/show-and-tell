package com.github.nocturneteam.showandtell.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/** Read-only view of a shared container, opened by clicking its chat tag; hover items for their full tooltip. */
public class PreviewScreen extends Screen {
	private static final Identifier HIGHLIGHT_BACK = Identifier.withDefaultNamespace("container/slot_highlight_back");
	private static final Identifier HIGHLIGHT_FRONT = Identifier.withDefaultNamespace("container/slot_highlight_front");
	private static final int PADDING = 7;
	private static final int TITLE_HEIGHT = 13;
	// Vanilla container title colour
	private static final int TITLE_COLOR = 0xFF404040;

	private final GridTooltip grid;
	private final @Nullable Screen parent;
	private int left;
	private int top;

	/** Opens on top of the current screen (chat), which comes back on close. */
	public static void open(Component title, GridTooltip grid) {
		Screens.open(new PreviewScreen(title, grid, Screens.current()));
	}

	private PreviewScreen(Component title, GridTooltip grid, @Nullable Screen parent) {
		super(title);
		this.grid = grid;
		this.parent = parent;
	}

	private int panelWidth() {
		return Math.max(grid.width(), font.width(title)) + PADDING * 2;
	}

	private int panelHeight() {
		return grid.height() + TITLE_HEIGHT + PADDING * 2;
	}

	@Override
	protected void init() {
		left = (width - panelWidth()) / 2;
		top = (height - panelHeight()) / 2;
	}

	// 26.4 moved the mouse position into the graphics extractor
	//? if >26.3 {
	/*@Override
	public void extractRenderState(GuiGraphicsExtractor graphics) {
		super.extractRenderState(graphics);
		draw(graphics, graphics.mouseX(), graphics.mouseY());
	}
	*///?} else {
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		draw(graphics, mouseX, mouseY);
	}
	//?}

	private void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		drawPanel(graphics, left, top, panelWidth(), panelHeight());
		graphics.text(font, title, left + PADDING, top + PADDING, TITLE_COLOR, false);

		int gridX = left + (panelWidth() - grid.width()) / 2;
		int gridY = top + PADDING + TITLE_HEIGHT;
		int hovered = slotAt(mouseX - gridX, mouseY - gridY);
		if (hovered >= 0) ClientGridTooltip.blit(graphics, HIGHLIGHT_BACK, gridX + grid.slotX(hovered) - 3, gridY + grid.slotY(hovered) - 3, 24);
		ClientGridTooltip.drawSlots(graphics, font, grid, gridX, gridY);
		if (hovered >= 0) {
			ClientGridTooltip.blit(graphics, HIGHLIGHT_FRONT, gridX + grid.slotX(hovered) - 3, gridY + grid.slotY(hovered) - 3, 24);
			ItemStack stack = grid.stack(hovered);
			if (!stack.isEmpty()) graphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);
		}
	}

	// Same light grey bevelled panel as vanilla inventory screens
	private static void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
		int outline = 0xFF000000;
		int light = 0xFFFFFFFF;
		int shadow = 0xFF555555;
		int fill = 0xFFC6C6C6;
		graphics.fill(x + 2, y, x + w - 3, y + 1, outline);
		graphics.fill(x + 3, y + h - 1, x + w - 2, y + h, outline);
		graphics.fill(x, y + 2, x + 1, y + h - 3, outline);
		graphics.fill(x + w - 1, y + 3, x + w, y + h - 2, outline);
		graphics.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
		graphics.fill(x + 1, y + 1, x + w - 3, y + 3, light);
		graphics.fill(x + 1, y + 1, x + 3, y + h - 3, light);
		graphics.fill(x + 3, y + h - 3, x + w - 1, y + h - 1, shadow);
		graphics.fill(x + w - 3, y + 3, x + w - 1, y + h - 1, shadow);
		// Rounded corners like the vanilla texture
		graphics.fill(x + 1, y + 1, x + 2, y + 2, outline);
		graphics.fill(x + w - 2, y + h - 2, x + w - 1, y + h - 1, outline);
		graphics.fill(x + w - 3, y + 1, x + w - 2, y + 2, fill);
		graphics.fill(x + 1, y + h - 3, x + 2, y + h - 2, fill);
	}

	/** Slot index under a point relative to the grid, or -1. */
	private int slotAt(int x, int y) {
		for (int i = 0; i < grid.slotCount(); i++) {
			int sx = grid.slotX(i);
			int sy = grid.slotY(i);
			if (x >= sx && x < sx + GridTooltip.SLOT && y >= sy && y < sy + GridTooltip.SLOT) return i;
		}
		return -1;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {
		Screens.open(parent);
	}
}
