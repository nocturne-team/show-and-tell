package com.github.nocturneteam.showandtell;

//? if neoforge {
/*import com.github.nocturneteam.showandtell.chat.ChatTagDecorator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ServerChatEvent;

@Mod(ShowAndTellCommon.MOD_ID)
public class ShowAndTellNeoForge {
	public ShowAndTellNeoForge(IEventBus modEventBus, ModContainer modContainer) {
		ShowAndTellCommon.init();
		NeoForge.EVENT_BUS.addListener(ServerChatEvent.class, e -> e.setMessage(ChatTagDecorator.decorate(e.getPlayer(), e.getMessage())));
	}
}
*///?}
