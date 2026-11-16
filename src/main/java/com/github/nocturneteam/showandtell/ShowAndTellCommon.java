package com.github.nocturneteam.showandtell;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ShowAndTellCommon {
	public static final String MOD_ID = "showandtell";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	// No mod version in code: release fingerprints must only change when the jar's content does
	public static final String MINECRAFT = /*$ minecraft*/ "26.3";

	private ShowAndTellCommon() {}

	public static void init() {
		LOGGER.info("Show and Tell loaded on Minecraft {}", MINECRAFT);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
