package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.block.RatTrapModel;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.server.block.RatTrapBlock;
import com.github.alexthe666.rats.server.block.entity.RatTrapBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RatTrapRenderer implements BlockEntityRenderer<RatTrapBlockEntity, RatTrapRenderer.RatTrapRenderState> {
	private static final RatTrapModel<?> MODEL_RAT_TRAP = new RatTrapModel<>();
	private static final RenderType TEXTURE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/rat_trap.png"));

	private final ItemModelResolver itemModelResolver;
	private final PoseStack scratch = new PoseStack();

	public static final class RatTrapRenderState extends BlockEntityRenderState {
		public float rotation;
		public float shutProgress;
		public ItemStackRenderState bait = new ItemStackRenderState();
	}

	public RatTrapRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public RatTrapRenderState createRenderState() {
		return new RatTrapRenderState();
	}

	@Override
	public void extractRenderState(RatTrapBlockEntity entity, RatTrapRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPos, breakProgress);
		state.rotation = 0;
		state.shutProgress = 0;
		ItemStack bait = ItemStack.EMPTY;
		if (entity.getLevel() != null && entity.getLevel().getBlockState(entity.getBlockPos()).getBlock() instanceof RatTrapBlock) {
			state.rotation = entity.getLevel().getBlockState(entity.getBlockPos()).getValue(RatTrapBlock.FACING).toYRot();
			state.shutProgress = entity.shutProgress;
			bait = entity.getBait();
		}
		// 26.1: ItemRenderer.renderStatic is gone; items render through ItemStackRenderState.
		this.itemModelResolver.updateForTopItem(state.bait, bait, ItemDisplayContext.FIXED, entity.getLevel(), null, 0);
	}

	@Override
	public void submit(RatTrapRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		int light = state.lightCoords;
		stack.pushPose();
		stack.translate(0.5D, 1.5D, 0.5D);

		stack.mulPose(Axis.XP.rotationDegrees(180));
		stack.mulPose(Axis.YP.rotationDegrees(state.rotation));
		MODEL_RAT_TRAP.animateHinge(state.shutProgress);
		collector.submitCustomGeometry(stack, TEXTURE, (pose, consumer) ->
				RatsRenderType.withSubmitPose(pose, this.scratch, s ->
						MODEL_RAT_TRAP.renderToBuffer(s, consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF)));
		if (!state.bait.isEmpty()) {
			stack.scale(0.4F, 0.4F, 0.4F);
			stack.translate(0, 3.4F, -0.5F);
			stack.mulPose(Axis.XP.rotationDegrees(90));
			stack.mulPose(Axis.YP.rotationDegrees(180));
			state.bait.submit(stack, collector, light, OverlayTexture.NO_OVERLAY, 0);
		}
		stack.popPose();
	}
}
