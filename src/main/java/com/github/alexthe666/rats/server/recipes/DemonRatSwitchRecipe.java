package com.github.alexthe666.rats.server.recipes;

import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.items.upgrades.DemonRatUpgradeItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

// 26.1: CustomRecipe is stateless (no CraftingBookCategory ctor); serializer is a unit-codec record.
public class DemonRatSwitchRecipe extends CustomRecipe {
	public static final DemonRatSwitchRecipe INSTANCE = new DemonRatSwitchRecipe();
	public static final MapCodec<DemonRatSwitchRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, DemonRatSwitchRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	public static final RecipeSerializer<DemonRatSwitchRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack upgrade = null;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (upgrade != null) {
					return false;
				} else {
					if (stack.is(RatsItemRegistry.RAT_UPGRADE_DEMON.get())) {
						upgrade = stack;
					} else {
						return false;
					}
				}
			}
		}
		return upgrade != null;
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty() && stack.is(RatsItemRegistry.RAT_UPGRADE_DEMON.get())) {
				return DemonRatUpgradeItem.getDemonUpgrade(!DemonRatUpgradeItem.isSoulVersion(stack));
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<DemonRatSwitchRecipe> getSerializer() {
		return SERIALIZER;
	}
}
