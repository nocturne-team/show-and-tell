package com.github.nocturneteam.showandtell;

//? if fabric {
import com.github.nocturneteam.showandtell.chat.ChatTagDecorator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;

public class ShowAndTellFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ShowAndTellCommon.init();
		ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.CONTENT_PHASE, ChatTagDecorator::decorate);
	}
}
//?}
