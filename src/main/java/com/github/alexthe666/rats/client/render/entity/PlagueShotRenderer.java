package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatlanteanSpiritModel;
import com.github.alexthe666.rats.server.entity.projectile.PlagueShot;
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
import net.minecraft.util.Mth;

public class PlagueShotRenderer extends EntityRenderer<PlagueShot, PlagueShotRenderer.PlagueShotRenderState> {

	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/plague_cloud.png");
	private static final RatlanteanSpiritModel<PlagueShot> MODEL_SPIRIT = new RatlanteanSpiritModel<>();

	public PlagueShotRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void submit(PlagueShotRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		int light = state.lightCoords;
		stack.pushPose();
		stack.scale(1.5F, -1.5F, 1.5F);
		stack.mulPose(Axis.YP.rotationDegrees(state.yRot + 180.0F));
		stack.mulPose(Axis.XP.rotationDegrees(-state.xRot));
		stack.translate(0F, -1.5F, 0F);
		// 26.1: ItemRenderer.getFoilBuffer is gone; a foiled model is a base submit plus an entityGlint submit.
		collector.submitCustomGeometry(stack, RenderTypes.entityCutoutNoCull(TEXTURE), (pose, consumer) ->
				MODEL_SPIRIT.renderToBuffer(rebuildStack(pose), consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF));
		collector.submitCustomGeometry(stack, RenderTypes.entityGlint(), (pose, consumer) ->
				MODEL_SPIRIT.renderToBuffer(rebuildStack(pose), consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF));
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

	@Override
	public PlagueShotRenderState createRenderState() {
		return new PlagueShotRenderState();
	}

	@Override
	public void extractRenderState(PlagueShot entity, PlagueShotRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
		state.xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
	}

	public static class PlagueShotRenderState extends EntityRenderState {
		public float yRot;
		public float xRot;
	}
}
