package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.client.model.entity.RatlanteanAutomatonModel;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.registry.RatlantisItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GlowingOverlayLayer<T extends LivingEntity> extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<T>> {
	private final RenderType renderType;

	public GlowingOverlayLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<T>> parent, Identifier texture) {
		super(parent);
		this.renderType = RenderTypes.eyes(texture);
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

		if (this.getParentModel().citadel() instanceof RatlanteanAutomatonModel<?> automaton) {
			stack.pushPose();
			automaton.armLeft1.translateAndRotate(stack);
			automaton.armLeft2.translateAndRotate(stack);
			automaton.drillArm2.translateAndRotate(stack);
			automaton.drilArm3.translateAndRotate(stack);
			automaton.blade.translateAndRotate(stack);
			stack.mulPose(Axis.YP.rotationDegrees(90));

			// 26.1: ItemRenderer.renderStatic is gone; items render through ItemStackRenderState.
			ItemStackRenderState sawblade = new ItemStackRenderState();
			Minecraft mc = Minecraft.getInstance();
			mc.getItemModelResolver().updateForTopItem(sawblade, new ItemStack(RatlantisItemRegistry.ANCIENT_SAWBLADE.get()), ItemDisplayContext.FIXED, mc.level instanceof ClientLevel cl ? cl : null, null, 0);
			sawblade.submit(stack, collector, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
			stack.popPose();
		}
	}
}
