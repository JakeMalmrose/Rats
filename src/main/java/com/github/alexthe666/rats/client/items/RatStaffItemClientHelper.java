package com.github.alexthe666.rats.client.items;

import com.github.alexthe666.rats.server.capability.SelectedRat;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

// Split out of RatStaffItem so the item class stays loadable on a dedicated server.
public class RatStaffItemClientHelper {

	public static void appendStaffTooltip(Consumer<Component> tooltip) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.player != null) {
			TamedRat rat = SelectedRat.get(mc.player);
			if (rat != null) {
				tooltip.accept(Component.translatable("item.rats.cheese_staff.bound_rat", rat.getDisplayName(), rat.getUUID().toString()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
			}
		}
	}
}
