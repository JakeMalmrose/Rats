package com.github.alexthe666.rats.client.render;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;

/**
 * Adapts a Citadel {@link AdvancedEntityModel} (which is parameterized over the live Entity) to the
 * vanilla 26.1 {@link EntityModel} over {@link LivingEntityRenderState} that MobRenderer requires.
 * Vanilla's Model.renderToBuffer only draws ModelPart geometry, so this bridge uses an empty root
 * and delegates to Citadel's mesh instead.
 */
public final class RatsEntityModelBridge<E extends LivingEntity> extends EntityModel<LivingEntityRenderState> {

	private final AdvancedEntityModel<E> citadel;

	public RatsEntityModelBridge(AdvancedEntityModel<E> citadel) {
		super(new ModelPart(Collections.emptyList(), Map.of()), RenderTypes::entityCutout);
		this.citadel = citadel;
	}

	public AdvancedEntityModel<E> citadel() {
		return citadel;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		this.citadel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
		this.citadel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, -1);
	}

	/** Citadel mesh draw for layers and custom submit callbacks. */
	public void renderCitadelToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		this.citadel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
	}

	/** Convenience overload for deferred submit callbacks that provide a baked pose instead of a mutable stack. */
	public void renderCitadelToBuffer(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		PoseStack stack = new PoseStack();
		stack.pushPose();
		stack.last().set(pose);
		this.citadel.renderToBuffer(stack, buffer, packedLight, packedOverlay, color);
		stack.popPose();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		LivingEntity raw = RatsClientKeys.getLiving(state);
		if (raw == null) {
			return;
		}
		@SuppressWarnings("unchecked")
		E entity = (E) raw;
		float limbSwing = state.walkAnimationPos;
		float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
		float ageInTicks = state.ageInTicks;
		// Vanilla LivingEntityRenderer.extractRenderState: state.yRot is already head yaw relative to body (wrapped).
		float netHeadYaw = state.yRot;
		float headPitch = state.xRot;
		citadel.prepareMobModel(entity, limbSwing, limbSwingAmount, ageInTicks);
		citadel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}
}
