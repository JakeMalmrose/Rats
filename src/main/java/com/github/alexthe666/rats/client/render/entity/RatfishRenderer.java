package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatFishModel;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.misc.Ratfish;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RatfishRenderer extends MobRenderer<Ratfish, LivingEntityRenderState, RatsEntityModelBridge<Ratfish>> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ratfish.png");

	public RatfishRenderer(EntityRendererProvider.Context context) {
		super(context, new RatsEntityModelBridge<>(new RatFishModel<>(0)), 0.3F);
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return TEXTURE;
	}

	@Override
	protected void setupRotations(LivingEntityRenderState state, PoseStack stack, float bodyRot, float entityScale) {
		super.setupRotations(state, stack, bodyRot, entityScale);
		float f = 1.0F;
		float f1 = 1.0F;
		if (!state.isInWater) {
			f = 1.3F;
			f1 = 1.7F;
		}

		float f2 = f * 4.3F * Mth.sin(f1 * 0.6F * state.ageInTicks);
		stack.mulPose(Axis.YP.rotationDegrees(f2));
		stack.translate(0.0D, 0.0D, -0.4F);
		if (!state.isInWater) {
			stack.translate(0.2F, 0.1F, 0.0D);
			stack.mulPose(Axis.ZP.rotationDegrees(90.0F));
		}
	}
}
