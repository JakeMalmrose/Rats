package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.CubeModel;
import com.github.alexthe666.rats.client.model.RatsModelLayers;
import com.github.alexthe666.rats.server.entity.projectile.ThrownBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class ThrownBlockRenderer extends EntityRenderer<ThrownBlock, ThrownBlockRenderer.ThrownBlockRenderState> {
	private static final Identifier LIGHTNING_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/psychic.png");
	private final CubeModel cube;

	public ThrownBlockRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.cube = new CubeModel(context.bakeLayer(RatsModelLayers.THROWN_BLOCK));
		this.shadowRadius = 0.5F;
	}

	@Override
	public void submit(ThrownBlockRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		float f = state.ageInTicks;
		if (state.hasBlock && state.movingBlockRenderState.blockState.getRenderShape() == RenderShape.MODEL) {
			stack.pushPose();
			stack.translate(-0.5D, 0, 0.5D);
			stack.mulPose(Axis.YP.rotationDegrees(90.0F));
			collector.submitMovingBlock(stack, state.movingBlockRenderState);
			stack.popPose();
		}

		stack.pushPose();
		stack.scale(1F, -1F, 1F);
		stack.translate(0F, -0.5F, 0F);
		stack.mulPose(Axis.YP.rotationDegrees(state.yRot - 180));
		collector.submitModel(this.cube, Unit.INSTANCE, stack, RenderTypes.energySwirl(LIGHTNING_TEXTURE, f * 0.01F, f * 0.01F),
				state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
		stack.popPose();
		super.submit(state, stack, collector, camera);
	}

	@Override
	public ThrownBlockRenderState createRenderState() {
		return new ThrownBlockRenderState();
	}

	@Override
	public void extractRenderState(ThrownBlock entity, ThrownBlockRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.yRot = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks;
		BlockState heldState = entity.getHeldBlockState();
		state.hasBlock = heldState != null;
		if (heldState != null) {
			BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
			state.movingBlockRenderState.randomSeedPos = entity.blockPosition();
			state.movingBlockRenderState.blockPos = pos;
			state.movingBlockRenderState.blockState = heldState;
			if (entity.level() instanceof ClientLevel clientLevel) {
				state.movingBlockRenderState.biome = clientLevel.getBiome(pos);
				state.movingBlockRenderState.cardinalLighting = clientLevel.cardinalLighting();
				state.movingBlockRenderState.lightEngine = clientLevel.getLightEngine();
			}
		}
	}

	public static class ThrownBlockRenderState extends EntityRenderState {
		public float yRot;
		public boolean hasBlock;
		public final MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
	}
}
