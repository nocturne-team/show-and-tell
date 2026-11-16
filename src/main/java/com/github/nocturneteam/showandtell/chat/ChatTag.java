package com.github.nocturneteam.showandtell.chat;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum ChatTag {
	ITEM("item", "i"),
	INVENTORY("inv", "inventory"),
	ENDER("ender", "ec", "enderchest"),
	ARMOR("armor", "armour");

	private final List<String> names;

	ChatTag(String... names) {
		this.names = List.of(names);
	}

	public String primaryName() {
		return names.getFirst();
	}

	public static Optional<ChatTag> byName(String name) {
		String lower = name.toLowerCase(Locale.ROOT);
		for (ChatTag tag : values()) {
			if (tag.names.contains(lower)) return Optional.of(tag);
		}
		return Optional.empty();
	}

	/** Strings sent to clients as custom chat completions. */
	public static List<String> completions() {
		return Arrays.stream(values()).map(t -> "[" + t.primaryName() + "]").toList();
	}

	/** True when the word before the cursor is an unfinished tag, e.g. "[in". */
	public static boolean isTypingTag(String textBeforeCursor) {
		if (textBeforeCursor.startsWith("/")) return false;
		int wordStart = Math.max(textBeforeCursor.lastIndexOf(' '), textBeforeCursor.lastIndexOf('\t')) + 1;
		String word = textBeforeCursor.substring(wordStart);
		return word.startsWith("[") && word.indexOf(']') < 0;
	}
}
