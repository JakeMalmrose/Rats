package com.github.alexthe666.rats.client.registry;

import com.github.alexthe666.rats.server.items.OreRatNuggetItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Split out of RatsCreativeTabRegistry so the registry class stays loadable on a dedicated server.
public class CreativeTabClientHelper {

	@Nullable
	public static Level getClientLevel() {
		return Minecraft.getInstance().level;
	}

	public static List<ItemStack> getOreNuggets(Level level) {
		List<ItemStack> uniqueOres = new ArrayList<>();
		if (level != null) {
			for (Item item : BuiltInRegistries.ITEM.listElements().filter(holder -> holder.is(Tags.Items.ORES)).map(Holder::value).toList()) {
				ItemStack oreDrop = OreRatNuggetItem.getIngot(level, new ItemStack(item));
				if (!uniqueOres.contains(oreDrop) && !oreDrop.isEmpty()) {
					uniqueOres.add(oreDrop);
				}
			}
			uniqueOres.sort(Comparator.comparing(stack -> stack.getDisplayName().getString()));
		}
		return uniqueOres;
	}
}
