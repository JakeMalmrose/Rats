package com.github.alexthe666.rats.data.tags;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.Nullable;


// 26.1: stripped to a TagKey constant holder; the datagen provider half targeted the
// removed 1.21 datagen API and the shipped JSON under src/generated/resources is canonical.
public class RatsBiomeTags {

	public static final TagKey<Biome> RAT_SPAWN_BIOMES = create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "rat_spawn_biomes"));
	public static final TagKey<Biome> PIPER_SPAWN_BIOMES = create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "piper_spawn_biomes"));
	public static final TagKey<Biome> DEMON_RAT_SPAWN_BIOMES = create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "demon_rat_spawn_biomes"));

	private static TagKey<Biome> create(Identifier id) {
		return TagKey.create(Registries.BIOME, id);
	}
}
