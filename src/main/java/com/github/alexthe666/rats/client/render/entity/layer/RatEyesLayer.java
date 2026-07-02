package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.rat.AbstractRat;
import com.github.alexthe666.rats.server.entity.rat.Rat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LightLayer;

public class RatEyesLayer<T extends AbstractRat> extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<T>> {
	protected static final RenderType EYES = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/eyes/glow.png"));
	protected static final RenderType PLAGUE_EYES = RenderTypes.eyes(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/eyes/plague.png"));

	public RatEyesLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<T>> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!(RatsClientKeys.getLiving(state) instanceof AbstractRat rat)) {
			return;
		}
		long roundedTime = rat.level().getDayTime() % 24000;
		boolean night = roundedTime >= 13000 && roundedTime <= 23000;
		BlockPos ratPos = rat.getLightPosition();
		int i = rat.level().getBrightness(LightLayer.SKY, ratPos);
		int j = rat.level().getBrightness(LightLayer.BLOCK, ratPos);
		int brightness;
		if (night) {
			brightness = j;
		} else {
			brightness = Math.max(i, j);
		}
		if (rat instanceof Rat plagueable && plagueable.hasPlague()) {
			this.getParentModel().setupAnim(state);
			collector.submitCustomGeometry(stack, PLAGUE_EYES, (pose, consumer) ->
					this.getParentModel().renderCitadelToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, -1));
		} else if (brightness < 7) {
			this.getParentModel().setupAnim(state);
			collector.submitCustomGeometry(stack, EYES, (pose, consumer) ->
					this.getParentModel().renderCitadelToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, -1));
		}
	}
}
