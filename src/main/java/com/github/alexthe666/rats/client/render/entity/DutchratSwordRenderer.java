package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.registry.RatlantisItemRegistry;
import com.github.alexthe666.rats.server.entity.projectile.DutchratSword;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DutchratSwordRenderer extends EntityRenderer<DutchratSword, DutchratSwordRenderer.DutchratSwordRenderState> {

	// 26.1: created lazily — building an ItemStack during renderer construction runs before
	// item data components are bound and throws "Components not bound yet".
	private static ItemStack piratSword;

	private static ItemStack getPiratSword() {
		if (piratSword == null) {
			piratSword = new ItemStack(RatlantisItemRegistry.GHOST_PIRAT_CUTLASS.get());
		}
		return piratSword;
	}

	private final ItemModelResolver itemModelResolver;

	public DutchratSwordRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemModelResolver = context.getItemModelResolver();
	}

	@Override
	public void submit(DutchratSwordRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(state.yRot - 180.0F));
		stack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
		stack.translate(0.0F, 0.5F, 0.0F);
		stack.scale(3.0F, 3.0F, 3.0F);
		stack.mulPose(Axis.YP.rotationDegrees(90.0F));
		stack.mulPose(Axis.ZP.rotationDegrees(state.ageInTicks * 20.0F));
		stack.translate(0.0F, -0.15F, 0.0F);
		state.item.submit(stack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		stack.popPose();
		super.submit(state, stack, collector, camera);
	}

	@Override
	public DutchratSwordRenderState createRenderState() {
		return new DutchratSwordRenderState();
	}

	@Override
	public void extractRenderState(DutchratSword entity, DutchratSwordRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
		state.xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		this.itemModelResolver.updateForNonLiving(state.item, getPiratSword(), ItemDisplayContext.GROUND, entity);
	}

	public static class DutchratSwordRenderState extends EntityRenderState {
		public float yRot;
		public float xRot;
		public final ItemStackRenderState item = new ItemStackRenderState();
	}
}
