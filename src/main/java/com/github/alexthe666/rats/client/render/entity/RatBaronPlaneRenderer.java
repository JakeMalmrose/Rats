package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.BiplaneModel;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.monster.boss.RatBaronPlane;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

// Keep the BiplaneModel behind the bridge: AbstractRatRenderer.scale() unwraps it to seat riding rats.
public class RatBaronPlaneRenderer extends MobRenderer<RatBaronPlane, LivingEntityRenderState, RatsEntityModelBridge<RatBaronPlane>> {

	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat_baron_plane.png");

	public RatBaronPlaneRenderer(EntityRendererProvider.Context context) {
		super(context, new RatsEntityModelBridge<>(new BiplaneModel<>()), 1.65F);
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
