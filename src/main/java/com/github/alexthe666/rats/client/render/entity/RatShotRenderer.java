package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.StaticRatModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.server.entity.projectile.RatShot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;

public class RatShotRenderer extends EntityRenderer<RatShot, RatShotRenderer.RatShotRenderState> {

	private static final RenderType TEXTURE_EYES = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/eyes/glow.png"));
	private static final StaticRatModel<RatShot> MODEL_STATIC_RAT = new StaticRatModel<>();

	public RatShotRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void submit(RatShotRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (!(RatsClientKeys.getEntity(state) instanceof RatShot shot)) {
			return;
		}
		int light = state.lightCoords;
		stack.pushPose();
		stack.scale(0.6F, -0.6F, 0.6F);
		stack.translate(0F, -1.5F, 0F);
		stack.mulPose(Axis.YP.rotationDegrees(state.yRot - 180));
		stack.mulPose(Axis.XN.rotationDegrees(state.xRot));

		float f = state.ageInTicks * 0.5F;
		float f1 = 1;
		float age = state.ageInTicks;
		int overlay = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
		// setupAnim runs inside the deferred callback so the shared static model holds this entity's pose when it draws.
		collector.submitCustomGeometry(stack, RenderTypes.entityCutout(state.texture), (pose, consumer) -> {
			MODEL_STATIC_RAT.setupAnim(shot, f, f1, age, 0, 0);
			MODEL_STATIC_RAT.renderToBuffer(rebuildStack(pose), consumer, light, overlay, 0xFFFFFFFF);
		});

		if (state.glowingEyes) {
			collector.submitCustomGeometry(stack, TEXTURE_EYES, (pose, consumer) -> {
				MODEL_STATIC_RAT.setupAnim(shot, f, f1, age, 0, 0);
				MODEL_STATIC_RAT.renderToBuffer(rebuildStack(pose), consumer, light, overlay, 0xFFFFFFFF);
			});
		}
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
	public RatShotRenderState createRenderState() {
		return new RatShotRenderState();
	}

	@Override
	public void extractRenderState(RatShot entity, RatShotRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
		state.xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		state.texture = entity.getColorVariant().getTexture();
		// 26.1: getDayTime is gone; day time now comes from the world clock system.
		long roundedTime = entity.level().getOverworldClockTime() % 24000;
		boolean night = roundedTime >= 13000 && roundedTime <= 22000;
		BlockPos ratPos = entity.getLightPosition();
		int brightI = entity.level().getBrightness(LightLayer.SKY, ratPos);
		int brightJ = entity.level().getBrightness(LightLayer.BLOCK, ratPos);
		int brightness;
		if (night) {
			brightness = brightJ;
		} else {
			brightness = Math.max(brightI, brightJ);
		}
		state.glowingEyes = brightness < 7;
	}

	public static class RatShotRenderState extends EntityRenderState {
		public float yRot;
		public float xRot;
		public boolean glowingEyes;
		public Identifier texture;
	}
}
