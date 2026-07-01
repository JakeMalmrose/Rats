package com.github.alexthe666.rats.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// TODO(26.1): these staff-visualization helpers drew world-space quads with the immediate-mode
// pipeline (RenderSystem shader state + BufferUploader.drawWithShader), which was removed with the
// render-pipeline rework. Their caller (ForgeClientEvents.onRenderWorld) is stubbed for the same
// reason; reimplement both on the submit/level-render-state path once the port settles.
public class RatsIconRenderUtil {

	public static void renderPOIIcon(Identifier icon, Vec3 viewVec, @Nullable BlockPos renderPos, float bob, PoseStack stack, Tesselator tesselator) {
	}

	public static void renderBox(Identifier texture, Vec3 viewPos, Vec3 centerPos, AABB boxSize, PoseStack stack) {
	}

	public static void renderMovingAABB(AABB boundingBox, PoseStack stack) {
	}
}
