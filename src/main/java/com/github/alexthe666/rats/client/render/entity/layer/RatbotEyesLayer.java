package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.monster.RatlanteanRatbot;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class RatbotEyesLayer extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<RatlanteanRatbot>> {
	private static final RenderType TEXTURE_EYES_0 = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ratlantean_ratbot/ratlantean_ratbot_eyes_0.png"));
	private static final RenderType TEXTURE_EYES_1 = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ratlantean_ratbot/ratlantean_ratbot_eyes_1.png"));
	private static final RenderType TEXTURE_EYES_2 = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ratlantean_ratbot/ratlantean_ratbot_eyes_2.png"));
	private static final RenderType TEXTURE_EYES_3 = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/ratlantean_ratbot/ratlantean_ratbot_eyes_3.png"));

	public RatbotEyesLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<RatlanteanRatbot>> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!(RatsClientKeys.getLiving(state) instanceof RatlanteanRatbot entity)) {
			return;
		}
		this.getParentModel().setupAnim(state);
		collector.submitCustomGeometry(stack, this.getTextureForTick(entity.tickCount * 3), (pose, consumer) ->
				this.getParentModel().renderCitadelToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, -1));
	}

	private RenderType getTextureForTick(int tickCount) {
		int tickCap = tickCount % 40;
		if (tickCap > 19) {
			tickCap = tickCap - 20;
			if (tickCap > 15) {
				return TEXTURE_EYES_0;
			} else if (tickCap > 10) {
				return TEXTURE_EYES_1;
			} else if (tickCap > 5) {
				return TEXTURE_EYES_2;
			} else {
				return TEXTURE_EYES_3;
			}
		} else {
			if (tickCap > 15) {
				return TEXTURE_EYES_3;
			} else if (tickCap > 10) {
				return TEXTURE_EYES_2;
			} else if (tickCap > 5) {
				return TEXTURE_EYES_1;
			} else {
				return TEXTURE_EYES_0;
			}
		}
	}
}
