package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.block.AutoCurdlerModel;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.server.block.AutoCurdlerBlock;
import com.github.alexthe666.rats.server.block.entity.AutoCurdlerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class AutoCurdlerRenderer implements BlockEntityRenderer<AutoCurdlerBlockEntity, AutoCurdlerRenderer.AutoCurdlerRenderState> {
	private static final AutoCurdlerModel<?> MODEL_AUTO_CURDLER = new AutoCurdlerModel<>();
	private static final RenderType TEXTURE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/auto_curdler.png"));
	// 26.1: entitySmoothCutout and InventoryMenu.BLOCK_ATLAS are gone; entityCutout (now no-cull) over the block atlas is the equivalent.
	private static final RenderType TEXTURE_BLOCKS = RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS);

	private final PoseStack scratch = new PoseStack();

	public static final class AutoCurdlerRenderState extends BlockEntityRenderState {
		public float rotation;
		public int fluidAmount;
		@Nullable
		public TextureAtlasSprite fluidSprite;
	}

	public AutoCurdlerRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public AutoCurdlerRenderState createRenderState() {
		return new AutoCurdlerRenderState();
	}

	@Override
	public void extractRenderState(AutoCurdlerBlockEntity entity, AutoCurdlerRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPos, breakProgress);
		state.rotation = 0;
		if (entity.getLevel() != null && entity.getLevel().getBlockState(entity.getBlockPos()).getBlock() instanceof AutoCurdlerBlock) {
			state.rotation = entity.getLevel().getBlockState(entity.getBlockPos()).getValue(AutoCurdlerBlock.FACING).getClockWise().toYRot() + 90;
		}
		FluidStack fluidStack = entity.getTank().getFluid();
		state.fluidAmount = fluidStack.getAmount();
		state.fluidSprite = null;
		if (!fluidStack.isEmpty()) {
			// 26.1: IClientFluidTypeExtensions lost its texture hooks; fluid sprites now come from the baked FluidModel.
			state.fluidSprite = Minecraft.getInstance().getModelManager().getFluidStateModelSet()
					.get(fluidStack.getFluid().defaultFluidState()).stillMaterial().sprite();
		}
	}

	@Override
	public void submit(AutoCurdlerRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		int light = state.lightCoords;
		int overlay = OverlayTexture.NO_OVERLAY;
		stack.pushPose();
		stack.translate(0.5D, 1.5D, 0.5D);
		stack.mulPose(Axis.YP.rotationDegrees(-state.rotation));
		stack.mulPose(Axis.ZP.rotationDegrees(180));
		stack.pushPose();
		if (state.fluidSprite != null) {
			TextureAtlasSprite sprite = state.fluidSprite;
			int amount = state.fluidAmount;
			stack.pushPose();
			stack.mulPose(Axis.XP.rotationDegrees(180));
			stack.translate(-0.5F, -1.6F, -0.5F);
			collector.submitCustomGeometry(stack, TEXTURE_BLOCKS, (pose, consumer) ->
					renderMilk(consumer, pose.pose(), sprite, amount, light, overlay));
			stack.popPose();
		}
		collector.submitCustomGeometry(stack, TEXTURE, (pose, consumer) ->
				RatsRenderType.withSubmitPose(pose, this.scratch, s ->
						MODEL_AUTO_CURDLER.renderToBuffer(s, consumer, light, overlay, 0xFFFFFFFF)));
		stack.popPose();
		stack.popPose();
	}

	private static void renderMilk(VertexConsumer vertexbuffer, Matrix4f matrix4f, TextureAtlasSprite sprite, int fluidAmount, int combinedLight, int overlay) {
		float textureYPos = (0.6F * (fluidAmount / 5000F));
		AABB boundingBox = new AABB(0.25F, 0.35F, 0.25F, 0.75F, 0.6F + textureYPos, 0.75F);
		double avgY = boundingBox.maxY - boundingBox.minY;
		double avgX = Math.abs(boundingBox.maxX - boundingBox.minX);
		double avgZ = Math.abs(boundingBox.maxZ - boundingBox.minZ);
		float f1 = sprite.getU0();
		float f2_alt_x = (float) Math.min(sprite.getU1(), f1 + avgX * Math.abs(sprite.getU1() - sprite.getU0()));
		float f2_alt_z = (float) Math.min(sprite.getU1(), f1 + avgZ * Math.abs(sprite.getU1() - sprite.getU0()));
		float f3 = sprite.getV0();
		float f4_alt = (float) Math.min(sprite.getV1(), f3 + avgY * Math.abs(sprite.getV1() - sprite.getV0()));
		float f4_alt_z = (float) Math.min(sprite.getV1(), f3 + avgZ * Math.abs(sprite.getV1() - sprite.getV0()));
		//back
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f2_alt_x, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, -1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, -1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, -1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f2_alt_x, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, -1.0F);
		//front
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, 1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f2_alt_x, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, 1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f2_alt_x, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, 1.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 0.0F, 1.0F);
		//tops
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, f4_alt_z).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, -1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f2_alt_x, f4_alt_z).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, -1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f2_alt_x, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, -1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, -1.0F, 0.0F);

		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, f4_alt_z).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f2_alt_x, f4_alt_z).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f2_alt_x, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 1.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(0.0F, 1.0F, 0.0F);
		//sides
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f2_alt_z, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(-1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f2_alt_z, f3).setOverlay(overlay).setLight(combinedLight).setNormal(-1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(-1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f1, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(-1.0F, 0.0F, 0.0F);

		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f2_alt_z, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, 255).setUv(f2_alt_z, f3).setOverlay(overlay).setLight(combinedLight).setNormal(1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, f3).setOverlay(overlay).setLight(combinedLight).setNormal(1.0F, 0.0F, 0.0F);
		vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, 255).setUv(f1, f4_alt).setOverlay(overlay).setLight(combinedLight).setNormal(1.0F, 0.0F, 0.0F);
	}
}
