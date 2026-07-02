package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatlanteanSpiritModel;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.server.block.entity.UpgradeCombinerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class UpgradeCombinerRenderer implements BlockEntityRenderer<UpgradeCombinerBlockEntity, UpgradeCombinerRenderer.UpgradeCombinerRenderState> {
	private static final RatlanteanSpiritModel<?> MODEL_SPIRIT = new RatlanteanSpiritModel<>();
	private static final RenderType TEXTURE = RatsRenderType.getGlowingTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/upgrade_combiner.png"));

	private final PoseStack scratch = new PoseStack();

	public static final class UpgradeCombinerRenderState extends BlockEntityRenderState {
		public float bobTicks;
		public float rotation;
	}

	public UpgradeCombinerRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public UpgradeCombinerRenderState createRenderState() {
		return new UpgradeCombinerRenderState();
	}

	@Override
	public void extractRenderState(UpgradeCombinerBlockEntity entity, UpgradeCombinerRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPos, breakProgress);
		state.bobTicks = (float) entity.tickCount + partialTicks;
		float f1;

		for (f1 = entity.ratRotation - entity.ratRotationPrev; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
		}

		while (f1 < -(float) Math.PI) {
			f1 += ((float) Math.PI * 2F);
		}
		state.rotation = entity.ratRotationPrev + f1 * partialTicks;
	}

	@Override
	public void submit(UpgradeCombinerRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		stack.pushPose();
		stack.translate(0.5D, 0.0D, 0.5D);
		stack.translate(0.0F, 3.25F + Mth.sin(state.bobTicks * 0.1F) * 0.1F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(-state.rotation * (180F / (float) Math.PI) - 90F));
		stack.mulPose(Axis.ZP.rotationDegrees(180));
		stack.scale(1.5F, 1.5F, 1.5F);
		collector.submitCustomGeometry(stack, TEXTURE, (pose, consumer) ->
				RatsRenderType.withSubmitPose(pose, this.scratch, s ->
						MODEL_SPIRIT.renderToBuffer(s, consumer, 244, OverlayTexture.NO_OVERLAY, 0x80FFFFFF)));
		stack.popPose();
	}
}
