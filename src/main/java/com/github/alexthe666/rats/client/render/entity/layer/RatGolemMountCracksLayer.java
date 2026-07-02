package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.mount.RatGolemMount;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class RatGolemMountCracksLayer extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<RatGolemMount>> {
	private static final Map<RatGolemMount.Cracks, Identifier> CRACK_MAP = ImmutableMap.of(RatGolemMount.Cracks.LOW, Identifier.parse("textures/entity/iron_golem/iron_golem_crackiness_low.png"), RatGolemMount.Cracks.MEDIUM, Identifier.parse("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), RatGolemMount.Cracks.HIGH, Identifier.parse("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

	public RatGolemMountCracksLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<RatGolemMount>> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!state.isInvisible && RatsClientKeys.getLiving(state) instanceof RatGolemMount entity) {
			RatGolemMount.Cracks cracks = entity.getCracks();
			if (cracks != RatGolemMount.Cracks.NONE) {
				Identifier resourcelocation = CRACK_MAP.get(cracks);
				// 26.1: the vanilla helper takes a packed ARGB color plus a submit order and defers the draw;
				// the bridge re-runs the Citadel setupAnim from the state at draw time.
				renderColoredCutoutModel(this.getParentModel(), resourcelocation, stack, collector, light, state, -1, 0);
			}
		}
	}
}
