package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public class BasicOverlayLayer<T extends LivingEntity> extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<T>> {
	private final RenderType renderType;

	public BasicOverlayLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<T>> parent, Identifier texture) {
		super(parent);
		// 26.1: entityNoOutline was removed; entityTranslucent with affectsOutline=false is the equivalent no-outline pass.
		this.renderType = RenderTypes.entityTranslucent(texture, false);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		this.getParentModel().setupAnim(state);
		// capture the bridge at submit time — the renderer swaps this.model per entity (adult vs
		// pinkie), so getParentModel() at draw time can be another rat's model (ghost-baby overlay)
		final var submitModel = this.getParentModel();
		collector.submitCustomGeometry(stack, this.renderType, (pose, consumer) -> {
				// re-pose the shared model at draw time: other entities re-run setupAnim between submit and draw
				submitModel.setupAnim(state);
				submitModel.renderCitadelToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY, -1);
			});
	}
}
