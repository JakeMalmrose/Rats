package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.ChangesTextureUpgrade;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.GlowingEyesUpgrade;
import com.github.alexthe666.rats.server.misc.RatUpgradeUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TamedRatEyesLayer extends RatEyesLayer<TamedRat> {
	public TamedRatEyesLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<TamedRat>> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!(RatsClientKeys.getLiving(state) instanceof TamedRat rat)) {
			return;
		}
		if (RatUpgradeUtils.forEachUpgradeBool(rat, item -> item instanceof GlowingEyesUpgrade, false)) {
			AtomicBoolean skip = new AtomicBoolean(false);
			AtomicReference<RenderType> tex = new AtomicReference<>(EYES);
			RatUpgradeUtils.forEachUpgrade(rat, item -> item instanceof GlowingEyesUpgrade, (stack1, slot) -> {
				if (rat.isSlotVisible(slot)) {
					tex.set(((GlowingEyesUpgrade) stack1.getItem()).getEyeTexture(stack1));
				} else {
					skip.set(true);
				}
			});

			if (!skip.get()) {
				if (tex.get() != null || RatUpgradeUtils.forEachUpgradeBool(rat, upgrade -> upgrade instanceof ChangesTextureUpgrade eyeTex && eyeTex.makesEyesGlowByDefault(), false)) {
					this.getParentModel().setupAnim(state);
					collector.submitCustomGeometry(stack, tex.get(), (pose, consumer) ->
							this.getParentModel().renderCitadelToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, -1));
				}
			}
		} else {
			super.submit(stack, collector, light, state, netHeadYaw, headPitch);
		}
	}
}
