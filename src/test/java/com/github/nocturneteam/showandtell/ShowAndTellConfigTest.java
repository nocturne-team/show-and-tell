package com.github.nocturneteam.showandtell;

import com.github.nocturneteam.showandtell.chat.ChatTag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShowAndTellConfigTest {
	@Test
	void defaultFileMatchesDefaults() {
		assertEquals(ShowAndTellConfig.DEFAULT, ShowAndTellConfig.parse(ShowAndTellConfig.DEFAULT_FILE));
	}

	@Test
	void readsValuesAndAliases() {
		ShowAndTellConfig config = ShowAndTellConfig.parse("""
			{"disabled_tags": ["ec", "Inv", "nope"], "max_tags_per_message": 1, "cooldown_seconds": 5}
			""");
		assertEquals(Set.of(ChatTag.ENDER, ChatTag.INVENTORY), config.disabledTags());
		assertEquals(1, config.maxTagsPerMessage());
		assertEquals(5, config.cooldownSeconds());
		assertFalse(config.allows(ChatTag.ENDER));
		assertTrue(config.allows(ChatTag.ITEM));
	}

	@Test
	void badValuesFallBack() {
		ShowAndTellConfig config = ShowAndTellConfig.parse("""
			{"max_tags_per_message": "lots", "cooldown_seconds": -3}
			""");
		assertEquals(ShowAndTellConfig.DEFAULT.maxTagsPerMessage(), config.maxTagsPerMessage());
		assertEquals(0, config.cooldownSeconds());
	}
}
