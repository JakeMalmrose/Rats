package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.server.block.RatHoleBlock;
import com.github.alexthe666.rats.server.block.entity.RatHoleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RatHoleRenderer implements BlockEntityRenderer<RatHoleBlockEntity, RatHoleRenderer.RatHoleRenderState> {

	private static final AABB TOP_AABB = new AABB(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
	private static final AABB WEST_CONNECT_AABB = new AABB(0.0F, 0.0F, 0.25F, 0.25F, 0.5F, 0.75F);
	private static final AABB EAST_CONNECT_AABB = new AABB(0.75F, 0.0F, 0.25F, 1.0F, 0.5F, 0.75F);
	private static final AABB NORTH_CONNECT_AABB = new AABB(0.25F, 0.0F, 0.0F, 0.75F, 0.5F, 0.25F);
	private static final AABB SOUTH_CONNECT_AABB = new AABB(0.25F, 0.0F, 0.75F, 0.75F, 0.5F, 1.0F);

	private static final AABB NORTH_CORNER_AABB = new AABB(0.0F, 0.0F, -0.0F, 0.25F, 0.5F, 0.25F);
	private static final AABB EAST_CORNER_AABB = new AABB(0.75F, 0.0F, -0.0F, 1.0F, 0.5F, 0.25F);
	private static final AABB SOUTH_CORNER_AABB = new AABB(0.0F, 0.0F, 0.75F, 0.25F, 0.5F, 1.0F);
	private static final AABB WEST_CORNER_AABB = new AABB(0.75F, 0.0F, 0.75F, 1.0F, 0.5F, 1.0F);

	// 26.1: entitySmoothCutout and InventoryMenu.BLOCK_ATLAS are gone; entityCutout (now no-cull) over the block atlas is the equivalent.
	private static final RenderType TEXTURE = RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS);

	public static final class RatHoleRenderState extends BlockEntityRenderState {
		public boolean connectedNorth;
		public boolean connectedEast;
		public boolean connectedSouth;
		public boolean connectedWest;
		public TextureAtlasSprite sprite;
		@Nullable
		public BlockStateModel breakingModel;
		public long breakingSeed;
	}

	public RatHoleRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public int getViewDistance() {
		return 256;
	}

	@Override
	public RatHoleRenderState createRenderState() {
		return new RatHoleRenderState();
	}

	@Override
	public void extractRenderState(RatHoleBlockEntity entity, RatHoleRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPos, breakProgress);
		BlockState imitated = Blocks.OAK_PLANKS.defaultBlockState();
		state.connectedNorth = false;
		state.connectedEast = false;
		state.connectedSouth = false;
		state.connectedWest = false;
		if (entity.getLevel() != null && entity.getLevel().getBlockState(entity.getBlockPos()).getBlock() instanceof RatHoleBlock) {
			BlockState actualState = entity.getBlockState();
			state.connectedNorth = actualState.getValue(RatHoleBlock.NORTH);
			state.connectedEast = actualState.getValue(RatHoleBlock.EAST);
			state.connectedSouth = actualState.getValue(RatHoleBlock.SOUTH);
			state.connectedWest = actualState.getValue(RatHoleBlock.WEST);
			imitated = entity.getImitatedBlockState();
		}
		// 26.1: BakedModel#getParticleIcon(ModelData) is gone; particle sprites come from the block state model set.
		state.sprite = Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(imitated).sprite();
		// 26.1: BlockRenderDispatcher#renderBreakingTexture is gone; the crumbling overlay is a deferred submit instead.
		state.breakingModel = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(entity.getBlockState());
		state.breakingSeed = entity.getBlockState().getSeed(entity.getBlockPos());
	}

	private void renderAABB(VertexConsumer vertexbuffer, PoseStack.Pose pose, AABB boundingBox, TextureAtlasSprite sprite, int light, int overlay) {
		double avgY = boundingBox.maxY - boundingBox.minY;
		double avgX = boundingBox.maxX - boundingBox.minX;
		double avgZ = boundingBox.maxZ - boundingBox.minZ;

		float f1 = (float) ((sprite.getX() + (boundingBox.minX * 16.0D)) / (sprite.getX() / sprite.getU0()));
		float maxUX = (float) Math.min(sprite.getU1(), f1 + avgX * Math.abs(sprite.getU1() - sprite.getU0()));
		float f2 = (float) ((sprite.getX() + (boundingBox.minZ * 16.0D)) / (sprite.getX() / sprite.getU0()));
		float maxUZ = (float) Math.min(sprite.getU1(), f2 + avgZ * Math.abs(sprite.getU1() - sprite.getU0()));
		float f3 = (float) ((sprite.getY() + (sprite.contents().height() - (boundingBox.maxY * 16.0D))) / (sprite.getY() / sprite.getV0()));
		float maxVY = (float) Math.min(sprite.getV1(), f3 + avgY * Math.abs(sprite.getV1() - sprite.getV0()));
		float maxVZ = (float) Math.min(sprite.getV1(), f3 + avgZ * Math.abs(sprite.getV1() - sprite.getV0()));
		org.joml.Matrix4f matrix4f = pose.pose();
		//back
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(maxUX, f3).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 0.0F, 1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 0.0F, 1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, maxVY).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 0.0F, 1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(maxUX, maxVY).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 0.0F, 1.0F);
		//front
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, maxVY).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 0.0F, -1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(maxUX, maxVY).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 0.0F, -1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(maxUX, f3).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 0.0F, -1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 0.0F, -1.0F);
		//tops
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, maxVZ).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, -1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(maxUX, maxVZ).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, -1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(maxUX, f3).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, -1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, -1.0F, 0.0F);

		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, maxVZ).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(maxUX, maxVZ).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(maxUX, f3).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
		//sides
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(maxUZ, maxVY).setOverlay(overlay).setLight(light).setNormal(pose, -1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(maxUZ, f3).setOverlay(overlay).setLight(light).setNormal(pose, -1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f2, f3).setOverlay(overlay).setLight(light).setNormal(pose, -1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f2, maxVY).setOverlay(overlay).setLight(light).setNormal(pose, -1.0F, 0.0F, 0.0F);

		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(maxUZ, maxVY).setOverlay(overlay).setLight(light).setNormal(pose, 1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(maxUZ, f3).setOverlay(overlay).setLight(light).setNormal(pose, 1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f2, f3).setOverlay(overlay).setLight(light).setNormal(pose, 1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f2, maxVY).setOverlay(overlay).setLight(light).setNormal(pose, 1.0F, 0.0F, 0.0F);
	}

	@Override
	public void submit(RatHoleRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		TextureAtlasSprite sprite = state.sprite;
		int light = state.lightCoords;
		int overlay = OverlayTexture.NO_OVERLAY;
		stack.pushPose();
		collector.submitCustomGeometry(stack, TEXTURE, (pose, consumer) -> {
			this.renderAABB(consumer, pose, TOP_AABB, sprite, light, overlay);
			this.renderAABB(consumer, pose, EAST_CORNER_AABB, sprite, light, overlay);
			this.renderAABB(consumer, pose, WEST_CORNER_AABB, sprite, light, overlay);
			this.renderAABB(consumer, pose, NORTH_CORNER_AABB, sprite, light, overlay);
			this.renderAABB(consumer, pose, SOUTH_CORNER_AABB, sprite, light, overlay);
			if (state.connectedEast) {
				this.renderAABB(consumer, pose, EAST_CONNECT_AABB, sprite, light, overlay);
			}
			if (state.connectedWest) {
				this.renderAABB(consumer, pose, WEST_CONNECT_AABB, sprite, light, overlay);
			}
			if (state.connectedNorth) {
				this.renderAABB(consumer, pose, NORTH_CONNECT_AABB, sprite, light, overlay);
			}
			if (state.connectedSouth) {
				this.renderAABB(consumer, pose, SOUTH_CONNECT_AABB, sprite, light, overlay);
			}
		});
		if (state.breakProgress != null && state.breakingModel != null) {
			collector.submitBreakingBlockModel(stack, state.breakingModel, state.breakingSeed, state.breakProgress.progress());
		}
		stack.popPose();
	}
}
