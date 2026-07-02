package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.LaserPortalModel;
import com.github.alexthe666.rats.server.entity.misc.LaserPortal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class LaserPortalRenderer extends EntityRenderer<LaserPortal, LaserPortalRenderer.LaserPortalRenderState> {

	private static final Identifier PORTAL = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/neo_ratlantean/neo_ratlantean_glow.png");
	private static final LaserPortalModel MODEL_NEO_RATLANTEAN = new LaserPortalModel();

	public LaserPortalRenderer(EntityRendererProvider.Context context) {
		super(context);
		MODEL_NEO_RATLANTEAN.floatyPivot.setRotationPoint(0, 0, 0);
		MODEL_NEO_RATLANTEAN.floatyPivot.rotateAngleY = 0.7853981633974483F;
	}

	@Override
	public void submit(LaserPortalRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		float d1 = state.scale;

		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
		stack.pushPose();
		stack.scale(1.5F * d1, 1.5F * d1, 1.5F * d1);
		stack.translate(0, 0.5F, 0);
		stack.translate(0, 1 - d1, 0);
		stack.mulPose(Axis.YP.rotationDegrees(90));
		stack.mulPose(Axis.XP.rotationDegrees(90));
		stack.mulPose(Axis.ZP.rotationDegrees(state.yRot - 90.0F));
		stack.mulPose(Axis.YP.rotationDegrees(state.ageInTicks * 10));
		// 26.1: ItemRenderer.getFoilBuffer is gone; a foiled model is a base submit plus an entityGlint submit.
		collector.submitCustomGeometry(stack, RenderTypes.entityCutout(PORTAL), (pose, consumer) ->
				MODEL_NEO_RATLANTEAN.renderToBuffer(rebuildStack(pose), consumer, 240, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF));
		collector.submitCustomGeometry(stack, RenderTypes.entityGlint(), (pose, consumer) ->
				MODEL_NEO_RATLANTEAN.renderToBuffer(rebuildStack(pose), consumer, 240, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF));
		stack.popPose();
		stack.popPose();
		super.submit(state, stack, collector, camera);
	}

	// Citadel models draw from a mutable PoseStack; rebuild one from the baked pose the deferred callback gets.
	private static PoseStack rebuildStack(PoseStack.Pose pose) {
		PoseStack stack = new PoseStack();
		stack.pushPose();
		stack.last().set(pose);
		return stack;
	}

	private float interpolateValue(float start, float end, float pct) {
		return start + (end - start) * pct;
	}

	@Override
	public LaserPortalRenderState createRenderState() {
		return new LaserPortalRenderState();
	}

	@Override
	public void extractRenderState(LaserPortal entity, LaserPortalRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.yRot = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks;
		state.xRot = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
		state.scale = this.interpolateValue(entity.scaleOfPortalPrev, entity.scaleOfPortal, partialTicks);
	}

	public static class LaserPortalRenderState extends EntityRenderState {
		public float yRot;
		public float xRot;
		public float scale;
	}
}
