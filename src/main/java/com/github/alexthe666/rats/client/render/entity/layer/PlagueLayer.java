package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.registry.RatsEffectRegistry;
import com.github.alexthe666.rats.server.entity.rat.AbstractRat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public class PlagueLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {

	private static final RenderType TEXTURE = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/misc/plague_overlay.png"));

	public PlagueLayer(RenderLayerParent<S, M> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, S state, float netHeadYaw, float headPitch) {
		LivingEntity entity = RatsClientKeys.getLiving(state);
		if (entity != null && !(entity instanceof AbstractRat) && entity.hasEffect(RatsEffectRegistry.PLAGUE)) {
			// 26.1: submitModel re-runs the parent model's setupAnim from the state at draw time, so this
			// works both for vanilla models and for the Citadel bridge on modded renderers.
			collector.submitModel(this.getParentModel(), state, stack, TEXTURE, light, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
		}
	}
}
