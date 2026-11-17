package com.github.nocturneteam.showandtell;

import com.github.nocturneteam.showandtell.chat.ChatTag;
import com.github.nocturneteam.showandtell.chat.ChatTagParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

/** Server settings from config/showandtell.json, read once at startup. */
public record ShowAndTellConfig(Set<ChatTag> disabledTags, int maxTagsPerMessage, int cooldownSeconds) {
	public static final ShowAndTellConfig DEFAULT = new ShowAndTellConfig(Set.of(), ChatTagParser.MAX_TAGS, 0);

	static final String DEFAULT_FILE = """
		{
			// Tags players can't use, e.g. ["inv", "ender"]
			"disabled_tags": [],
			// Tags per message; extra ones stay as plain text
			"max_tags_per_message": 3,
			// Seconds a player waits between messages with tags, 0 = off
			"cooldown_seconds": 0
		}
		""";

	private static ShowAndTellConfig current = DEFAULT;

	public static ShowAndTellConfig get() {
		return current;
	}

	public boolean allows(ChatTag tag) {
		return !disabledTags.contains(tag);
	}

	/** Reads the file, writing defaults first if it's missing. On a bad file the previous settings stay. */
	public static boolean load(Path dir) {
		Path file = dir.resolve(ShowAndTellCommon.MOD_ID + ".json");
		try {
			if (Files.notExists(file)) {
				Files.createDirectories(dir);
				Files.writeString(file, DEFAULT_FILE);
			}
			current = parse(Files.readString(file));
			return true;
		} catch (IOException | RuntimeException e) {
			ShowAndTellCommon.LOGGER.error("Couldn't read {}, keeping the previous settings", file, e);
			return false;
		}
	}

	/** Missing or bad fields fall back to defaults; unknown tag names are logged and skipped. */
	static ShowAndTellConfig parse(String json) {
		JsonReader reader = new JsonReader(new StringReader(json));
		// Allows the comments in the default file
		reader.setStrictness(Strictness.LENIENT);
		JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

		Set<ChatTag> disabled = EnumSet.noneOf(ChatTag.class);
		if (root.get("disabled_tags") instanceof JsonArray array) {
			for (JsonElement element : array) {
				String name = element.getAsString();
				ChatTag.byName(name).ifPresentOrElse(disabled::add,
					() -> ShowAndTellCommon.LOGGER.warn("Unknown tag in disabled_tags: {}", name));
			}
		}
		int maxTags = Math.max(0, intOr(root, "max_tags_per_message", DEFAULT.maxTagsPerMessage));
		int cooldown = Math.max(0, intOr(root, "cooldown_seconds", DEFAULT.cooldownSeconds));
		return new ShowAndTellConfig(Set.copyOf(disabled), maxTags, cooldown);
	}

	private static int intOr(JsonObject root, String key, int fallback) {
		JsonElement element = root.get(key);
		if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) return fallback;
		return element.getAsInt();
	}
}
