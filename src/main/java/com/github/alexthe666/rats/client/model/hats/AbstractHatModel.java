package com.github.alexthe666.rats.client.model.hats;

import com.github.alexthe666.rats.client.render.RatsClientKeys;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class AbstractHatModel extends HumanoidModel<HumanoidRenderState> {

	public AbstractHatModel(ModelPart root) {
		super(root);
	}

	public AbstractHatModel(ModelPart root, Function<Identifier, RenderType> function) {
		super(root, function);
	}

	// 26.1: models pose from render states; armor stand poses now live on ArmorStandRenderState (see vanilla ArmorStandArmorModel).
	@Override
	public void setupAnim(HumanoidRenderState state) {
		if (state instanceof ArmorStandRenderState stand) {
			this.head.xRot = 0.017453292F * stand.headPose.x();
			this.head.yRot = 0.017453292F * stand.headPose.y();
			this.head.zRot = 0.017453292F * stand.headPose.z();
			this.head.setPos(0.0F, 1.0F, 0.0F);
			this.body.xRot = 0.017453292F * stand.bodyPose.x();
			this.body.yRot = 0.017453292F * stand.bodyPose.y();
			this.body.zRot = 0.017453292F * stand.bodyPose.z();
			this.leftArm.xRot = 0.017453292F * stand.leftArmPose.x();
			this.leftArm.yRot = 0.017453292F * stand.leftArmPose.y();
			this.leftArm.zRot = 0.017453292F * stand.leftArmPose.z();
			this.rightArm.xRot = 0.017453292F * stand.rightArmPose.x();
			this.rightArm.yRot = 0.017453292F * stand.rightArmPose.y();
			this.rightArm.zRot = 0.017453292F * stand.rightArmPose.z();
			this.leftLeg.xRot = 0.017453292F * stand.leftLegPose.x();
			this.leftLeg.yRot = 0.017453292F * stand.leftLegPose.y();
			this.leftLeg.zRot = 0.017453292F * stand.leftLegPose.z();
			this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
			this.rightLeg.xRot = 0.017453292F * stand.rightLegPose.x();
			this.rightLeg.yRot = 0.017453292F * stand.rightLegPose.y();
			this.rightLeg.zRot = 0.017453292F * stand.rightLegPose.z();
			this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
			// 26.1: ModelPart.copyFrom is gone and the hat part is now a child of the head, so it follows it automatically.
		} else {
			super.setupAnim(state);
			// 26.1: dispatch to the legacy entity-based hook so subclasses animating off the live entity keep working.
			LivingEntity living = RatsClientKeys.getLiving(state);
			if (living != null) {
				this.setupAnim(living, state.walkAnimationPos, Math.min(1.0F, state.walkAnimationSpeed), state.ageInTicks, state.yRot, state.xRot);
			}
		}
	}

	/** Legacy entity-driven animation hook; vanilla no longer calls this directly, {@link #setupAnim(HumanoidRenderState)} forwards to it. */
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
}
