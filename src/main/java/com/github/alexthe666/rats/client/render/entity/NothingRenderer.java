package com.github.alexthe666.rats.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;

public class NothingRenderer extends EntityRenderer<Entity, EntityRenderState> {

	public NothingRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public EntityRenderState createRenderState() {
		return new EntityRenderState();
	}

	// Intentionally submits nothing (not even the default shadow/flame passes).
	@Override
	public void submit(EntityRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
	}
}
