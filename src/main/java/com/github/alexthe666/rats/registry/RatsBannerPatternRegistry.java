package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;

// Banner patterns became a datapack registry in 1.20.5+; entries live in
// data/rats/banner_pattern/*.json and code only refers to them by key.
public class RatsBannerPatternRegistry {

	public static final ResourceKey<BannerPattern> RAT_PATTERN = key("rat");
	public static final ResourceKey<BannerPattern> CHEESE_PATTERN = key("cheese");
	public static final ResourceKey<BannerPattern> RAT_AND_CROSSBONES_BANNER = key("rat_and_crossbones");
	public static final ResourceKey<BannerPattern> RAT_AND_SICKLE_BANNER = key("rat_and_sickle");

	private static ResourceKey<BannerPattern> key(String name) {
		return ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, name));
	}
}
