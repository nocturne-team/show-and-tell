package com.github.nocturneteam.showandtell.chat;

import com.github.nocturneteam.showandtell.chat.ChatTagParser.Segment;
import com.github.nocturneteam.showandtell.chat.ChatTagParser.Tag;
import com.github.nocturneteam.showandtell.chat.ChatTagParser.Text;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatTagParserTest {
	@Test
	void plainTextIsOneSegment() {
		assertEquals(List.of(new Text("hello there")), ChatTagParser.parse("hello there"));
	}

	@Test
	void itemTagInMiddle() {
		List<Segment> expected = List.of(new Text("look at "), new Tag(ChatTag.ITEM), new Text(" nice"));
		assertEquals(expected, ChatTagParser.parse("look at [item] nice"));
	}

	@Test
	void shortAliasAndCaseInsensitive() {
		assertEquals(List.of(new Tag(ChatTag.ITEM)), ChatTagParser.parse("[i]"));
		assertEquals(List.of(new Tag(ChatTag.ITEM)), ChatTagParser.parse("[ITEM]"));
	}

	@Test
	void unknownBracketsStayText() {
		assertEquals(List.of(new Text("[hello] [ item]")), ChatTagParser.parse("[hello] [ item]"));
	}

	@Test
	void escapedTagStaysLiteral() {
		assertEquals(List.of(new Text("type [item] to share")), ChatTagParser.parse("type \\[item] to share"));
	}

	@Test
	void tagsBeyondLimitStayText() {
		List<Segment> expected = List.of(
			new Tag(ChatTag.ITEM), new Text(" "),
			new Tag(ChatTag.ITEM), new Text(" "),
			new Tag(ChatTag.ITEM), new Text(" [item]")
		);
		assertEquals(expected, ChatTagParser.parse("[item] [item] [item] [item]"));
	}

	@Test
	void containsTagShortcut() {
		assertTrue(ChatTagParser.containsTag("hey [i]"));
		assertFalse(ChatTagParser.containsTag("hey \\[i] and [x]"));
	}
}
