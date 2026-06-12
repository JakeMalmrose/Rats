package com.github.alexthe666.rats.server.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;

// 26.1: SingleItemRecipe now stores (CommonInfo, Ingredient, ItemStackTemplate); type/serializer are
// abstract getters and group moved out of the base class, so we keep group here to preserve the old field.
public abstract class BaseRatRecipe extends SingleItemRecipe {
	private final String group;

	public BaseRatRecipe(String group, Ingredient input, ItemStackTemplate output) {
		super(new Recipe.CommonInfo(true), input, output);
		this.group = group;
	}

	@Override
	public String group() {
		return this.group;
	}

	public final ItemStack getResult() {
		return this.result().create();
	}

	public final ItemStackTemplate getResultTemplate() {
		return this.result();
	}

	public final Ingredient getInputIngredient() {
		return this.input();
	}
}
