package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.rat.Rat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class RatOverlayLayer extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<Rat>> {

	// 26.1: entityNoOutline was removed; entityTranslucent with affectsOutline=false is the equivalent no-outline pass.
	private static final RenderType PLAGUE_TEX = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/plague_overlay.png"), false);
	// 26.1: entitySmoothCutout was removed; entityCutout (now no-cull) is the closest cutout variant.
	private static final RenderType TOGA_TEX = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/upgrades/toga.png"));

	public RatOverlayLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<Rat>> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!(RatsClientKeys.getLiving(state) instanceof Rat rat)) {
			return;
		}
		this.getParentModel().setupAnim(state);
		if (rat.hasPlague()) {
			collector.submitCustomGeometry(stack, PLAGUE_TEX, (pose, consumer) ->
					this.getParentModel().renderCitadelToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, -1));
		}
		if (rat.hasToga()) {
			collector.submitCustomGeometry(stack, TOGA_TEX, (pose, consumer) ->
					this.getParentModel().renderCitadelToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, -1));
		}
	}
}
