package com.github.nocturneteam.showandtell.chat;

import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public enum ChatTag {
	ITEM(null, "item", "i"),
	OFFHAND(null, "offhand", "oh"),
	INVENTORY(null, "inv", "inventory"),
	ENDER(null, "ender", "ec", "enderchest"),
	ARMOR(null, "armor", "armour"),
	POS(null, "pos", "loc", "coords"),
	// NeoForge only on 26.x
	CURIOS("curios", "curios", "curio");

	private final @Nullable String requiredMod;
	private final List<String> names;

	ChatTag(@Nullable String requiredMod, String... names) {
		this.requiredMod = requiredMod;
		this.names = List.of(names);
	}

	/** Mod that has to be loaded for this tag to work, if any. */
	public Optional<String> requiredMod() {
		return Optional.ofNullable(requiredMod);
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
	public static List<String> completions(Predicate<ChatTag> enabled) {
		return Arrays.stream(values()).filter(enabled).map(t -> "[" + t.primaryName() + "]").toList();
	}

	/** True when the word before the cursor is an unfinished tag, e.g. "[in". */
	public static boolean isTypingTag(String textBeforeCursor) {
		if (textBeforeCursor.startsWith("/")) return false;
		int wordStart = Math.max(textBeforeCursor.lastIndexOf(' '), textBeforeCursor.lastIndexOf('\t')) + 1;
		String word = textBeforeCursor.substring(wordStart);
		return word.startsWith("[") && word.indexOf(']') < 0;
	}
}
