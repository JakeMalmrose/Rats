package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RattlingGunBaseModel;
import com.github.alexthe666.rats.client.model.entity.RattlingGunModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.server.entity.misc.RattlingGun;
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
import net.minecraft.resources.Identifier;

public class RattlingGunRenderer extends EntityRenderer<RattlingGun, RattlingGunRenderer.RattlingGunRenderState> {

	// 26.1: entityCutoutNoCull was removed; the entityCutout pipeline is no-cull now.
	private static final RenderType TEXTURE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rattling_gun/rattling_gun.png"));
	private static final RenderType TEXTURE_FIRING = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rattling_gun/rattling_gun_firing.png"));
	public static final RattlingGunModel<RattlingGun> GUN_MODEL = new RattlingGunModel<>();
	public static final RattlingGunBaseModel<RattlingGun> GUN_BASE_MODEL = new RattlingGunBaseModel<>();

	public RattlingGunRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void submit(RattlingGunRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (!(RatsClientKeys.getEntity(state) instanceof RattlingGun gun)) {
			return;
		}
		int light = state.lightCoords;
		float age = state.ageInTicks;
		boolean firing = state.firing;
		stack.pushPose();
		stack.pushPose();
		stack.translate(0, 1.5F, 0);
		stack.mulPose(Axis.XP.rotationDegrees(180));
		collector.submitCustomGeometry(stack, TEXTURE, (pose, consumer) ->
				GUN_BASE_MODEL.renderToBuffer(rebuildStack(pose), consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF));
		stack.mulPose(Axis.YP.rotationDegrees(state.yRot));
		// setupAnim runs inside the deferred callback so the shared static model holds this entity's pose when it draws.
		collector.submitCustomGeometry(stack, TEXTURE, (pose, consumer) -> {
			this.poseGun(gun, firing, age);
			GUN_MODEL.renderToBuffer(rebuildStack(pose), consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
		});
		stack.popPose();

		if (firing) {
			stack.pushPose();
			stack.translate(0, 1.6F, 0);
			stack.mulPose(Axis.XP.rotationDegrees(180));
			stack.mulPose(Axis.YP.rotationDegrees(state.yRot));
			collector.submitCustomGeometry(stack, TEXTURE_FIRING, (pose, consumer) -> {
				GUN_MODEL.resetToDefaultPose();
				GUN_MODEL.setupAnim(gun, 0, 0, age, 0, 0);
				GUN_MODEL.renderToBuffer(rebuildStack(pose), consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
			});
			stack.popPose();
		}
		stack.popPose();

		super.submit(state, stack, collector, camera);
	}

	private void poseGun(RattlingGun gun, boolean firing, float age) {
		GUN_MODEL.resetToDefaultPose();
		if (!firing) {
			GUN_MODEL.gun1.rotateAngleZ = 0;
			GUN_MODEL.handle1.rotateAngleX = 0;
		} else {
			GUN_MODEL.setupAnim(gun, 0, 0, age, 0, 0);
		}
	}

	// Citadel models draw from a mutable PoseStack; rebuild one from the baked pose the deferred callback gets.
	private static PoseStack rebuildStack(PoseStack.Pose pose) {
		PoseStack stack = new PoseStack();
		stack.pushPose();
		stack.last().set(pose);
		return stack;
	}

	@Override
	public RattlingGunRenderState createRenderState() {
		return new RattlingGunRenderState();
	}

	@Override
	public void extractRenderState(RattlingGun entity, RattlingGunRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.yRot = entity.getYRot();
		state.firing = entity.isFiring();
		state.ageInTicks = entity.tickCount + partialTicks;
	}

	public static class RattlingGunRenderState extends EntityRenderState {
		public float yRot;
		public boolean firing;
	}
}
