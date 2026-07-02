package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.server.entity.mount.RatChickenMount;
import net.minecraft.client.model.animal.chicken.AdultChickenModel;
import net.minecraft.client.model.animal.chicken.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RatChickenMountRenderer extends MobRenderer<RatChickenMount, ChickenRenderState, ChickenModel> {
	// 26.1: vanilla chicken textures moved to per-variant paths; the mount always uses the temperate one.
	private static final Identifier CHICKEN_TEXTURES = Identifier.parse("textures/entity/chicken/chicken_temperate.png");

	public RatChickenMountRenderer(EntityRendererProvider.Context context) {
		super(context, new AdultChickenModel(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
	}

	@Override
	public ChickenRenderState createRenderState() {
		return new ChickenRenderState();
	}

	@Override
	public void extractRenderState(RatChickenMount entity, ChickenRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		// 26.1: the old getBob() wing flap is computed by ChickenModel from these state fields.
		state.flap = Mth.lerp(partialTicks, entity.oFlap, entity.wingRotation);
		state.flapSpeed = Mth.lerp(partialTicks, entity.oFlapSpeed, entity.destPos);
	}

	@Override
	public Identifier getTextureLocation(ChickenRenderState state) {
		return CHICKEN_TEXTURES;
	}
}
