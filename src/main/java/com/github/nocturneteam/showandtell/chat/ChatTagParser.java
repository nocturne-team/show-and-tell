package com.github.nocturneteam.showandtell.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Splits a chat message into plain text and {@link ChatTag}s like {@code [item]}. */
public final class ChatTagParser {
	/** Tags past this count stay as plain text, so one message can't carry a huge payload. */
	public static final int MAX_TAGS = 3;

	public sealed interface Segment {}

	public record Text(String text) implements Segment {}

	public record Tag(ChatTag tag) implements Segment {}

	private ChatTagParser() {}

	public static boolean containsTag(String message) {
		return parse(message).stream().anyMatch(s -> s instanceof Tag);
	}

	public static List<Segment> parse(String message) {
		List<Segment> out = new ArrayList<>();
		StringBuilder text = new StringBuilder();
		int tags = 0;
		int i = 0;
		while (i < message.length()) {
			char c = message.charAt(i);
			// \[ escapes a tag
			if (c == '\\' && i + 1 < message.length() && message.charAt(i + 1) == '[') {
				text.append('[');
				i += 2;
				continue;
			}
			if (c == '[') {
				int close = message.indexOf(']', i + 1);
				if (close > 0) {
					Optional<ChatTag> tag = ChatTag.byName(message.substring(i + 1, close));
					if (tag.isPresent() && tags < MAX_TAGS) {
						if (!text.isEmpty()) {
							out.add(new Text(text.toString()));
							text.setLength(0);
						}
						out.add(new Tag(tag.get()));
						tags++;
						i = close + 1;
						continue;
					}
				}
			}
			text.append(c);
			i++;
		}
		if (!text.isEmpty()) out.add(new Text(text.toString()));
		return out;
	}
}
