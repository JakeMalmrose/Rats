package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.EmptyModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.client.render.entity.layer.RatKingLayer;
import com.github.alexthe666.rats.server.entity.monster.boss.RatKing;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class RatKingRenderer extends MobRenderer<RatKing, LivingEntityRenderState, RatsEntityModelBridge<RatKing>> {

	private static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/black.png");

	public RatKingRenderer(EntityRendererProvider.Context context) {
		super(context, new RatsEntityModelBridge<>(new EmptyModel<>()), 1.0F);
		this.addLayer(new RatKingLayer(this));
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Vec3 getRenderOffset(LivingEntityRenderState state) {
		return new Vec3(0.0F, 0.0F, 0.0F);
	}

	@Override
	protected void setupRotations(LivingEntityRenderState state, PoseStack stack, float bodyRot, float entityScale) {
		// Intentionally no super call: the king never body-rotates; only the upside-down easter egg applies.
		if (RatsClientKeys.getLiving(state) instanceof RatKing king && king.hasCustomName()) {
			String s = ChatFormatting.stripFormatting(king.getName().getString());
			if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
				stack.translate(0.0D, state.boundingBoxHeight + 0.1F, 0.0D);
				stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}
		}
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return TEXTURE_1;
	}
}
