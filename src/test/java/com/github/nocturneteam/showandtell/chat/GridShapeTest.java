package com.github.nocturneteam.showandtell.chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GridShapeTest {
	@Test
	void columns() {
		assertEquals(9, GridShape.columns(ChatTag.INVENTORY, 36));
		assertEquals(9, GridShape.columns(ChatTag.BACKPACK, 54));
		assertEquals(11, GridShape.columns(ChatTag.BACKPACK, 63));
		assertEquals(18, GridShape.columns(ChatTag.BACKPACK, 126));
		assertEquals(18, GridShape.columns(ChatTag.BACKPACK, 256));
		assertEquals(5, GridShape.columns(ChatTag.ARMOR, 5));
		assertEquals(4, GridShape.columns(ChatTag.ACCESSORIES, 4));
	}
}
