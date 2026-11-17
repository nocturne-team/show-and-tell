package com.github.nocturneteam.showandtell.compat;

//? if neoforge {
/*import com.github.nocturneteam.showandtell.chat.SharedContainer.Slots;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Only loaded when Sophisticated Backpacks is installed; callers check first.
final class SophisticatedCompat {
	private SophisticatedCompat() {}

	// The backpack shown on the player's back
	static Optional<Slots> worn(ServerPlayer player) {
		return PlayerInventoryProvider.get().getBackpackFromRendered(player, false)
			.flatMap(info -> contents(info.getBackpack()));
	}

	static Optional<Slots> contents(ItemStack stack) {
		if (!(stack.getItem() instanceof BackpackItem)) return Optional.empty();
		// fromStack loads the server-side storage; fromExistingData only hits a cache
		InventoryHandler handler = BackpackWrapper.fromStack(stack).getInventoryHandler();
		List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < handler.size(); i++) items.add(handler.getStackInSlot(i));
		return Optional.of(new Slots(items, List.of()));
	}
}
*///?}
