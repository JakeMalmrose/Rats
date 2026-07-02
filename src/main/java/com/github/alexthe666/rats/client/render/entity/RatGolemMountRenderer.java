package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatGolemMountModel;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.client.render.entity.layer.RatGolemMountCracksLayer;
import com.github.alexthe666.rats.server.entity.mount.RatGolemMount;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RatGolemMountRenderer extends MobRenderer<RatGolemMount, LivingEntityRenderState, RatsEntityModelBridge<RatGolemMount>> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/mounts/golem_mount.png");

	public RatGolemMountRenderer(EntityRendererProvider.Context context) {
		super(context, new RatsEntityModelBridge<>(new RatGolemMountModel<>()), 0.75F);
		this.addLayer(new RatGolemMountCracksLayer(this));
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
