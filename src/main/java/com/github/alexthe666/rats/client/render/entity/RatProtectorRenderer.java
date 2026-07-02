package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatModel;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.server.entity.misc.RatProtector;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class RatProtectorRenderer extends AbstractRatRenderer<RatProtector> {

	public static final Identifier BASE_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat_protector.png");

	public RatProtectorRenderer(EntityRendererProvider.Context context) {
		super(context, new RatModel<>());
		this.addLayer(new Overlay(this));
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return BASE_TEXTURE;
	}

	private static class Overlay extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<RatProtector>> {

		public Overlay(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<RatProtector>> parent) {
			super(parent);
		}

		@Override
		public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
			this.getParentModel().setupAnim(state);
			collector.submitCustomGeometry(stack, RatsRenderType.getYellowGlint(), (pose, consumer) ->
					this.getParentModel().renderCitadelToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, 0xFF808080));
		}
	}
}
