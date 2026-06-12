package com.github.alexthe666.rats.registry;

import net.minecraft.util.random.Weighted;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.data.tags.RatsBiomeTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RatsBiomeModifierRegistry {
	private static final ResourceKey<BiomeModifier> ADD_RAT_SPAWNS = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(RatsMod.MODID, "add_rat_spawns"));
	private static final ResourceKey<BiomeModifier> ADD_PIPER_SPAWNS = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(RatsMod.MODID, "add_piper_spawns"));
	private static final ResourceKey<BiomeModifier> ADD_DEMON_RAT_SPAWNS = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(RatsMod.MODID, "add_demon_rat_spawns"));

	public static void bootstrap(BootstrapContext<BiomeModifier> context) {
		context.register(ADD_RAT_SPAWNS,
				BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
						context.lookup(Registries.BIOME).getOrThrow(RatsBiomeTags.RAT_SPAWN_BIOMES),
						new Weighted<>(new MobSpawnSettings.SpawnerData(RatsEntityRegistry.RAT.get(), 1, 3), 80)));

		context.register(ADD_PIPER_SPAWNS,
				BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
						context.lookup(Registries.BIOME).getOrThrow(RatsBiomeTags.PIPER_SPAWN_BIOMES),
						new Weighted<>(new MobSpawnSettings.SpawnerData(RatsEntityRegistry.PIED_PIPER.get(), 1, 1), 25)));

		context.register(ADD_DEMON_RAT_SPAWNS,
				BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
						context.lookup(Registries.BIOME).getOrThrow(RatsBiomeTags.DEMON_RAT_SPAWN_BIOMES),
						new Weighted<>(new MobSpawnSettings.SpawnerData(RatsEntityRegistry.DEMON_RAT.get(), 1, 1), 15)));
	}
}
