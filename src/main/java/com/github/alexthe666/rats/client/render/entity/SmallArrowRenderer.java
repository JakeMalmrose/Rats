package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.server.entity.projectile.SmallArrow;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

// 26.1: ArrowRenderer is render-state based and draws the shared ArrowModel; the old hand-drawn
// quad geometry is gone. The "small" look is kept by scaling the pose before the model submits.
public class SmallArrowRenderer extends ArrowRenderer<SmallArrow, ArrowRenderState> {
	public static final Identifier ARROW = Identifier.parse("textures/entity/projectiles/arrow.png");

	public SmallArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void submit(ArrowRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		stack.pushPose();
		stack.scale(0.5F, 0.5F, 0.5F);
		super.submit(state, stack, collector, camera);
		stack.popPose();
	}

	@Override
	protected Identifier getTextureLocation(ArrowRenderState state) {
		return ARROW;
	}

	@Override
	public ArrowRenderState createRenderState() {
		return new ArrowRenderState();
	}
}
