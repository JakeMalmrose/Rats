package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.monster.GhostPirat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class GhostPiratRenderer extends AbstractRatRenderer<GhostPirat> {

	private static final Identifier BASE_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ghost_pirat/ghost_pirat.png");

	public GhostPiratRenderer(EntityRendererProvider.Context context) {
		super(context, new RatModel<>());
		this.shadowRadius = 0.35F;
		this.addLayer(new GhostPiratLayer(this));
	}

	@Override
	protected void scale(LivingEntityRenderState state, PoseStack stack) {
		super.scale(state, stack);
		stack.scale(2.0F, 2.0F, 2.0F);
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return BASE_TEXTURE;
	}

	private static class GhostPiratLayer extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<GhostPirat>> {
		private static final Identifier GHOST_OVERLAY = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ghost_pirat/ghost_pirat_overlay.png");

		public GhostPiratLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<GhostPirat>> parent) {
			super(parent);
		}

		@Override
		public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
			float f = state.ageInTicks;
			this.getParentModel().setupAnim(state);
			collector.submitCustomGeometry(stack, RenderTypes.energySwirl(GHOST_OVERLAY, f * 0.01F, f * 0.01F), (pose, consumer) -> {
					// re-pose the shared model at draw time: other entities re-run setupAnim between submit and draw
					this.getParentModel().setupAnim(state);
					this.getParentModel().renderCitadelToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY, 0xFF808080);
				});
		}
	}
}
