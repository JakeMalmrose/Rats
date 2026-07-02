package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.projectile.GolemBeam;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class GolemBeamRenderer extends EntityRenderer<GolemBeam, GolemBeamRenderer.GolemBeamRenderState> {

	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ratlantean_automaton/automaton_beam.png");
	private static final RenderType RENDER_TYPE = RenderTypes.eyes(TEXTURE);

	public GolemBeamRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void submit(GolemBeamRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
		stack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
		float f9 = state.shake;
		if (f9 > 0.0F) {
			float f10 = -Mth.sin(f9 * 3.0F) * f9;
			stack.mulPose(Axis.ZP.rotationDegrees(f10));
		}

		stack.mulPose(Axis.XP.rotationDegrees(45.0F));
		stack.scale(0.05625F, 0.05625F, 0.05625F);
		stack.translate(-4.0D, 0.0D, 0.0D);
		collector.submitCustomGeometry(stack, RENDER_TYPE, (pose, consumer) -> {
			vertex(pose, consumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, 240);
			vertex(pose, consumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, 240);
			vertex(pose, consumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, 240);
			vertex(pose, consumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, 240);
			vertex(pose, consumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, 240);
			vertex(pose, consumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, 240);
			vertex(pose, consumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, 240);
			vertex(pose, consumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, 240);
		});

		// 26.1: custom geometry captures a pose snapshot per submit, so each fin rotation needs its own submit.
		for (int j = 0; j < 4; ++j) {
			stack.mulPose(Axis.XP.rotationDegrees(90.0F));
			collector.submitCustomGeometry(stack, RENDER_TYPE, (pose, consumer) -> {
				vertex(pose, consumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, 240);
				vertex(pose, consumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, 240);
				vertex(pose, consumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, 240);
				vertex(pose, consumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, 240);
			});
		}

		stack.popPose();
		super.submit(state, stack, collector, camera);
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, int x, int y, int z, float u, float v, int xNorm, int zNorm, int yNorm, int light) {
		consumer.addVertex(pose, (float) x, (float) y, (float) z).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, (float) xNorm, (float) yNorm, (float) zNorm);
	}

	@Override
	public GolemBeamRenderState createRenderState() {
		return new GolemBeamRenderState();
	}

	@Override
	public void extractRenderState(GolemBeam entity, GolemBeamRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
		state.xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		state.shake = (float) entity.shakeTime - partialTicks;
	}

	public static class GolemBeamRenderState extends EntityRenderState {
		public float yRot;
		public float xRot;
		public float shake;
	}
}
