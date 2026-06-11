package com.github.alexthe666.rats.client.render;

import com.github.alexthe666.rats.server.items.RatsSpawnEggItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Registered as rats:spawn_egg_layer in ModClientEvents; referenced by assets/rats/items/<egg>.json tints. */
public record RatsSpawnEggTintSource(int layer) implements ItemTintSource {

	public static final MapCodec<RatsSpawnEggTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(Codec.INT.fieldOf("layer").forGetter(RatsSpawnEggTintSource::layer))
			.apply(instance, RatsSpawnEggTintSource::new));

	@Override
	public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
		if (stack.getItem() instanceof RatsSpawnEggItem egg) {
			return egg.getTintColor(this.layer);
		}
		return -1;
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return MAP_CODEC;
	}
}
