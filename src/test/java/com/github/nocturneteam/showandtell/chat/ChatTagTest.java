package com.github.nocturneteam.showandtell.chat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatTagTest {
	@Test
	void namesAndAliases() {
		assertEquals(Optional.of(ChatTag.INVENTORY), ChatTag.byName("inv"));
		assertEquals(Optional.of(ChatTag.INVENTORY), ChatTag.byName("inventory"));
		assertEquals(Optional.of(ChatTag.ENDER), ChatTag.byName("ender"));
		assertEquals(Optional.of(ChatTag.ENDER), ChatTag.byName("ec"));
		assertEquals(Optional.of(ChatTag.ARMOR), ChatTag.byName("Armor"));
		assertEquals(Optional.of(ChatTag.ARMOR), ChatTag.byName("armour"));
		assertEquals(Optional.empty(), ChatTag.byName("nope"));
	}

	@Test
	void completionsUsePrimaryNames() {
		assertEquals(List.of("[item]", "[inv]", "[ender]", "[armor]", "[curios]"), ChatTag.completions(t -> true));
	}

	@Test
	void completionsSkipUnavailableTags() {
		assertEquals(List.of("[item]", "[armor]"), ChatTag.completions(t -> t == ChatTag.ITEM || t == ChatTag.ARMOR));
	}

	@Test
	void onlyModTagsNeedAMod() {
		assertEquals(Optional.of("curios"), ChatTag.CURIOS.requiredMod());
		assertEquals(Optional.empty(), ChatTag.ITEM.requiredMod());
	}

	@Test
	void typingTagOnlyWhenWordStartsWithBracket() {
		assertTrue(ChatTag.isTypingTag("["));
		assertTrue(ChatTag.isTypingTag("look at [in"));
		assertFalse(ChatTag.isTypingTag(""));
		assertFalse(ChatTag.isTypingTag("hello"));
		assertFalse(ChatTag.isTypingTag("look at [item]"));
		assertFalse(ChatTag.isTypingTag("look at [item] "));
		assertFalse(ChatTag.isTypingTag("a[b"));
		assertFalse(ChatTag.isTypingTag("\\["));
		assertFalse(ChatTag.isTypingTag("/give @s ["));
	}
}
