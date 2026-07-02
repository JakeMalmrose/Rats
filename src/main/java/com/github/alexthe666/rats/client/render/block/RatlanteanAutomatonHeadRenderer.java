package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatlanteanAutomatonModel;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.server.block.RatlanteanAutomatonHeadBlock;
import com.github.alexthe666.rats.server.block.entity.RatlanteanAutomatonHeadBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RatlanteanAutomatonHeadRenderer implements BlockEntityRenderer<RatlanteanAutomatonHeadBlockEntity, RatlanteanAutomatonHeadRenderer.AutomatonHeadRenderState> {
	private static final RatlanteanAutomatonModel<?> AUTOMATON_MODEL = new RatlanteanAutomatonModel<>(false);
	private static final RenderType GOLEM_TEXTURE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ratlantean_automaton/ratlantean_automaton.png"));
	private static final RenderType GLOW_TEXTURE = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ratlantean_automaton/ratlantean_automaton_glow.png"));

	private final PoseStack scratch = new PoseStack();

	public static final class AutomatonHeadRenderState extends BlockEntityRenderState {
		public boolean shouldRender;
		public float rotation;
		public float tickCount;
	}

	public RatlanteanAutomatonHeadRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public AutomatonHeadRenderState createRenderState() {
		return new AutomatonHeadRenderState();
	}

	@Override
	public void extractRenderState(RatlanteanAutomatonHeadBlockEntity te, AutomatonHeadRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPos, breakProgress);
		state.shouldRender = false;
		if (te.getLevel() != null) {
			if (te.getLevel().getBlockState(te.getBlockPos()).getBlock() instanceof RatlanteanAutomatonHeadBlock) {
				state.shouldRender = true;
				state.rotation = te.getLevel().getBlockState(te.getBlockPos()).getValue(RatlanteanAutomatonHeadBlock.FACING).toYRot();
				state.tickCount = te.tickCount + partialTicks;
			}
		} else if (Minecraft.getInstance().player != null) {
			state.shouldRender = true;
			state.rotation = 0;
			state.tickCount = Minecraft.getInstance().player.tickCount + partialTicks;
		}
	}

	@Override
	public void submit(AutomatonHeadRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (!state.shouldRender) {
			return;
		}
		int light = state.lightCoords;
		int overlay = OverlayTexture.NO_OVERLAY;
		stack.pushPose();
		stack.translate(0.5F, -0.5F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180));
		stack.mulPose(Axis.YP.rotationDegrees(state.rotation));
		AUTOMATON_MODEL.setTERotationAngles(state.tickCount);
		collector.submitCustomGeometry(stack, GOLEM_TEXTURE, (pose, consumer) ->
				RatsRenderType.withSubmitPose(pose, this.scratch, s ->
						AUTOMATON_MODEL.renderHead(s, consumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F)));
		collector.submitCustomGeometry(stack, GLOW_TEXTURE, (pose, consumer) ->
				RatsRenderType.withSubmitPose(pose, this.scratch, s ->
						AUTOMATON_MODEL.renderHead(s, consumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F)));
		stack.popPose();
	}
}
