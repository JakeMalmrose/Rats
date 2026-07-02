package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.projectile.LaserBeam;
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

public class LaserBeamRenderer extends EntityRenderer<LaserBeam, LaserBeamRenderer.LaserBeamRenderState> {

	private static final Identifier TEXTURE_RED = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/neo_ratlantean/laser_beam.png");
	private static final Identifier TEXTURE_BLUE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/neo_ratlantean/laser_beam_blue.png");
	private static final RenderType RENDER_TYPE_RED = RenderTypes.eyes(TEXTURE_RED);
	private static final RenderType RENDER_TYPE_BLUE = RenderTypes.eyes(TEXTURE_BLUE);

	public LaserBeamRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void submit(LaserBeamRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
		stack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
		float f9 = state.shake;
		int r = state.red;
		int g = state.green;
		int b = state.blue;
		if (f9 > 0.0F) {
			float f10 = -Mth.sin(f9 * 3.0F) * f9;
			stack.mulPose(Axis.ZP.rotationDegrees(f10));
		}

		stack.mulPose(Axis.XP.rotationDegrees(45.0F));
		stack.scale(0.05625F, 0.05625F, 0.05625F);
		stack.translate(-4.0D, 0.0D, 0.0D);
		int light = 240;
		collector.submitCustomGeometry(stack, r > 200 ? RENDER_TYPE_RED : RENDER_TYPE_BLUE, (pose, consumer) -> {
			vertex(pose, consumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light, r, g, b);
			vertex(pose, consumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light, r, g, b);
			vertex(pose, consumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light, r, g, b);
			vertex(pose, consumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light, r, g, b);
			vertex(pose, consumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light, r, g, b);
			vertex(pose, consumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light, r, g, b);
			vertex(pose, consumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light, r, g, b);
			vertex(pose, consumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light, r, g, b);
		});
		stack.popPose();
		super.submit(state, stack, collector, camera);
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, int normX, int normZ, int normY, int light, int red, int green, int blue) {
		consumer.addVertex(pose, x, y, z).setColor(red, green, blue, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, (float) normX, (float) normY, (float) normZ);
	}

	@Override
	public LaserBeamRenderState createRenderState() {
		return new LaserBeamRenderState();
	}

	@Override
	public void extractRenderState(LaserBeam entity, LaserBeamRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
		state.xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		state.shake = (float) entity.shakeTime - partialTicks;
		state.red = (int) (entity.getRGB()[0] * 255F);
		state.green = (int) (entity.getRGB()[1] * 255F);
		state.blue = (int) (entity.getRGB()[2] * 255F);
	}

	public static class LaserBeamRenderState extends EntityRenderState {
		public float yRot;
		public float xRot;
		public float shake;
		public int red;
		public int green;
		public int blue;
	}
}
