package com.github.alexthe666.rats.client.render;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Registered as rats:nugget; tints the ore-rat-nugget overlay layer by the stored resource. */
public record RatsNuggetTintSource() implements ItemTintSource {

	public static final MapCodec<RatsNuggetTintSource> MAP_CODEC = MapCodec.unit(new RatsNuggetTintSource());

	@Override
	public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
		return 0xFF000000 | NuggetColorRegister.getNuggetColor(stack);
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return MAP_CODEC;
	}
}
