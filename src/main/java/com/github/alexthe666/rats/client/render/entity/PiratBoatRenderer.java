package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.client.model.entity.PiratBoatModel;
import com.github.alexthe666.rats.client.render.entity.layer.PiratBoatSailLayer;
import com.github.alexthe666.rats.server.entity.misc.PiratBoat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

// 26.1: MobRenderer is render-state based and applies the standard living transforms (yaw flip,
// hurt tint, -1.501 translate) itself; the boat model is authored sideways, so the extra 90° yaw
// and 1.2 scale are applied in scale().
public class PiratBoatRenderer extends MobRenderer<PiratBoat, LivingEntityRenderState, PiratBoatModel> {

	private static final Identifier TEXTURE = Identifier.parse("textures/entity/boat/spruce.png");

	public PiratBoatRenderer(EntityRendererProvider.Context context, PiratBoatModel model) {
		super(context, model, 0.0F);
		this.addLayer(new PiratBoatSailLayer(this));
	}

	@Override
	protected void scale(LivingEntityRenderState state, PoseStack stack) {
		stack.scale(1.2F, 1.2F, 1.2F);
		stack.mulPose(Axis.YP.rotationDegrees(90.0F));
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return TEXTURE;
	}
}
