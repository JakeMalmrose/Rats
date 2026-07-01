package com.github.alexthe666.rats.server.items.upgrades.interfaces;

import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.function.Consumer;

public interface CombinedUpgrade {

	int getUpgradeSlots();

	// 26.1: ContainerHelper.loadAllItems now reads from a ValueInput; bridge the stack's CustomData tag through TagValueInput.
	static NonNullList<ItemStack> loadUpgrades(CompoundTag tag, int slots) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(slots, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), tag), nonnulllist);
		return nonnulllist;
	}

	default void addTooltip(ItemStack stack, Consumer<Component> tooltip) {
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

		if (tag.contains("Items")) {
			NonNullList<ItemStack> nonnulllist = loadUpgrades(tag, this.getUpgradeSlots());
			int i = 0;
			for (ItemStack itemstack : nonnulllist) {
				if (!itemstack.isEmpty()) {
					if (i <= 4) {
						++i;
						tooltip.accept(Component.literal(String.format("%s", itemstack.getDisplayName().getString())));
					} else {
						break;
					}
				}
			}
			if (nonnulllist.stream().filter(stack1 -> !stack1.isEmpty()).toList().size() > 5) {
				tooltip.accept(Component.translatable(RatsLangConstants.AND_MORE, nonnulllist.stream().filter(stack1 -> !stack1.isEmpty()).toList().size() - 5).withStyle(ChatFormatting.GRAY));
			}
		}
	}
}
