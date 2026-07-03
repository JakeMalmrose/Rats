package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.FlyingDutchratModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.client.render.entity.layer.DutchratHelmetLayer;
import com.github.alexthe666.rats.server.entity.monster.boss.Dutchrat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DutchratRenderer extends MobRenderer<Dutchrat, LivingEntityRenderState, RatsEntityModelBridge<Dutchrat>> {

	private static final Identifier DUTCHRAT_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/dutchrat/dutchrat.png");

	public DutchratRenderer(EntityRendererProvider.Context context) {
		super(context, new RatsEntityModelBridge<>(new FlyingDutchratModel<>()), 0.5F);
		this.addLayer(new DutchratGlowLayer(this));
		// 26.1: PLAYER_OUTER_ARMOR was folded into the PLAYER_ARMOR ArmorModelSet; head() is the helmet bake.
		this.addLayer(new DutchratHelmetLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_ARMOR.head())), context));
		// 26.1: vanilla ItemInHandLayer requires an ArmedEntityRenderState + ArmedModel; the Citadel bridge is
		// neither, so the conditional sword-in-hand render is a small custom layer instead.
		this.addLayer(new DutchratSwordLayer(this));
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return DUTCHRAT_TEXTURE;
	}

	private static class DutchratSwordLayer extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<Dutchrat>> {

		public DutchratSwordLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<Dutchrat>> parent) {
			super(parent);
		}

		@Override
		public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
			if (!(RatsClientKeys.getLiving(state) instanceof Dutchrat rat)) {
				return;
			}
			if (!rat.hasThrownSword() && rat.getBellSummonTicks() <= 0) {
				ItemStack sword = rat.getMainHandItem();
				if (!sword.isEmpty()) {
					stack.pushPose();
					((FlyingDutchratModel<?>) this.getParentModel().citadel()).translateToHand(HumanoidArm.RIGHT, stack);
					// [VanillaCopy] of the old ItemInHandLayer hand transform.
					stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
					stack.mulPose(Axis.YP.rotationDegrees(180.0F));
					stack.translate(1.0F / 16.0F, 0.125F, -0.625F);
					// 26.1: ItemInHandRenderer/ItemRenderer.renderStatic are gone; items render through ItemStackRenderState.
					ItemStackRenderState swordState = new ItemStackRenderState();
					Minecraft.getInstance().getItemModelResolver().updateForLiving(swordState, sword, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, rat);
					swordState.submit(stack, collector, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
					stack.popPose();
				}
			}
		}
	}

	public static class DutchratGlowLayer extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<Dutchrat>> {
		private static final Identifier GLOW_1 = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/dutchrat/dutchrat_glow_1.png");
		private static final Identifier GLOW_2 = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/dutchrat/dutchrat_glow_2.png");

		public DutchratGlowLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<Dutchrat>> parent) {
			super(parent);
		}

		@Override
		public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
			this.getParentModel().setupAnim(state);
			// capture the bridge at submit time — the renderer swaps this.model per entity (adult vs
			// pinkie), so getParentModel() at draw time can be another rat's model (ghost-baby overlay)
			final var submitModel = this.getParentModel();
			collector.submitCustomGeometry(stack, RatsRenderType.getGlowingTranslucent(GLOW_1), (pose, consumer) -> {
					// re-pose the shared model at draw time: other entities re-run setupAnim between submit and draw
					submitModel.setupAnim(state);
					submitModel.renderCitadelToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
				});
			collector.submitCustomGeometry(stack, RatsRenderType.getGlowingTranslucent(GLOW_2), (pose, consumer) -> {
					// re-pose the shared model at draw time: other entities re-run setupAnim between submit and draw
					submitModel.setupAnim(state);
					submitModel.renderCitadelToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY, 0x80FFFFFF);
				});
		}
	}
}
