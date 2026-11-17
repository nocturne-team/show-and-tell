package com.github.nocturneteam.showandtell.compat;

//? if neoforge {
/*import com.github.nocturneteam.showandtell.chat.SharedContainer.Slots;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosSlotTypes;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

// Only loaded when Curios is installed; callers check first.
public final class CuriosCompat {
	private CuriosCompat() {}

	// Every visible slot in Curios' own order, empty ones with their slot icon
	public static Slots slots(ServerPlayer player) {
		List<ItemStack> items = new ArrayList<>();
		List<Identifier> icons = new ArrayList<>();
		CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
			List<Map.Entry<String, ICurioStacksHandler>> handlers = new ArrayList<>(inventory.getCurios().entrySet());
			handlers.sort(Comparator.comparingInt((Map.Entry<String, ICurioStacksHandler> e) -> order(e.getKey())).thenComparing(Map.Entry::getKey));
			for (Map.Entry<String, ICurioStacksHandler> entry : handlers) {
				ICurioStacksHandler handler = entry.getValue();
				if (!handler.isVisible()) continue;
				ISlotType type = CuriosSlotTypes.getSlotType(entry.getKey());
				Identifier icon = type != null ? type.getIcon() : ISlotType.GENERIC_ICON;
				IDynamicStackHandler stacks = handler.getStacks();
				for (int i = 0; i < stacks.getSlots(); i++) {
					items.add(stacks.getStackInSlot(i));
					icons.add(icon);
				}
			}
		});
		return new Slots(items, icons);
	}

	private static int order(String id) {
		ISlotType type = CuriosSlotTypes.getSlotType(id);
		return type != null ? type.getOrder() : Integer.MAX_VALUE;
	}
}
*///?}
