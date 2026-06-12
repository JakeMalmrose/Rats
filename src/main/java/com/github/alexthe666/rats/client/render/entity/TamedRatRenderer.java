package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.events.ModClientEvents;
import com.github.alexthe666.rats.client.model.entity.PinkieModel;
import com.github.alexthe666.rats.client.model.entity.RatModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.client.render.entity.layer.TamedRatEyesLayer;
import com.github.alexthe666.rats.client.render.entity.layer.TamedRatOverlayLayer;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.ChangesTextureUpgrade;
import com.github.alexthe666.rats.server.misc.RatUpgradeUtils;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.ClientHooks;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TamedRatRenderer extends AbstractRatRenderer<TamedRat> {

	private static final Identifier PINKIE_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/baby.png");
	private static final String[] FISHING_PROGRESS = new String[] {".", "..", "..."};

	private static final ImmutableMap<String, Identifier> SPECIAL_SKINS = ImmutableMap.<String, Identifier>builder()
		.put("brick", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/brick.png"))
		.put("bugraak", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/bugraak.png"))
		.put("dino", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/dino.png"))
		.put("friar", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/friar.png"))
		.put("gizmo", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/gizmo.png"))
		.put("julian", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/julian.png"))
		.put("lil_cheese", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/lil_cheese.png"))
		.put("ratatla", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/ratatla.png"))
		.put("riddler", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/riddler.png"))
		.put("sharva", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/sharva.png"))
		.put("shizuka", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/shizuka.png"))
		.put("skrat", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/skrat.png"))
		.put("splinter", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/splinter.png"))
		.put("ultrakill", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/ultrakill.png"))
		.put("zura", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/patreon_skins/zura.png"))
		.build();

	private final RatsEntityModelBridge<TamedRat> ratBridge;
	private final RatsEntityModelBridge<TamedRat> pinkieBridge;

	@SuppressWarnings("unchecked")
	public TamedRatRenderer(EntityRendererProvider.Context context) {
		super(context, new RatModel<>());
		this.ratBridge = (RatsEntityModelBridge<TamedRat>) (Object) this.model;
		this.pinkieBridge = new RatsEntityModelBridge<>(new PinkieModel<>());
		this.addLayer(new TamedRatOverlayLayer(this));
		this.addLayer(new TamedRatEyesLayer(this));
	}

	@Override
	protected boolean shouldShowName(TamedRat entity, double distanceToCameraSq) {
		return ModClientEvents.shouldRenderNameplates() && super.shouldShowName(entity, distanceToCameraSq);
	}

	@Override
	protected void scale(LivingEntityRenderState state, PoseStack stack) {
		// 26.1: baby/adult model swap moved from render() to scale() (mirrors AM RenderBison).
		this.model = state.isBaby ? this.pinkieBridge : this.ratBridge;
		super.scale(state, stack);
	}

	@Override
	public void submit(LivingEntityRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		super.submit(state, stack, collector, camera);
		if (!(RatsClientKeys.getLiving(state) instanceof TamedRat entity)) {
			return;
		}
		if (ModClientEvents.shouldRenderNameplates() && entity.crafting && RatUpgradeUtils.hasUpgrade(entity, RatsItemRegistry.RAT_UPGRADE_FISHERMAN.get())) {
			boolean hasName = this.shouldShowName(entity, state.distanceToCameraSq);
			stack.pushPose();
			stack.translate(0.0F, hasName ? 1.3F : 1.05F, 0.0F);
			stack.mulPose(camera.orientation);
			stack.scale(0.25F, 0.25F, 0.25F);
			stack.mulPose(Axis.YP.rotationDegrees(180));
			stack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(state.ageInTicks / 5) * 20));
			Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(entity, new ItemStack(Items.FISHING_ROD), ItemDisplayContext.GUI, stack, collector, state.lightCoords);
			stack.popPose();

			stack.pushPose();
			stack.translate(0.0F, entity.getBbHeight() + 0.5F, 0.0F);
			stack.mulPose(camera.orientation);
			stack.scale(-0.025F, -0.025F, 0.025F);
			String dots = FISHING_PROGRESS[(int) (Util.getMillis() / 300L % (long) FISHING_PROGRESS.length)];
			collector.submitText(stack, -this.getFont().width(dots) / 2.0F, hasName ? -10 : 0, Component.literal(dots).getVisualOrderText(), false, Font.DisplayMode.NORMAL, 15728880, 0xFFFFFFFF, 0, 0);
			stack.popPose();
		}
//		if (ForgeClientEvents.isRatSelectedOnStaff(entity)) {
//			this.renderAdditionalInfo(entity, state, stack, collector, camera, state.lightCoords);
//		}
	}

	protected void renderAdditionalInfo(TamedRat entity, LivingEntityRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera, int light) {
		if (ClientHooks.isNameplateInRenderDistance(entity, state.distanceToCameraSq)) {
			// 1.21: Entity.getNameTagOffsetY was removed in favour of EntityAttachments.NAME_TAG.
			// We compute the nameplate Y by hand here (BbHeight + small offset) so the additional rat
			// info (RF / status text) layers above the rat's head consistently across baby/adult sizes.
			float f = entity.getBbHeight() + 0.5F;
			stack.pushPose();
			stack.translate(0.0F, f, 0.0F);
			stack.mulPose(camera.orientation);
			stack.scale(-0.025F, -0.025F, 0.025F);
			float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
			int j = (int) (f1 * 255.0F) << 24;
			Font font = this.getFont();

			Component rf = Component.literal("RF: " + entity.getHeldRF());
			float f2 = (float) (-font.width(rf) / 2);
			collector.submitText(stack, f2, -20, rf.getVisualOrderText(), false, Font.DisplayMode.NORMAL, light, 553648127, j, 0);
			Component fluid = Component.literal("Fluid: " + entity.transportingFluid.getAmount());
			collector.submitText(stack, f2, -10, fluid.getVisualOrderText(), false, Font.DisplayMode.NORMAL, light, 553648127, j, 0);
			Component pickupSides = Component.literal("Deposit: " + entity.depositFacing.getName() + ", Pickup: " + entity.pickupFacing.getName());
			collector.submitText(stack, f2, 0, pickupSides.getVisualOrderText(), false, Font.DisplayMode.NORMAL, light, 553648127, j, 0);

			stack.popPose();
		}
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		if (!(RatsClientKeys.getLiving(state) instanceof TamedRat entity) || entity.isBaby()) {
			return PINKIE_TEXTURE;
		} else {
			AtomicReference<String> upgradeTex = new AtomicReference<>(null);

			RatUpgradeUtils.forEachUpgrade(entity, item -> item instanceof ChangesTextureUpgrade, (stack, slot) -> {
				if (entity.isSlotVisible(slot)) {
					upgradeTex.set(((ChangesTextureUpgrade) stack.getItem()).getTexture().toString());
				}
			});

			if (upgradeTex.get() != null) {
				return Identifier.parse(upgradeTex.get());
			}

			if (entity.hasCustomName()) {
				String name = entity.getCustomName().getString().toLowerCase(Locale.ROOT);
				if (SPECIAL_SKINS.containsKey(name)) {
					return Objects.requireNonNull(SPECIAL_SKINS.get(name));
				}
			}

			return super.getTextureLocation(state);
		}
	}
}
