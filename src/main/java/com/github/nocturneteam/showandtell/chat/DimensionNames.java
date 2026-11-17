package com.github.nocturneteam.showandtell.chat;

import java.util.Arrays;
import java.util.Locale;

final class DimensionNames {
	private DimensionNames() {}

	// For clients without our lang file: "the_nether" -> "the Nether", "aether" -> "Aether"
	static String fallback(String path) {
		if (path.equals("overworld")) return "the Overworld";
		String name = String.join(" ", Arrays.stream(path.split("[_/]"))
			.filter(w -> !w.isEmpty())
			.map(w -> w.substring(0, 1).toUpperCase(Locale.ROOT) + w.substring(1))
			.toList());
		return name.startsWith("The ") ? "the " + name.substring(4) : name;
	}
}
