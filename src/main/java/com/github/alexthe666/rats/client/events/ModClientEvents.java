package com.github.alexthe666.rats.client.events;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.gui.*;
import com.github.alexthe666.rats.client.model.CubeModel;
import com.github.alexthe666.rats.client.model.RatsModelLayers;
import com.github.alexthe666.rats.client.model.deco.RatHammockModel;
import com.github.alexthe666.rats.client.model.deco.RatIglooModel;
import com.github.alexthe666.rats.client.model.deco.RatSeedBowlModel;
import com.github.alexthe666.rats.client.model.deco.RatWaterBottleModel;
import com.github.alexthe666.rats.client.model.entity.*;
import com.github.alexthe666.rats.client.model.hats.*;
import com.github.alexthe666.rats.client.particle.*;
import com.github.alexthe666.rats.client.render.NuggetColorRegister;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsItemProperties;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.client.render.RatsSpawnEggTintSource;
import com.github.alexthe666.rats.client.render.block.*;
import com.github.alexthe666.rats.client.render.entity.*;
import com.github.alexthe666.rats.client.render.entity.layer.PartyHatLayer;
import com.github.alexthe666.rats.client.render.entity.layer.PlagueLayer;
import com.github.alexthe666.rats.registry.*;
import com.github.alexthe666.rats.server.block.entity.RatTubeBlockEntity;
import com.github.alexthe666.rats.server.entity.misc.PiratWoodBoat;
import com.github.alexthe666.rats.server.items.*;
import com.github.alexthe666.rats.server.items.upgrades.DemonRatUpgradeItem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayers;
import com.google.common.reflect.TypeToken;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

