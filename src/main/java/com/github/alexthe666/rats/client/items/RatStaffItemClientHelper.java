package com.github.alexthe666.rats.client.items;

import com.github.alexthe666.rats.server.capability.SelectedRat;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

// Split out of RatStaffItem so the item class stays loadable on a dedicated server.
@OnlyIn(Dist.CLIENT)
public class RatStaffItemClientHelper {

	public static void appendStaffTooltip(List<Component> tooltip) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.player != null) {
			TamedRat rat = SelectedRat.get(mc.player);
			if (rat != null) {
				tooltip.add(Component.translatable("item.rats.cheese_staff.bound_rat", rat.getDisplayName(), rat.getUUID().toString()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
			}
		}
	}
}
