package com.github.alexthe666.rats.client.events;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.StaticRatModel;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.client.render.entity.RatProtectorRenderer;
import com.github.alexthe666.rats.client.util.RatsIconRenderUtil;
import com.github.alexthe666.rats.registry.*;
import com.github.alexthe666.rats.registry.worldgen.RatlantisDimensionRegistry;
import com.github.alexthe666.rats.server.block.entity.RatQuarryBlockEntity;
import com.github.alexthe666.rats.server.capability.SelectedRat;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.events.ForgeEvents;
import com.github.alexthe666.rats.server.items.RatStaffItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Matrix4f;

import java.util.Objects;

@EventBusSubscriber(modid = RatsMod.MODID, value = Dist.CLIENT)
public class ForgeClientEvents {

	public static final Identifier PLAGUE_HEART_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/gui/plague_hearts.png");
	private static final Identifier RADIUS_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/misc/rat_radius.png");
	private static final Identifier QUARRY_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/misc/quarry_radius.png");
	private static final Identifier HOME_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/misc/rat_home.png");
	private static final Identifier RAT_DEPOSIT_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/misc/rat_deposit.png");
	private static final Identifier RAT_PICKUP_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/misc/rat_pickup.png");
	private static final Identifier RAT_PATROL_NODE_TEXTURE = Identifier.parse("rats:textures/misc/rat_patrol.png");
	// 26.1: post chains live at assets/<ns>/post_effect/<id>.json and are set by plain id.
	private static final Identifier SYNESTHESIA = Identifier.fromNamespaceAndPath(RatsMod.MODID, "synesthesia");
	private static float synesthesiaProgress = 0;
	private static float prevSynesthesiaProgress = 0;
	private static final float MAX_SYNESTESIA = 40;
	private static final StaticRatModel<LivingEntity> RAT_MODEL = new StaticRatModel<>();

