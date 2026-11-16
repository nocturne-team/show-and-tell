package com.github.nocturneteam.showandtell;

//? if fabric {
import com.github.nocturneteam.showandtell.client.ClientGridTooltip;
import com.github.nocturneteam.showandtell.client.GridTooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;

public class ShowAndTellFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTooltipComponentCallback.EVENT.register(data -> data instanceof GridTooltip grid ? new ClientGridTooltip(grid) : null);
	}
}
//?}
