package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatKingModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.monster.boss.RatKing;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LightLayer;

public class RatKingLayer extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<RatKing>> {
	private static final RenderType TEXTURE_EYES = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/eyes/glow.png"));
	// 26.1: entityCutoutNoCull was removed; the entityCutout pipeline is no-cull now.
	private static final RenderType TEXTURE_0 = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/blue.png"));
	private static final RenderType TEXTURE_1 = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/black.png"));
	private static final RenderType TEXTURE_2 = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/brown.png"));
	private static final RenderType TEXTURE_3 = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/green.png"));
	private static final RatKingModel<RatKing> RAT_MODEL = new RatKingModel<>();

	public RatKingLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<RatKing>> ratRendererIn) {
		super(ratRendererIn);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!(RatsClientKeys.getLiving(state) instanceof RatKing king)) {
			return;
		}
		// 26.1: getDayTime is gone; day time now comes from the world clock system.
		long roundedTime = king.level().getOverworldClockTime() % 24000;
		boolean night = roundedTime >= 13000 && roundedTime <= 22000;
		BlockPos ratPos = king.getLightPosition();
		int brightI = king.level().getBrightness(LightLayer.SKY, ratPos);
		int brightJ = king.level().getBrightness(LightLayer.BLOCK, ratPos);
		int brightness;
		if (night) {
			brightness = brightJ;
		} else {
			brightness = Math.max(brightI, brightJ);
		}

		float limbSwing = state.walkAnimationPos;
		float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
		float ageInTicks = state.ageInTicks;
		int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);

		for (int i = 0; i < RatKing.RAT_COUNT; i++) {
			int deathTime = Math.min(Math.max(0, king.deathTime - i * 5), 5);
			stack.pushPose();
			stack.mulPose(Axis.YP.rotationDegrees(i * RatKing.RAT_ANGLE));
			stack.translate(0, 0.6F + (deathTime * 0.01F), -0.8);
			stack.pushPose();
			stack.scale(0.6F, 0.6F, 0.6F);
			// 26.1: geometry callbacks run after every sub-rat has been submitted and RAT_MODEL is shared,
			// so each rat's pose (setIndex + setupAnim) must be re-applied inside its callback using the
			// pose captured at submit time instead of the (since popped) PoseStack.
			int index = i;
			collector.submitCustomGeometry(stack, this.getRatTexture(king.getRatColors(i)), (pose, consumer) ->
					this.renderKingRat(king, pose, consumer, index, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, light, overlay));
			if (brightness < 7) {
				collector.submitCustomGeometry(stack, TEXTURE_EYES, (pose, consumer) ->
						this.renderKingRat(king, pose, consumer, index, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, light, overlay));
			}

			stack.popPose();
			stack.popPose();
		}
	}

	private void renderKingRat(RatKing king, PoseStack.Pose pose, VertexConsumer consumer, int index, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, int light, int overlay) {
		RAT_MODEL.setIndex(index);
		RAT_MODEL.setupAnim(king, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		PoseStack posed = new PoseStack();
		posed.pushPose();
		posed.last().set(pose);
		RAT_MODEL.renderToBuffer(posed, consumer, light, overlay, -1);
		posed.popPose();
	}

	private RenderType getRatTexture(int textureIndex) {
		return switch (textureIndex) {
			case 1 -> TEXTURE_1;
			case 2 -> TEXTURE_2;
			case 3 -> TEXTURE_3;
			default -> TEXTURE_0;
		};
	}
}
