package com.github.alexthe666.rats.registry.worldgen;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatlantisEntityRegistry;
import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RatlantisBiomeRegistry {

	//new biome ideas:
	//make the ocean its own biome
	//ratglove fields. No trees, barely any ruins, just ratglove flowers and maybe even compressed rat blocks. Ratlanteans also spawn in abundance here
	//sparser jungle biome
	//ratlantis plains, spawns ruins more frequently. Maybe could also spawn an ancient city-like structure?
	//marble mushroom biome
	public static final ResourceKey<Biome> RATLANTIS = makeKey("ratlantis");

	private static ResourceKey<Biome> makeKey(String name) {
		return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(RatsMod.MODID, name));
	}

	public static void bootstrap(BootstrapContext<Biome> context) {
		HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
		HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

		context.register(RatlantisBiomeRegistry.RATLANTIS, new Biome.BiomeBuilder()
				.hasPrecipitation(true)
				.downfall(0.5F)
				.temperature(0.55F)
				.mobSpawnSettings(new MobSpawnSettings.Builder()
						.creatureGenerationProbability(0.4F)
						.addSpawn(MobCategory.MONSTER, 20, new MobSpawnSettings.SpawnerData(RatlantisEntityRegistry.FERAL_RATLANTEAN.get(), 1, 3))
						.addSpawn(MobCategory.MONSTER, 50, new MobSpawnSettings.SpawnerData(RatlantisEntityRegistry.PIRAT.get(), 1, 2))
						.addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(RatlantisEntityRegistry.RATLANTEAN_SPIRIT.get(), 1, 1))
						.addSpawn(MobCategory.MONSTER, 5, new MobSpawnSettings.SpawnerData(RatlantisEntityRegistry.RATLANTEAN_RATBOT.get(), 1, 1))
						.addSpawn(MobCategory.CREATURE, 40, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 1, 2))
						.addSpawn(MobCategory.CREATURE, 14, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 2))
						.addSpawn(MobCategory.WATER_AMBIENT, 15, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 1, 3))
						.addSpawn(MobCategory.WATER_AMBIENT, 45, new MobSpawnSettings.SpawnerData(RatlantisEntityRegistry.RATFISH.get(), 8, 8))
						.addSpawn(MobCategory.WATER_CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 2))
						.addSpawn(RatsMod.RATS, 10, new MobSpawnSettings.SpawnerData(RatsEntityRegistry.RAT.get(), 2, 4))
						.build())
				.generationSettings(new BiomeGenerationSettings.Builder(features, carvers)
						.addCarver(RatlantisDimensionRegistry.RATLANTIS_CAVES)
						.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_WATER)
						.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_LAVA)
						.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, RatlantisPlacedFeatureRegistry.SMALL_RUINS)
						.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, RatlantisPlacedFeatureRegistry.LARGE_RUINS)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, CavePlacements.MONSTER_ROOM)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIRT)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRAVEL)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GRANITE_UPPER)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIORITE_UPPER)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_ANDESITE_UPPER)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COAL_UPPER)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_MIDDLE)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD_LOWER)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_REDSTONE)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_LARGE)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_LAPIS)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_SAND)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_GRAVEL)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RatlantisPlacedFeatureRegistry.CHEESE_ORE)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RatlantisPlacedFeatureRegistry.GEM_ORE)
						.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RatlantisPlacedFeatureRegistry.ORATCHALCUM_ORE)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RatlantisPlacedFeatureRegistry.RATGLOVE_FLOWERS)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RatlantisPlacedFeatureRegistry.MARBLE_PILE)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BAMBOO_LIGHT)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_JUNGLE)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_WARM)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.VINES)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_MELON)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.WARM_OCEAN_VEGETATION)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_WARM)
						.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE)
						.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.FREEZE_TOP_LAYER)
						.build())
				.specialEffects(new BiomeSpecialEffects.Builder()
						// 26.1: sky color and ambient mood moved to environment attributes (biome JSON `attributes`)
						.foliageColorOverride(2546944)
						.grassColorOverride(8177920)
						.waterColor(4445678)
						.build())
				.build()
		);
	}

	public static BiomeSource buildBiomeSource(HolderGetter<Biome> biomes) {
		return MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<>(ImmutableList.of(
				Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), biomes.getOrThrow(RATLANTIS))
		)));
	}
}
