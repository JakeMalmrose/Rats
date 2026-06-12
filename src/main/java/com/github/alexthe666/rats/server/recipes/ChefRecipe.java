package com.github.alexthe666.rats.server.recipes;

import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.registry.RatsRecipeRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class ChefRecipe extends BaseRatRecipe {
	public ChefRecipe(String group, Ingredient input, ItemStackTemplate output) {
		super(group, input, output);
	}

	@Override
	public RecipeType<ChefRecipe> getType() {
		return RatsRecipeRegistry.CHEF.get();
	}

	@Override
	public RecipeSerializer<ChefRecipe> getSerializer() {
		return RatsRecipeRegistry.CHEF_SERIALIZER.get();
	}

	// 26.1: getToastSymbol no longer exists on Recipe; kept for mod UI use (no @Override).
	public ItemStack getToastSymbol() {
		return new ItemStack(RatsItemRegistry.RAT_UPGRADE_CHEF.get());
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		return this.getInputIngredient().test(input.item());
	}

	// Never shown in the vanilla recipe book (special recipe); category is required by the interface.
	@Override
	public RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CRAFTING_MISC;
	}
}
