package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.registry.RatlantisItemRegistry;
import com.github.alexthe666.rats.server.block.entity.UpgradeSeparatorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class UpgradeSeparatorRenderer implements BlockEntityRenderer<UpgradeSeparatorBlockEntity, UpgradeSeparatorRenderer.UpgradeSeparatorRenderState> {

	private final ItemModelResolver itemModelResolver;

	public static final class UpgradeSeparatorRenderState extends BlockEntityRenderState {
		public float bobTicks;
		public float rotation;
		public ItemStackRenderState item = new ItemStackRenderState();
	}

	public UpgradeSeparatorRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public UpgradeSeparatorRenderState createRenderState() {
		return new UpgradeSeparatorRenderState();
	}

	@Override
	public void extractRenderState(UpgradeSeparatorBlockEntity entity, UpgradeSeparatorRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPos, breakProgress);
		state.bobTicks = entity.ratRotationPrev + partialTicks;
		float f1;

		for (f1 = entity.ratRotation - entity.ratRotationPrev; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
		}

		while (f1 < -(float) Math.PI) {
			f1 += ((float) Math.PI * 2F);
		}
		state.rotation = entity.ratRotationPrev + f1 * partialTicks;
		// 26.1: ItemRenderer.renderStatic is gone; items render through ItemStackRenderState.
		this.itemModelResolver.updateForTopItem(state.item, new ItemStack(RatlantisItemRegistry.ANCIENT_SAWBLADE.get()), ItemDisplayContext.FIXED, entity.getLevel(), null, 0);
	}

	@Override
	public void submit(UpgradeSeparatorRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		stack.pushPose();
		stack.translate(0.5D, 0.15D, 0.5D);
		stack.translate(0.0F, 1F + Mth.sin(state.bobTicks * 0.1F) * 0.1F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(-state.rotation * 0.1F * (180F / (float) Math.PI)));
		stack.mulPose(Axis.ZP.rotationDegrees(180));
		stack.mulPose(Axis.XP.rotationDegrees(90));
		state.item.submit(stack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		stack.popPose();
	}
}
