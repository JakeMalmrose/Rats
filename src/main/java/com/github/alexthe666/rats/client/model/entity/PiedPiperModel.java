package com.github.alexthe666.rats.client.model.entity;

import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

// 26.1: HierarchicalModel is gone; models pose from a render state. ArmedEntityRenderState carries
// the hand items and main arm the flute animation and ItemInHandLayer need.
public class PiedPiperModel<S extends ArmedEntityRenderState> extends EntityModel<S> implements ArmedModel<S>, HeadedModel {
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public PiedPiperModel(ModelPart root) {
		super(root);
		this.head = root.getChild("head");
		this.nose = this.head.getChild("nose");
		this.rightArm = root.getChild("right_arm");
		this.leftArm = root.getChild("left_arm");
		this.rightLeg = root.getChild("right_leg");
		this.leftLeg = root.getChild("left_leg");
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition partdefinition = mesh.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		head.addOrReplaceChild("nose", CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-1.0F, -1.0F, -2.0F, 2.0F, 4.0F, 2.0F),
				PartPose.offset(0.0F, -2.0F, -4.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create()
						.texOffs(22, 26)
						.addBox(-4.5F, -5.0F, -6.0F, 9.0F, 5.0F, 12.0F, new CubeDeformation(0.25F))
						.texOffs(32, 0)
						.addBox(-2.5F, -7.0F, -4.0F, 5.0F, 2.0F, 8.0F),
				PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		hat.addOrReplaceChild("feather", CubeListBuilder.create()
						.texOffs(44, 0)
						.addBox(0.0F, -9.0F, -2.0F, 0.0F, 10.0F, 10.0F),
				PartPose.offsetAndRotation(-3.0F, -5.0F, 1.0F, 0.0F, 0.0F, -0.3927F));

		partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
						.texOffs(0, 18)
						.addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
						.texOffs(0, 38)
						.addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(30, 46)
						.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
				PartPose.offset(-5.0F, 2.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
						.texOffs(30, 46).mirror()
						.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
				PartPose.offset(5.0F, 2.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
						.texOffs(48, 46)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
				PartPose.offset(-2.0F, 12.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
						.texOffs(48, 46).mirror()
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
				PartPose.offset(2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

	private ModelPart getArm(HumanoidArm arm) {
		return arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void translateToHand(S state, HumanoidArm arm, PoseStack stack) {
		this.getArm(arm).translateAndRotate(stack);
		stack.translate(arm == HumanoidArm.RIGHT ? 0.25F : -0.25F, 0.2F, 0.1F);
		stack.mulPose(Axis.ZP.rotationDegrees(arm == HumanoidArm.RIGHT ? 10 : -10));
	}

	@Override
	public void setupAnim(S state) {
		super.setupAnim(state);
		this.head.yRot = state.yRot * 0.017453292F;
		this.head.xRot = state.xRot * 0.017453292F;
		// 26.1: Model#riding is gone; read the passenger flag off the live entity.
		LivingEntity living = RatsClientKeys.getLiving(state);
		if (living != null && living.isPassenger()) {
			this.rightArm.xRot = -0.62831855F;
			this.rightArm.yRot = 0.0F;
			this.rightArm.zRot = 0.0F;
			this.leftArm.xRot = -0.62831855F;
			this.leftArm.yRot = 0.0F;
			this.leftArm.zRot = 0.0F;
			this.rightLeg.xRot = -1.4137167F;
			this.rightLeg.yRot = 0.31415927F;
			this.rightLeg.zRot = 0.07853982F;
			this.leftLeg.xRot = -1.4137167F;
			this.leftLeg.yRot = -0.31415927F;
			this.leftLeg.zRot = -0.07853982F;
		} else {
			this.rightArm.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + 3.1415927F) * 2.0F * state.walkAnimationSpeed * 0.5F;
			this.rightArm.yRot = 0.0F;
			this.rightArm.zRot = 0.0F;
			this.leftArm.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 2.0F * state.walkAnimationSpeed * 0.5F;
			this.leftArm.yRot = 0.0F;
			this.leftArm.zRot = 0.0F;
			this.rightLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed * 0.5F;
			this.rightLeg.yRot = 0.0F;
			this.rightLeg.zRot = 0.0F;
			this.leftLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + 3.1415927F) * 1.4F * state.walkAnimationSpeed * 0.5F;
			this.leftLeg.yRot = 0.0F;
			this.leftLeg.zRot = 0.0F;
		}
		if (state.rightHandItemStack.is(RatsItemRegistry.RAT_FLUTE.get()) || state.leftHandItemStack.is(RatsItemRegistry.RAT_FLUTE.get())) {
			// entity id keeps each piper's nose wiggling at its own frequency, like vanilla's WitchModel.
			float f = 0.01F * (float) (living == null ? 0 : living.getId() % 10);
			this.nose.yRot = 0.0F;
			this.nose.zRot = Mth.cos(state.ageInTicks * f) * 2.5F * 0.017453292F;
			this.nose.xRot = -1.2F;
			if (state.mainArm == HumanoidArm.LEFT) {
				this.leftArm.yRot = 0.3F + this.head.yRot;
				this.rightArm.yRot = -0.6F + this.head.yRot;
				this.leftArm.xRot = -1.5707964F + this.head.xRot + 0.1F;
				this.rightArm.xRot = -1.5F + this.head.xRot;
			} else {
				this.rightArm.yRot = -0.3F + this.head.yRot;
				this.leftArm.yRot = 0.6F + this.head.yRot;
				this.rightArm.xRot = -1.5707964F + this.head.xRot + 0.1F;
				this.leftArm.xRot = -1.5F + this.head.xRot;
			}
		} else {
			this.nose.yRot = 0.0F;
			this.nose.zRot = 0.0F;
			this.nose.xRot = 0.0F;
		}
	}

}
