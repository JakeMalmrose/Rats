package com.github.alexthe666.rats.registry.worldgen;

import com.github.alexthe666.rats.RatsMod;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TimelineTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class RatlantisDimensionRegistry {
	public static final Identifier DIMENSION = Identifier.fromNamespaceAndPath(RatsMod.MODID, "ratlantis");
	public static final ResourceKey<Level> DIMENSION_KEY = ResourceKey.create(Registries.DIMENSION, DIMENSION);

	public static final ResourceKey<ConfiguredWorldCarver<?>> RATLANTIS_CAVES = ResourceKey.create(Registries.CONFIGURED_CARVER, Identifier.fromNamespaceAndPath(RatsMod.MODID, "ratlantis_caves"));
	public static final ResourceKey<NoiseGeneratorSettings> RATLANTIS_NOISE_GEN = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.fromNamespaceAndPath(RatsMod.MODID, "ratlantis_noise_gen"));
	public static final ResourceKey<DimensionType> RATLANTIS_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath(RatsMod.MODID, "ratlantis_type"));
	public static final ResourceKey<LevelStem> RATLANTIS_LEVEL_STEM = ResourceKey.create(Registries.LEVEL_STEM, DIMENSION);

	// 26.1: DimensionType gained skybox/cardinal lighting/environment attributes/timelines/clocks;
	// values mirror the vanilla overworld bootstrap with Ratlantis' old flags (natural, bed works,
	// 0..256 world height, no fixed time).
	private static DimensionType ratlantisType(HolderGetter<Timeline> timelines, HolderGetter<WorldClock> clocks) {
		EnvironmentAttributeMap attributes = EnvironmentAttributeMap.builder()
				.set(EnvironmentAttributes.FOG_COLOR, -4138753)
				.set(EnvironmentAttributes.SKY_COLOR, OverworldBiomes.calculateSkyColor(0.8F))
				.set(EnvironmentAttributes.AMBIENT_LIGHT_COLOR, -16119286)
				.set(EnvironmentAttributes.CLOUD_COLOR, ARGB.white(0.8F))
				.set(EnvironmentAttributes.CLOUD_HEIGHT, 192.33F)
				.set(EnvironmentAttributes.BED_RULE, BedRule.CAN_SLEEP_WHEN_DARK)
				.set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
				.set(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLINS, false)
				.set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
				.build();
		return new DimensionType(
				false, //ultrawarm
				true, //natural
				false, //piglin safe
				false, //has raids? (original had raids enabled via natural flag; keep overworld-like false->true)
				1.0D, //coordinate scale
				0, //min y
				256, //height
				256, //logical height
				BlockTags.INFINIBURN_OVERWORLD,
				0.0F, //ambient light
				new DimensionType.MonsterSettings(UniformInt.of(0, 7), 7),
				DimensionType.Skybox.OVERWORLD,
				CardinalLighting.Type.DEFAULT,
				attributes,
				timelines.getOrThrow(TimelineTags.IN_OVERWORLD),
				Optional.of(clocks.getOrThrow(WorldClocks.OVERWORLD))
		);
	}

	public static NoiseGeneratorSettings ratlantisNoise(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noises) {
		DensityFunction densityfunction = NoiseRouterData.getFunction(functions, NoiseRouterData.SHIFT_X);
		DensityFunction densityfunction1 = NoiseRouterData.getFunction(functions, NoiseRouterData.SHIFT_Z);
		return new NoiseGeneratorSettings(
				NoiseSettings.create(0, 256, 1, 2),
				Blocks.STONE.defaultBlockState(),
				Blocks.WATER.defaultBlockState(),
				new NoiseRouter(
						DensityFunctions.zero(), //barrier
						DensityFunctions.zero(), //fluid level floodedness
						DensityFunctions.zero(), //fluid level spread
						DensityFunctions.zero(), //lava
						DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, noises.getOrThrow(Noises.TEMPERATURE)), //temperature
						DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, noises.getOrThrow(Noises.VEGETATION)), //vegetation
						DensityFunctions.zero(), //continents
						DensityFunctions.cache2d(DensityFunctions.endIslands(0L)), //erosion
						DensityFunctions.zero(), //depth
						DensityFunctions.zero(), //ridges
						DensityFunctions.add(
								DensityFunctions.constant(-0.234275D),
								DensityFunctions.mul(
										DensityFunctions.yClampedGradient(16, 32, 0.0D, 1.0D),
										DensityFunctions.add(
												DensityFunctions.constant(0.234375D),
												DensityFunctions.add(
														DensityFunctions.constant(-23.4375D),
														DensityFunctions.mul(
																DensityFunctions.yClampedGradient(0, 312, 1.0D, 0.0D),
																DensityFunctions.add(
																		DensityFunctions.constant(23.4375D),
																		DensityFunctions.add(
																				DensityFunctions.constant(-0.703125D),
																				DensityFunctions.cache2d(DensityFunctions.endIslands(0L))
																		)
																)
														)
												))
								)
						), //initial density
						DensityFunctions.mul(
								DensityFunctions.constant(0.64D),
								DensityFunctions.interpolated(
										DensityFunctions.blendDensity(
												DensityFunctions.add(
														DensityFunctions.constant(0.554375D),
														DensityFunctions.mul(
																DensityFunctions.yClampedGradient(0, 87, 0.25D, 3.6D),
																DensityFunctions.add(
																		DensityFunctions.constant(1.234375D),
																		DensityFunctions.add(
																				DensityFunctions.constant(-2.75D),
																				DensityFunctions.mul(
																						DensityFunctions.yClampedGradient(0, 175, 1.0D, 0.0D),
																						DensityFunctions.add(
																								DensityFunctions.constant(1.75D),
																								new DensityFunctions.HolderHolder(functions.getOrThrow(NoiseRouterData.SLOPED_CHEESE_AMPLIFIED))
																						)
																				)
																		)
																)
														)
												)
										)
								)
						).squeeze(), //final density
						DensityFunctions.zero(), //vein toggle
						DensityFunctions.zero(), //vein ridged
						DensityFunctions.zero() //vein gap
				),
				createSurfaceRules(),
				List.of(),
				63,
				false,
				true,
				false,
				false
		);
	}

	public static void bootstrapNoise(BootstrapContext<NoiseGeneratorSettings> context) {
		context.register(RATLANTIS_NOISE_GEN, ratlantisNoise(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE)));
	}

	public static void bootstrapType(BootstrapContext<DimensionType> context) {
		context.register(RATLANTIS_DIM_TYPE, ratlantisType(context.lookup(Registries.TIMELINE), context.lookup(Registries.WORLD_CLOCK)));
	}

	public static void bootstrapCarver(BootstrapContext<ConfiguredWorldCarver<?>> context) {
		context.register(RATLANTIS_CAVES, RatlantisFeatureRegistry.RATLANTIS_CAVES.get().configured(new CaveCarverConfiguration(
				0.15F,
				UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
				UniformFloat.of(0.1F, 0.9F),
				VerticalAnchor.bottom(), //no lava
				CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
				context.lookup(Registries.BLOCK).getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
				UniformFloat.of(0.7F, 1.4F),
				UniformFloat.of(0.8F, 1.3F),
				UniformFloat.of(-1.0F, -0.4F))));
	}

	public static void bootstrapLevelStem(BootstrapContext<LevelStem> context) {
		HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
		HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
		HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);
		context.register(RATLANTIS_LEVEL_STEM, new LevelStem(dimTypes.getOrThrow(RatlantisDimensionRegistry.RATLANTIS_DIM_TYPE), new NoiseBasedChunkGenerator(RatlantisBiomeRegistry.buildBiomeSource(biomeRegistry), noiseGenSettings.getOrThrow(RatlantisDimensionRegistry.RATLANTIS_NOISE_GEN))));
	}

	private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
	private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
	private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
	private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
	private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);

	private static SurfaceRules.RuleSource makeStateRule(Block block) {
		return SurfaceRules.state(block.defaultBlockState());
	}

	private static SurfaceRules.RuleSource createSurfaceRules() {
		SurfaceRules.RuleSource overworldLike = SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
						SurfaceRules.sequence(
								SurfaceRules.sequence(
										SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE),
										SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), GRASS_BLOCK), SAND),
								SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
										SurfaceRules.sequence(GRASS_BLOCK)))),
				SurfaceRules.ifTrue(SurfaceRules.waterStartCheck(-6, -1),
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT))));

		ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();

		builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
		builder.add(overworldLike);
		return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
	}
}
