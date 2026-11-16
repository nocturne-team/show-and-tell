package com.github.nocturneteam.showandtell;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ShowAndTellCommon {
	public static final String MOD_ID = "showandtell";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String VERSION = /*$ mod_version*/ "0.1.0";
	public static final String MINECRAFT = /*$ minecraft*/ "26.3";

	private ShowAndTellCommon() {}

	public static void init() {
		LOGGER.info("Show and Tell {} on Minecraft {}", VERSION, MINECRAFT);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
