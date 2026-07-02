package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.block.TrashCanModel;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.server.block.TrashCanBlock;
import com.github.alexthe666.rats.server.block.entity.TrashCanBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TrashCanRenderer implements BlockEntityRenderer<TrashCanBlockEntity, TrashCanRenderer.TrashCanRenderState> {
	private static final TrashCanModel<?> MODEL_TRASH_CAN = new TrashCanModel<>();
	// 26.1: entityCutoutNoCull was removed; entityCutout's pipeline no longer culls
	private static final RenderType TEXTURE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/trash_can.png"), true);

	private final PoseStack scratch = new PoseStack();

	public static final class TrashCanRenderState extends BlockEntityRenderState {
		public TrashCanBlockEntity entity;
		public float rotation;
	}

	public TrashCanRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public TrashCanRenderState createRenderState() {
		return new TrashCanRenderState();
	}

	@Override
	public void extractRenderState(TrashCanBlockEntity entity, TrashCanRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPos, breakProgress);
		state.entity = entity;
		state.rotation = 0;
		if (entity.getLevel() != null && entity.getLevel().getBlockState(entity.getBlockPos()).getBlock() instanceof TrashCanBlock) {
			state.rotation = entity.getLevel().getBlockState(entity.getBlockPos()).getValue(TrashCanBlock.FACING).toYRot();
		}
	}

	@Override
	public void submit(TrashCanRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		int light = state.lightCoords;
		stack.pushPose();
		stack.translate(0.5D, 1.501D, 0.5D);
		stack.mulPose(Axis.XP.rotationDegrees(180));
		stack.mulPose(Axis.YP.rotationDegrees(state.rotation));
		MODEL_TRASH_CAN.animate(state.entity);
		collector.submitCustomGeometry(stack, TEXTURE, (pose, consumer) ->
				RatsRenderType.withSubmitPose(pose, this.scratch, s ->
						MODEL_TRASH_CAN.renderToBuffer(s, consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF)));
		stack.popPose();
	}
}
