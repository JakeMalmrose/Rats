package com.github.alexthe666.rats.data.ratlantis.tags;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.worldgen.RatlantisBiomeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;


// 26.1: stripped to a TagKey constant holder; the datagen provider half targeted the
// removed 1.21 datagen API and the shipped JSON under src/generated/resources is canonical.
public class RatlantisBiomeTags {

	public static final TagKey<Biome> DUTCHRAT_SHIP_SPAWNS = create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "has_structure/dutchrat_ship"));
	public static final TagKey<Biome> BARON_RUNWAY_SPAWNS = create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "has_structure/baron_runway"));

}
