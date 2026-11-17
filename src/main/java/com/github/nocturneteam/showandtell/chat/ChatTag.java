package com.github.nocturneteam.showandtell.chat;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public enum ChatTag {
	ITEM(List.of(), "item", "i"),
	OFFHAND(List.of(), "offhand", "oh"),
	INVENTORY(List.of(), "inv", "inventory"),
	ENDER(List.of(), "ender", "ec", "enderchest"),
	ARMOR(List.of(), "armor", "armour"),
	POS(List.of(), "pos", "loc", "coords"),
	// Curios (NeoForge) or Trinkets Updated (Fabric)
	ACCESSORIES(List.of("curios", "trinkets"), "accessories", "acc", "curios", "curio", "trinkets", "trinket"),
	// Traveler's Backpack (both loaders) or Sophisticated Backpacks (NeoForge)
	BACKPACK(List.of("travelersbackpack", "sophisticatedbackpacks"), "backpack", "bp");

	private final List<String> requiredMods;
	private final List<String> names;

	ChatTag(List<String> requiredMods, String... names) {
		this.requiredMods = requiredMods;
		this.names = List.of(names);
	}

	/** At least one of these mods has to be loaded for the tag to work; empty means none needed. */
	public List<String> requiredMods() {
		return requiredMods;
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
