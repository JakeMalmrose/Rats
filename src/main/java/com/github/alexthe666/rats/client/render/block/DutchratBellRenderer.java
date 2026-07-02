package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.server.block.entity.DutchratBellBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.bell.BellModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BellRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class DutchratBellRenderer implements BlockEntityRenderer<DutchratBellBlockEntity, BellRenderState> {

	private final BellModel model;
	private static final RenderType TEXTURE = RatsRenderType.getGlowingTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/dutchrat_bell.png"));

	public DutchratBellRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new BellModel(context.bakeLayer(ModelLayers.BELL));
	}

	@Override
	public BellRenderState createRenderState() {
		return new BellRenderState();
	}

	@Override
	public void extractRenderState(DutchratBellBlockEntity bell, BellRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(bell, state, partialTicks, cameraPos, breakProgress);
		state.ticks = (float) bell.ticks + partialTicks;
		state.shakeDirection = bell.shaking ? bell.clickDirection : null;
	}

	@Override
	public void submit(BellRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		// vanilla BellModel reproduces the 1.20 shake math; tint carries the old 50% alpha
		BellModel.State modelState = new BellModel.State(state.ticks, state.shakeDirection);
		this.model.setupAnim(modelState);
		collector.submitModel(this.model, modelState, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, 0x80FFFFFF, null, 0, state.breakProgress);
	}
}
