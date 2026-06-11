package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.projectile.RatArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class RatArrowRenderer extends ArrowRenderer<RatArrow> {

	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat_arrow.png");

	public RatArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public Identifier getTextureLocation(RatArrow entity) {
		return TEXTURE;
	}
}
