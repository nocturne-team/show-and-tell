package com.github.nocturneteam.showandtell.chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DimensionNamesTest {
	@Test
	void dimensionFallbackNames() {
		assertEquals("the Overworld", DimensionNames.fallback("overworld"));
		assertEquals("the Nether", DimensionNames.fallback("the_nether"));
		assertEquals("Twilight Forest", DimensionNames.fallback("twilight_forest"));
		assertEquals("Moon Orbit", DimensionNames.fallback("moon/orbit"));
	}
}
