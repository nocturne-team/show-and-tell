package com.github.nocturneteam.showandtell.chat;

// Kept free of Minecraft classes so it can be unit tested on every loader.
final class GridShape {
	private GridShape() {}

	static int columns(ChatTag kind, int slots) {
		return switch (kind) {
			case ARMOR -> slots;
			// One row like the armor tag, wrapping when there are lots of slots
			case CURIOS -> Math.min(slots, 9);
			// Big backpacks get wider instead of taller, so they fit on screen
			default -> slots > 54 ? Math.min(18, Math.ceilDiv(slots, 6)) : 9;
		};
	}
}
