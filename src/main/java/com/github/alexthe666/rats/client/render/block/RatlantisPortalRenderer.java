package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.server.block.entity.RatlantisPortalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

public class RatlantisPortalRenderer extends AbstractEndPortalRenderer<RatlantisPortalBlockEntity, EndPortalRenderState> {
	public static final Identifier PORTAL_BG = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/environment/ratlantis_sky_portal.png");
	public static final Identifier PORTAL_FG = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/environment/ratlantis_portal.png");

	public RatlantisPortalRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public EndPortalRenderState createRenderState() {
		return new EndPortalRenderState();
	}

	@Override
	public void submit(EndPortalRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		// unlike vanilla's end portal this fills the full block cube, matching the 1.20 renderer
		submitCube(state.facesToShow, RatsRenderType.getRatlantisPortal(), poseStack, collector);
	}
}
