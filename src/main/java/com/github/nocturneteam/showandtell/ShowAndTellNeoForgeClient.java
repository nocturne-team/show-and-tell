package com.github.nocturneteam.showandtell;

//? if neoforge {
/*import com.github.nocturneteam.showandtell.client.ClientGridTooltip;
import com.github.nocturneteam.showandtell.client.GridTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@Mod(value = ShowAndTellCommon.MOD_ID, dist = Dist.CLIENT)
public class ShowAndTellNeoForgeClient {
	public ShowAndTellNeoForgeClient(IEventBus modEventBus) {
		modEventBus.addListener(RegisterClientTooltipComponentFactoriesEvent.class, e -> e.register(GridTooltip.class, ClientGridTooltip::new));
	}
}
*///?}