	@SubscribeEvent
	public static void adjustSynesthesiaFOV(ViewportEvent.ComputeFov event) {
		if (RatConfig.synesthesiaShader) {
			if (prevSynesthesiaProgress > 0) {
				float prog = (prevSynesthesiaProgress + (synesthesiaProgress - prevSynesthesiaProgress) * Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
				float renderProg;
				if (prevSynesthesiaProgress <= synesthesiaProgress) {
					renderProg = (float) Math.sin(prog / MAX_SYNESTESIA * Math.PI) * 40.0F;
				} else {
					renderProg = -(float) Math.sin(prog / MAX_SYNESTESIA * Math.PI) * 40.0F;
				}
				event.setFOV(event.getFOV() + (float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, renderProg));
			}
		}
	}

	@SubscribeEvent
	public static void changeRatlantisBowFov(ComputeFovModifierEvent event) {
		Player player = event.getPlayer();
		if (player.isUsingItem()) {
			if (player.getUseItem().is(RatlantisItemRegistry.RATLANTIS_BOW.get())) {
				float f = player.getTicksUsingItem() / 10.0F;
				f = f > 1.0F ? 1.0F : f * f;
				event.setNewFovModifier((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, (event.getFovModifier() * (1.0F - f * 0.15F))));
			}
		}
	}

	// 26.1: RenderHighlightEvent.Block was replaced by the extraction-phase outline event.
	@SubscribeEvent
	public static void removeAutomatonHeadOutline(ExtractBlockOutlineRenderStateEvent event) {
		if (event.getBlockState().is(RatlantisBlockRegistry.MARBLED_CHEESE_RAT_HEAD.get())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingUpdate(EntityTickEvent.Post event) {
		if (!(event.getEntity() instanceof net.minecraft.world.entity.LivingEntity)) return;
		if (RatConfig.synesthesiaShader) {
			if (event.getEntity() == Minecraft.getInstance().player) {
				GameRenderer renderer = Minecraft.getInstance().gameRenderer;
				boolean active = ((net.minecraft.world.entity.LivingEntity) event.getEntity()).hasEffect(RatsEffectRegistry.SYNESTHESIA);
				try {
					if (active && renderer.currentPostEffect() == null) {
						renderer.setPostEffect(SYNESTHESIA);
					}
					if (!active && SYNESTHESIA.equals(renderer.currentPostEffect())) {
						renderer.clearPostEffect();
					}
				} catch (Exception e) {
					RatsMod.LOGGER.warn("Game tried to crash when applying shader");
				}

				if (prevSynesthesiaProgress == 2 && active) {
					event.getEntity().level().playLocalSound(event.getEntity().blockPosition(), RatsSoundRegistry.POTION_EFFECT_BEGIN.get(), SoundSource.NEUTRAL, 16.0F, 1.0F, false);
				}
				if (prevSynesthesiaProgress == 38 && !active) {
					event.getEntity().level().playLocalSound(event.getEntity().blockPosition(), RatsSoundRegistry.POTION_EFFECT_END.get(), SoundSource.NEUTRAL, 16.0F, 1.0F, false);
				}
				prevSynesthesiaProgress = synesthesiaProgress;
				if (active && synesthesiaProgress < MAX_SYNESTESIA) {
					synesthesiaProgress += 2F;
				} else if (!active && synesthesiaProgress > 0.0F) {
					synesthesiaProgress -= 2F;
				}
			}
		}
	}

	@SubscribeEvent
	public static void unrenderHatLayerWithMask(RenderLivingEvent.Pre<?, ?, ?> event) {
		// 26.1: the event carries the render state, not the entity.
		if (!(com.github.alexthe666.rats.client.render.RatsClientKeys.getLiving(event.getRenderState()) instanceof LivingEntity living)) return;
		ItemStack stack = living.getItemBySlot(EquipmentSlot.HEAD);
		boolean visible = !stack.is(RatsItemRegistry.BLACK_DEATH_MASK.get()) && !stack.is(RatsItemRegistry.PLAGUE_DOCTOR_MASK.get()) && !stack.is(RatlantisBlockRegistry.MARBLED_CHEESE_RAT_HEAD.get().asItem());

		if (!visible && event.getRenderer().getModel() instanceof HumanoidModel<?> humanoidModel && event.getRenderer().getModel() instanceof HeadedModel) {
			humanoidModel.hat.visible = false;
		}
	}

	@SubscribeEvent
	public static void addTooltipWhenRatlantisIsDisabled(ItemTooltipEvent event) {
		//only fire if actually loaded into a world, because otherwise the ratlantis flag won't be accurate
		if (event.getEntity() != null) {
			if (!RatsMod.RATLANTIS_DATAPACK_ENABLED) {
				if (!RatsMod.RATLANTIS_ITEMS.isEmpty() && RatsMod.RATLANTIS_ITEMS.contains(event.getItemStack().getItem())) {
					event.getToolTip().clear();
					event.getToolTip().add(Component.empty().append(event.getItemStack().getHoverName()).withStyle(event.getItemStack().getRarity().getStyleModifier()));
					event.getToolTip().add(Component.translatable("item.rats.ratlantis_disabled.desc0").withStyle(ChatFormatting.DARK_RED));
					event.getToolTip().add(Component.translatable("item.rats.ratlantis_disabled.desc1").withStyle(ChatFormatting.GRAY));
					event.getToolTip().add(Component.translatable("item.rats.ratlantis_disabled.desc2").withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
		Player player = Minecraft.getInstance().player;
		if (player == null || player.isCreative() || player.isSpectator()) return;
		if (!event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH) || !player.hasEffect(RatsEffectRegistry.PLAGUE) || !RatConfig.plagueHearts) {
			return;
		}
		net.minecraft.client.gui.GuiGraphicsExtractor graphics = event.getGuiGraphics();
		int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		int leftHeight = 39;
		int health = Mth.ceil(player.getHealth());
		Gui gui = Minecraft.getInstance().gui;
		boolean highlight = gui.healthBlinkTime > (long) gui.getGuiTicks() && (gui.healthBlinkTime - (long) gui.getGuiTicks()) / 3L % 2L == 1L;

		long j = Util.getMillis();
		if (health < gui.lastHealth && player.invulnerableTime > 0) {
			gui.lastHealthTime = j;
			gui.healthBlinkTime = gui.getGuiTicks() + 20;
		} else if (health > gui.lastHealth && player.invulnerableTime > 0) {
			gui.lastHealthTime = j;
			gui.healthBlinkTime = gui.getGuiTicks() + 10;
		}

		if (j - gui.lastHealthTime > 1000L) {
			gui.displayHealth = health;
			gui.lastHealthTime = j;
		}

		gui.lastHealth = health;
		int healthLast = gui.displayHealth;
		if (RatConfig.singleRowPlagueHearts) {
			//heart overlays cast to long, so have to do the same in this case
			gui.random.setSeed(gui.getGuiTicks() * 312871L);
		} else {
			gui.random.setSeed(gui.getGuiTicks() * 312871);
		}

		AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		float healthMax = Math.max((float) attrMaxHealth.getValue(), Math.max(healthLast, health));
		int absorption = Mth.ceil(player.getAbsorptionAmount());

		int healthRows = Mth.ceil((healthMax + absorption) / 2.0F / 10.0F);
		int rowHeight = Math.max(10 - (healthRows - 2), 3);

		int left = width / 2 - 91;
		int top = height - leftHeight;

		int regen = -1;
		if (player.hasEffect(MobEffects.REGENERATION)) {
			regen = gui.getGuiTicks() % (RatConfig.singleRowPlagueHearts ? 25 : Mth.ceil(healthMax + 5.0F));
		}

		int healthAmount = Mth.ceil((double) healthMax / 2.0D);

		for (int currentHeart = RatConfig.singleRowPlagueHearts ? Math.min(9, healthAmount + absorption - 1) : healthAmount + absorption - 1; currentHeart >= 0; currentHeart--) {
			int heartYPos = RatConfig.singleRowPlagueHearts ? 18 : 0;
			int emptyHeartYPos = RatConfig.singleRowPlagueHearts ? 27 : 9;
			int x = left + currentHeart % 10 * 8;
			int y = RatConfig.singleRowPlagueHearts ? top : top - currentHeart / 10 * rowHeight;
			if (health + absorption <= 4) {
				y += gui.random.nextInt(2);
			}

			if (currentHeart < healthAmount && currentHeart == regen) {
				y -= 2;
			}

			if (!RatConfig.singleRowPlagueHearts) {
				graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, PLAGUE_HEART_TEXTURE, x, y, 0, emptyHeartYPos, 9, 9, 256, 256);
			}
			int fullHealth = currentHeart * 2;
			//absorption hearts
			if (!RatConfig.singleRowPlagueHearts && currentHeart >= healthAmount) {
				int k2 = fullHealth - health * 2;
				if (k2 < absorption) {
					boolean halfHeart = k2 + 1 == absorption;
					graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, PLAGUE_HEART_TEXTURE, x, y, halfHeart ? 9 : 0, heartYPos, 9, 9, 256, 256);
				}
			}

			//blinking hearts
			if (highlight && fullHealth < healthLast) {
				boolean halfHeart = fullHealth + 1 == healthLast;
				graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, PLAGUE_HEART_TEXTURE, x, y, halfHeart ? 9 : 0, heartYPos, 9, 9, 256, 256);
			}

			//normal hearts
			if (fullHealth < health) {
				boolean halfHeart = fullHealth + 1 == health;
				graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, PLAGUE_HEART_TEXTURE, x, y, halfHeart ? 9 : 0, heartYPos, 9, 9, 256, 256);
			}
		}
	}

	@SubscribeEvent
	public static void onFogColors(ViewportEvent.ComputeFogColor event) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null && level.dimension().equals(RatlantisDimensionRegistry.DIMENSION_KEY)) {
			// TODO(26.1): day-night fraction moved into the environment-attribute/timeline system; use full daylight tint for now.
			float f12 = 1.0F;
			FluidState fluidstate = event.getCamera().getBlockAtCamera().getFluidState();
			if (!fluidstate.is(FluidTags.WATER)) {
				event.setRed((f12));
				event.setGreen((f12));
				event.setBlue(f12 * 0.7F);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingRender(RenderLivingEvent.Post<?, ?, ?> event) {
		if (com.github.alexthe666.rats.client.render.RatsClientKeys.getLiving(event.getRenderState()) instanceof Player player) {
			PoseStack stack = event.getPoseStack();
			int protectorCount = ForgeEvents.getProtectorCount(player);
			if (protectorCount <= 0) return;
			float partialTick = event.getPartialTick();
			int light = event.getRenderState().lightCoords;
			for (int i = 0; i < protectorCount; i++) {
				float tick = (float) (player.tickCount - 1) + partialTick;
				float offsetRot = 30 + 360 * (i / (float) protectorCount);
				float bob = (float) ((Math.sin(tick * 0.1F) * 0.2F + Math.cos(tick * 0.4F + i)) * 0.2);
				float scale = 0.4F;
				float rotation = Mth.wrapDegrees((tick * 8) % 360.0F + offsetRot);
				stack.pushPose();
				stack.mulPose(Axis.YP.rotationDegrees(rotation));
				stack.translate(0.0D, player.getBbHeight() + 0.5D + bob, player.getBbWidth() + 0.5F);
				stack.pushPose();
				stack.mulPose(Axis.YP.rotationDegrees(90));
				stack.mulPose(Axis.XP.rotationDegrees(75.0F));
				stack.scale(scale, scale, scale);
				stack.mulPose(Axis.XP.rotationDegrees(90.0F));
				float f = (player.tickCount + partialTick) * 0.5F;
				RAT_MODEL.setupAnim(player, f, 1, player.tickCount + partialTick, partialTick, 0);
				// 26.1: deferred submit replaces direct buffer access on the living-render event.
				event.getSubmitNodeCollector().submitCustomGeometry(stack, RatsRenderType.getGlowingTranslucent(RatProtectorRenderer.BASE_TEXTURE), (pose, consumer) ->
						RAT_MODEL.renderToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, -1));
				stack.popPose();
				stack.popPose();
			}
		}
	}

	@SubscribeEvent
	public static void onRenderWorld(RenderLevelStageEvent.AfterTranslucentBlocks event) {
		// TODO(26.1): the cheese/radius/patrol staff world overlays used immediate-mode drawing
		// (Tesselator + BufferUploader.drawWithShader), which was removed with the render-pipeline
		// rework. Reimplement on the submit/level-render-state path once the rest of the port settles.
	}

	public static boolean isRatSelectedOnStaff(TamedRat rat) {
		if (Minecraft.getInstance().player != null) {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RatStaffItem || player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RatStaffItem) {
				return Objects.equals(SelectedRat.get(player), rat);
			}
		}
		return false;
	}
}
