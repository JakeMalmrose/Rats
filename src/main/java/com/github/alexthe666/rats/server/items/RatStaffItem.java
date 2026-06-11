package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.client.items.RatStaffItemClientHelper;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;

public class RatStaffItem extends LoreTagItem {
	public RatStaffItem(Properties properties) {
		super(properties, 2, false);
	}

	public int getStaff(ItemStack stack) {
		if (stack.is(RatsItemRegistry.PATROL_STICK.get())) return 2;
		if (stack.is(RatsItemRegistry.RADIUS_STICK.get())) return 1;
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		// 1.21: Item.appendHoverText no longer receives Level. Tooltips only render on the client,
		// but this method is also reachable on a dedicated server, so the Minecraft-touching logic
		// lives in a client-only helper behind a dist check.
		if (FMLEnvironment.dist == Dist.CLIENT) {
			RatStaffItemClientHelper.appendStaffTooltip(tooltip);
		}
	}
}