//the mod event class mostly stores registry event things, such as registering renderers, item and block colors, shaders, and layer definitions.
//for Forge events, use ForgeClientEvents
@EventBusSubscriber(modid = RatsMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {

	public static boolean shouldRenderNameplates() {
		return Minecraft.getInstance().screen == null || !(Minecraft.getInstance().screen instanceof RatScreen) && !(Minecraft.getInstance().screen instanceof CheeseStaffScreen);
	}

	@Nullable
	public static Level getClientLevel() {
		return Minecraft.getInstance().level;
	}

	public static void openMobFilterScreen(InteractionHand hand) {
		Minecraft.getInstance().setScreen(new MobFilterScreen(hand));
	}

	// 26.1: ItemProperties was replaced by data-driven item model properties (assets/rats/items/*.json);
	// the code-side predicates live in RatsItemProperties and are registered below.
	@SubscribeEvent
	public static void registerRangeProperties(RegisterRangeSelectItemModelPropertyEvent event) {
		event.register(Identifier.fromNamespaceAndPath(RatsMod.MODID, "rat_count"), RatsItemProperties.RatCount.MAP_CODEC);
		event.register(Identifier.fromNamespaceAndPath(RatsMod.MODID, "glint_type"), RatsItemProperties.GlintType.MAP_CODEC);
	}

	@SubscribeEvent
	public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
		event.register(Identifier.fromNamespaceAndPath(RatsMod.MODID, "soul"), RatsItemProperties.DemonSoul.MAP_CODEC);
	}

	// 26.1: builtin/entity item models are gone; block-entity items (rat trap, trash can, auto curdler,
	// rat head, ratlantis portal) render through the rats:block_entity special model type instead.
	@SubscribeEvent
	public static void registerSpecialModelRenderers(net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent event) {
		event.register(Identifier.fromNamespaceAndPath(RatsMod.MODID, "block_entity"), com.github.alexthe666.rats.client.render.RatsBEWLR.Unbaked.MAP_CODEC);
	}

	@SubscribeEvent
	public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Identifier.fromNamespaceAndPath(RatsMod.MODID, "spawn_egg_layer"), RatsSpawnEggTintSource.MAP_CODEC);
		event.register(Identifier.fromNamespaceAndPath(RatsMod.MODID, "nugget"), com.github.alexthe666.rats.client.render.RatsNuggetTintSource.MAP_CODEC);
	}

	// 26.1: Item#initializeClient is gone — armor model/texture extensions register here instead.
	@SubscribeEvent
	public static void registerClientExtensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) {
		java.util.stream.Stream.concat(RatsItemRegistry.ITEMS.getEntries().stream(), RatlantisItemRegistry.ITEMS.getEntries().stream())
				.map(DeferredHolder::get)
				.forEach(item -> {
					if (item instanceof HatItem hat) {
						event.registerItem(new HatItem.ClientExtensions(hat), hat);
					} else if (item instanceof RatlantisArmorItem armor) {
						event.registerItem(new RatlantisArmorItem.ClientExtensions(armor), armor);
					}
				});
	}

	// Citadel-driven models need the live entity during setupAnim; stash it into every render state.
	@SubscribeEvent
	public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
		event.registerEntityModifier(
				new TypeToken<LivingEntityRenderer<? extends LivingEntity, LivingEntityRenderState, ?>>() {},
				(entity, renderState) -> renderState.setRenderData(RatsClientKeys.RENDER_STATE_LIVING_ENTITY, entity));
		event.registerEntityModifier(
				new TypeToken<EntityRenderer<? extends net.minecraft.world.entity.Entity, ? extends EntityRenderState>>() {},
				(entity, renderState) -> renderState.setRenderData(RatsClientKeys.RENDER_STATE_ENTITY, entity));
	}

	@SubscribeEvent
	public static void registerMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
		event.register(RatsMenuRegistry.RAT_CRAFTING_TABLE_CONTAINER.get(), RatCraftingTableScreen::new);
		event.register(RatsMenuRegistry.RAT_UPGRADE_CONTAINER.get(), RatUpgradeScreen::new);
		event.register(RatsMenuRegistry.RAT_UPGRADE_JR_CONTAINER.get(), JuryRiggedRatUpgradeScreen::new);
		event.register(RatsMenuRegistry.UPGRADE_COMBINER_CONTAINER.get(), UpgradeCombinerScreen::new);
		event.register(RatsMenuRegistry.AUTO_CURDLER_CONTAINER.get(), AutoCurdlerScreen::new);
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		for (PiratWoodBoat.Type boatType : PiratWoodBoat.Type.values()) {
			event.registerLayerDefinition(PiratWoodBoatRenderer.createBoatModelName(boatType), BoatModel::createBoatModel);
			event.registerLayerDefinition(PiratWoodBoatRenderer.createChestBoatModelName(boatType), BoatModel::createChestBoatModel);
		}

		event.registerLayerDefinition(RatsModelLayers.BLACK_DEATH, BlackDeathModel::create);
		event.registerLayerDefinition(RatsModelLayers.PIPER, PiedPiperModel::create);
		event.registerLayerDefinition(RatsModelLayers.PIRAT_BOAT, PiratBoatModel::create);
		event.registerLayerDefinition(RatsModelLayers.PLAGUE_DOCTOR, PlagueDoctorModel::create);
		event.registerLayerDefinition(RatsModelLayers.RAT_STRIDER_MOUNT, RatStriderMountModel::create);
		event.registerLayerDefinition(RatsModelLayers.THROWN_BLOCK, CubeModel::create);

		event.registerLayerDefinition(RatsModelLayers.HAMMOCK, RatHammockModel::create);
		event.registerLayerDefinition(RatsModelLayers.IGLOO, RatIglooModel::create);
		event.registerLayerDefinition(RatsModelLayers.SEED_BOWL, RatSeedBowlModel::create);
		event.registerLayerDefinition(RatsModelLayers.WATER_BOTTLE, RatWaterBottleModel::create);

		event.registerLayerDefinition(RatsModelLayers.CHEF_TOQUE, ChefToqueModel::create);
		event.registerLayerDefinition(RatsModelLayers.PIPER_HAT, PiperHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.ARCHEOLOGIST_HAT, ArcheologistHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.FARMER_HAT, FarmerHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.FEZ, RatFezModel::create);
		event.registerLayerDefinition(RatsModelLayers.TOP_HAT, TopHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.SANTA_HAT, SantaHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.HALO, HaloHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.PARTY_HAT, PartyHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.PIRATE_HAT, PiratHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.CROWN, CrownModel::create);
		event.registerLayerDefinition(RatsModelLayers.PLAGUE_DOCTOR_MASK, PlagueDoctorMaskModel::create);
		event.registerLayerDefinition(RatsModelLayers.EXTERMINATOR_HAT, ExterminatorHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.AVIATOR_HAT, AviatorHatModel::create);
		event.registerLayerDefinition(RatsModelLayers.OFFICER_HAT, MilitaryHatModel::create);

		event.registerLayerDefinition(RatsModelLayers.RATLANTIS_ARMOR_OUTER, () -> RatlantisArmorModel.create(LayerDefinitions.OUTER_ARMOR_DEFORMATION));
		event.registerLayerDefinition(RatsModelLayers.RATLANTIS_ARMOR_INNER, () -> RatlantisArmorModel.create(LayerDefinitions.INNER_ARMOR_DEFORMATION));
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(RatsEntityRegistry.RAT.get(), RatRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.TAMED_RAT.get(), TamedRatRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.PIED_PIPER.get(), PiedPiperRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.THROWN_BLOCK.get(), ThrownBlockRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.PLAGUE_DOCTOR.get(), PlagueDoctorRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.PURIFYING_LIQUID.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.BLACK_DEATH.get(), BlackDeathRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.PLAGUE_CLOUD.get(), context -> new RatlateanSpiritRenderer<>(context, true));
		event.registerEntityRenderer(RatsEntityRegistry.PLAGUE_BEAST.get(), PlagueBeastRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.PLAGUE_SHOT.get(), PlagueShotRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.RAT_CAPTURE_NET.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.RAT_DRAGON_FIRE.get(), NothingRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.RAT_ARROW.get(), RatArrowRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.RAT_MOUNT_GOLEM.get(), RatGolemMountRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.RAT_MOUNT_CHICKEN.get(), RatChickenMountRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.RAT_MOUNT_BEAST.get(), RatBeastMountRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.RAT_KING.get(), RatKingRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.RAT_SHOT.get(), RatShotRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.DEMON_RAT.get(), DemonRatRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.RAT_STRIDER_MOUNT.get(), RatStriderMountRenderer::new);
		event.registerEntityRenderer(RatsEntityRegistry.SMALL_ARROW.get(), SmallArrowRenderer::new);

		event.registerBlockEntityRenderer(RatsBlockEntityRegistry.RAT_HOLE.get(), RatHoleRenderer::new);
		event.registerBlockEntityRenderer(RatsBlockEntityRegistry.RAT_TRAP.get(), RatTrapRenderer::new);
		event.registerBlockEntityRenderer(RatsBlockEntityRegistry.AUTO_CURDLER.get(), AutoCurdlerRenderer::new);
		event.registerBlockEntityRenderer(RatsBlockEntityRegistry.RAT_CAGE_DECORATED.get(), DecoratedRatCageRenderer::new);
		event.registerBlockEntityRenderer(RatsBlockEntityRegistry.RAT_CAGE_BREEDING_LANTERN.get(), DecoratedRatCageRenderer::new);
		event.registerBlockEntityRenderer(RatsBlockEntityRegistry.RAT_CAGE_WHEEL.get(), DecoratedRatCageRenderer::new);
		event.registerBlockEntityRenderer(RatsBlockEntityRegistry.UPGRADE_COMBINER.get(), UpgradeCombinerRenderer::new);
		event.registerBlockEntityRenderer(RatsBlockEntityRegistry.UPGRADE_SEPERATOR.get(), UpgradeSeparatorRenderer::new);
		event.registerBlockEntityRenderer(RatsBlockEntityRegistry.TRASH_CAN.get(), TrashCanRenderer::new);


		event.registerEntityRenderer(RatlantisEntityRegistry.DUTCHRAT.get(), DutchratRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.DUTCHRAT_SWORD.get(), DutchratSwordRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RATFISH.get(), RatfishRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RATTLING_GUN.get(), RattlingGunRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RATTLING_GUN_BULLET.get(), RattlingGunBulletRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RATLANTEAN_RATBOT.get(), RatlanteanRatbotRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.PIRAT.get(), PiratRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RAT_MOUNT_AUTOMATON.get(), RatAutomatonMountRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.GHOST_PIRAT.get(), GhostPiratRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RAT_BARON.get(), RatBaronRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RAT_BARON_PLANE.get(), RatBaronPlaneRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RAT_MOUNT_BIPLANE.get(), RatBiplaneMountRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RAT_PROTECTOR.get(), RatProtectorRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RATLANTIS_ARROW.get(), RatlantisArrowRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RATLANTEAN_SPIRIT.get(), context -> new RatlateanSpiritRenderer<>(context, false));
		event.registerEntityRenderer(RatlantisEntityRegistry.RATLANTEAN_FLAME.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RATLANTEAN_AUTOMATON.get(), RatlanteanAutomatonRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.RATLANTEAN_AUTOMATON_BEAM.get(), GolemBeamRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.FERAL_RATLANTEAN.get(), FeralRatlanteanRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.NEO_RATLANTEAN.get(), NeoRatlanteanRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.LASER_BEAM.get(), LaserBeamRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.LASER_PORTAL.get(), LaserPortalRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.VIAL_OF_SENTIENCE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.PIRAT_BOAT.get(), context -> new PiratBoatRenderer(context, new PiratBoatModel(context.bakeLayer(RatsModelLayers.PIRAT_BOAT))));
		event.registerEntityRenderer(RatlantisEntityRegistry.CHEESE_CANNONBALL.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(RatlantisEntityRegistry.BOAT.get(), context -> new PiratWoodBoatRenderer(context, false));
		event.registerEntityRenderer(RatlantisEntityRegistry.CHEST_BOAT.get(), context -> new PiratWoodBoatRenderer(context, true));

		event.registerBlockEntityRenderer(RatlantisBlockEntityRegistry.RATLANTIS_PORTAL.get(), RatlantisPortalRenderer::new);
		event.registerBlockEntityRenderer(RatlantisBlockEntityRegistry.DUTCHRAT_BELL.get(), DutchratBellRenderer::new);
		event.registerBlockEntityRenderer(RatlantisBlockEntityRegistry.AUTOMATON_HEAD.get(), RatlanteanAutomatonHeadRenderer::new);
		event.registerBlockEntityRenderer(RatlantisBlockEntityRegistry.TOKEN.get(), RatlantisTokenRenderer::new);
		event.registerBlockEntityRenderer(RatlantisBlockEntityRegistry.PIRAT_SIGN.get(), PiratSignRenderer::new);
		event.registerBlockEntityRenderer(RatlantisBlockEntityRegistry.PIRAT_HANGING_SIGN.get(), PiratHangingSignRenderer::new);
	}

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(RatsParticleRegistry.BLACK_DEATH.get(), BlackDeathParticle.Provider::new);
		event.registerSpriteSet(RatsParticleRegistry.DUTCHRAT_SMOKE.get(), DutchratSmokeParticle.Provider::new);
		event.registerSpriteSet(RatsParticleRegistry.FLEA.get(), FleaParticle.Provider::new);
		event.registerSpriteSet(RatsParticleRegistry.FLY.get(), FlyParticle.Provider::new);
		event.registerSpriteSet(RatsParticleRegistry.LIGHTNING.get(), LightningParticle.Provider::new);
		event.registerSpriteSet(RatsParticleRegistry.MILK_BUBBLE.get(), MilkBubbleParticle.Provider::new);
		event.registerSpriteSet(RatsParticleRegistry.PIRAT_GHOST.get(), PiratGhostParticle.Provider::new);
		event.registerSpriteSet(RatsParticleRegistry.RAT_GHOST.get(), RatGhostParticle.Provider::new);
		event.registerSpriteSet(RatsParticleRegistry.RAT_KING_SMOKE.get(), RatKingSmokeParticle.Provider::new);
		event.registerSpecial(RatsParticleRegistry.RUNNING_RAT.get(), new RunningRatParticle.Provider());
		event.registerSpriteSet(RatsParticleRegistry.SALIVA.get(), SalivaParticle.Provider::new);
		event.registerSpriteSet(RatsParticleRegistry.UPGRADE_COMBINER.get(), UpgradeCombinerParticle.Provider::new);
	}

	// 26.1: block color handlers register BlockTintSource lists per block.
	@SubscribeEvent
	public static void onBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
		// 26.1: block tints multiply as full ARGB (QuadInstance.multiplyColor) — an alpha-less color
		// like DyeColor.getFireworkColor() zeroes the quad alpha, turning translucent tubes invisible.
		event.register(java.util.List.of(new net.minecraft.client.color.block.BlockTintSource() {
			@Override
			public int color(net.minecraft.world.level.block.state.BlockState state) {
				return net.minecraft.util.ARGB.opaque(DyeColor.WHITE.getFireworkColor());
			}

			@Override
			public int colorInWorld(net.minecraft.world.level.block.state.BlockState state, net.minecraft.client.renderer.block.BlockAndTintGetter level, net.minecraft.core.BlockPos pos) {
				int meta = 0;
				if (level != null && pos != null && level.getBlockEntity(pos) instanceof RatTubeBlockEntity tube) {
					meta = tube.getColor();
				}
				return net.minecraft.util.ARGB.opaque(DyeColor.byId(meta).getFireworkColor());
			}
		}), RatsBlockRegistry.RAT_TUBE_COLOR.get());

		event.register(java.util.List.of(new net.minecraft.client.color.block.BlockTintSource() {
			@Override
			public int color(net.minecraft.world.level.block.state.BlockState state) {
				return net.minecraft.util.ARGB.opaque(FoliageColor.get(0.5D, 1.0D));
			}

			@Override
			public int colorInWorld(net.minecraft.world.level.block.state.BlockState state, net.minecraft.client.renderer.block.BlockAndTintGetter level, net.minecraft.core.BlockPos pos) {
				return net.minecraft.util.ARGB.opaque(level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.get(0.5D, 1.0D));
			}
		}), RatlantisBlockRegistry.MARBLED_CHEESE_GRASS.get());
	}

	// 26.1: per-item color handlers are data-driven now — dyed deco items (tubes/igloos/hammocks)
	// get constant tints in assets/rats/items/*.json, marbled grass items use minecraft:grass, and
	// the ore nugget uses the rats:nugget item tint source registered in registerItemTintSources.

	private static int invertColor(int color) {
		int a = (color >> 24) & 0xff;
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;

		r = 255 - r;
		g = 255 - g;
		b = 255 - b;
		return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
	}

	@SubscribeEvent
	public static void attachRenderLayers(EntityRenderersEvent.AddLayers event) {
		// 26.1: player renderers are AvatarRenderers keyed by PlayerModelType; other renderers via getRenderer.
		event.getSkins().forEach(skin -> {
			var renderer = event.getPlayerRenderer(skin);
			if (renderer != null) {
				attachRenderLayers(renderer);
			}
		});
		for (EntityType<?> type : RatsEntityRegistry.ENTITIES.getEntries().stream().map(DeferredHolder::get).toList()) {
			attachToType(event, type);
		}
		// vanilla mobs also get the plague layer (matches 1.21.1 behavior of decorating every living renderer)
		for (EntityType<?> type : net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE) {
			attachToType(event, type);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void attachToType(EntityRenderersEvent.AddLayers event, EntityType<?> type) {
		var renderer = event.getRenderer((EntityType) type);
		if (renderer instanceof LivingEntityRenderer living) {
			attachRenderLayers(living);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void attachRenderLayers(LivingEntityRenderer renderer) {
		renderer.addLayer(new PlagueLayer<>(renderer));
		if (renderer.getModel() instanceof HumanoidModel<?>) {
			renderer.addLayer(new PartyHatLayer<>(renderer, new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_ARMOR.head()))));
		}
	}
}
