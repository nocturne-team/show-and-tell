package com.github.nocturneteam.showandtell.mixin.client;

import com.github.nocturneteam.showandtell.chat.ChatTag;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

// Vanilla only shows chat (non-command) suggestions on Tab; pop them up while typing a "[tag".
@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {
	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final private EditBox input;
	@Shadow @Final private boolean commandsOnly;
	@Shadow private boolean allowSuggestions;
	@Shadow private boolean keepSuggestions;
	@Shadow private @Nullable CompletableFuture<Suggestions> pendingSuggestions;

	@Shadow
	public abstract void showSuggestions(boolean immediateNarration);

	@Inject(method = "updateCommandInfo", at = @At("TAIL"))
	private void showandtell$popupTagSuggestions(CallbackInfo ci) {
		if (commandsOnly || keepSuggestions || !allowSuggestions || pendingSuggestions == null) return;
		if (!minecraft.options.autoSuggestions().get()) return;
		String beforeCursor = input.getValue().substring(0, input.getCursorPosition());
		if (ChatTag.isTypingTag(beforeCursor)) showSuggestions(false);
	}
}
