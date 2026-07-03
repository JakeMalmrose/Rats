package com.github.alexthe666.rats.client.util;

import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

/**
 * 26.1: rewritten from immediate-mode drawing (RenderSystem + BufferUploader.drawWithShader, removed
 * with the render-pipeline rework) onto the submit pipeline. Callers pass the SubmitCustomGeometryEvent's
 * PoseStack/collector plus the camera state; positions are world-space and translated camera-relative here.
 */
public class RatsIconRenderUtil {

	private static final int FULLBRIGHT = 15728880;

	public static void renderPOIIcon(Identifier icon, Vec3 cameraPos, @Nullable BlockPos renderPos, float bob, PoseStack stack, SubmitNodeCollector collector, Quaternionf cameraOrientation) {
		if (renderPos != null && !renderPos.equals(BlockPos.ZERO)) {
			stack.pushPose();
			stack.translate(renderPos.getX() + 0.5D - cameraPos.x(), renderPos.getY() + bob - cameraPos.y(), renderPos.getZ() + 0.5D - cameraPos.z());
			stack.mulPose(cameraOrientation);
			collector.submitCustomGeometry(stack, RatsRenderType.getGlowingTranslucent(icon), (pose, consumer) -> {
				vertex(consumer, pose, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F);
				vertex(consumer, pose, -0.5F, 0.5F, 0.0F, 1.0F, 0.0F);
				vertex(consumer, pose, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F);
				vertex(consumer, pose, 0.5F, -0.5F, 0.0F, 0.0F, 1.0F);
			});
			stack.popPose();
		}
	}

	public static void renderBox(Identifier texture, Vec3 cameraPos, Vec3 centerPos, AABB boxSize, PoseStack stack, SubmitNodeCollector collector) {
		stack.pushPose();
		stack.translate(centerPos.x() - cameraPos.x(), centerPos.y() - cameraPos.y(), centerPos.z() - cameraPos.z());
		collector.submitCustomGeometry(stack, RatsRenderType.getGlowingTranslucent(texture), (pose, consumer) ->
				renderMovingAABB(boxSize, pose, consumer));
		stack.popPose();
	}

	public static void renderMovingAABB(AABB boundingBox, PoseStack.Pose pose, VertexConsumer consumer) {
		float f3 = Minecraft.getInstance().isPaused() ? 0.0F : (float) (System.currentTimeMillis() % 3000L) / 3000.0F;
		float maxX = (float) boundingBox.maxX * 0.125F;
		float minX = (float) boundingBox.minX * 0.125F;
		float maxY = (float) boundingBox.maxY * 0.125F;
		float minY = (float) boundingBox.minY * 0.125F;
		float maxZ = (float) boundingBox.maxZ * 0.125F;
		float minZ = (float) boundingBox.minZ * 0.125F;
		//north
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ, f3 + minX - maxX, f3 + maxY - minY);
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ, f3 + maxX - minX, f3 + maxY - minY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ, f3 + maxX - minX, f3 + minY - maxY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ, f3 + minX - maxX, f3 + minY - maxY);
		//south
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ, f3 + minX - maxX, f3 + minY - maxY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ, f3 + maxX - minX, f3 + minY - maxY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ, f3 + maxX - minX, f3 + maxY - minY);
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ, f3 + minX - maxX, f3 + maxY - minY);
		//bottom
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ, f3 + minX - maxX, f3 + minY - maxY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ, f3 + maxX - minX, f3 + minY - maxY);
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ, f3 + maxX - minX, f3 + maxZ - minZ);
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ, f3 + minX - maxX, f3 + maxZ - minZ);
		//top
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ, f3 + minX - maxX, f3 + minY - maxY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ, f3 + maxX - minX, f3 + minY - maxY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ, f3 + maxX - minX, f3 + maxZ - minZ);
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ, f3 + minX - maxX, f3 + maxZ - minZ);
		//west
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ, f3 + minX - maxX, f3 + minY - maxY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ, f3 + minX - maxX, f3 + maxY - minY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ, f3 + maxX - minX, f3 + maxY - minY);
		vertex(consumer, pose, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ, f3 + maxX - minX, f3 + minY - maxY);
		//east
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ, f3 + minX - maxX, f3 + minY - maxY);
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ, f3 + minX - maxX, f3 + maxY - minY);
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ, f3 + maxX - minX, f3 + maxY - minY);
		vertex(consumer, pose, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ, f3 + maxX - minX, f3 + minY - maxY);
	}

	private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v) {
		consumer.addVertex(pose, x, y, z)
				.setColor(255, 255, 255, 255)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(FULLBRIGHT)
				.setNormal(pose, 0.0F, 1.0F, 0.0F);
	}
}
