package com.github.alexthe666.rats.server.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

// 26.1: RecipeSerializer is a final record (MapCodec, StreamCodec) — this is now a factory for the
// shared Rats single-item recipe schema (group + ingredient + result) instead of an implementation.
public final class RatsRecipeSerializer {

	private RatsRecipeSerializer() {
	}

	public static <T extends BaseRatRecipe> RecipeSerializer<T> create(SingleItemMaker<T> factory) {
		MapCodec<T> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(BaseRatRecipe::group),
				Ingredient.CODEC.fieldOf("ingredient").forGetter(BaseRatRecipe::getInputIngredient),
				ItemStackTemplate.CODEC.fieldOf("result").forGetter(BaseRatRecipe::getResultTemplate)
		).apply(instance, factory::create));
		StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, BaseRatRecipe::group,
				Ingredient.CONTENTS_STREAM_CODEC, BaseRatRecipe::getInputIngredient,
				ItemStackTemplate.STREAM_CODEC, BaseRatRecipe::getResultTemplate,
				factory::create
		);
		return new RecipeSerializer<>(codec, streamCodec);
	}

	public interface SingleItemMaker<T extends BaseRatRecipe> {
		T create(String group, Ingredient input, ItemStackTemplate output);
	}
}
