package com.github.alexthe666.rats.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jspecify.annotations.Nullable;

public class PiratSignRenderer extends StandingSignRenderer {

	private final SpriteGetter spriteGetter;

	public PiratSignRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
		this.spriteGetter = context.sprites();
	}

	@Override
	protected void submitSign(PoseStack poseStack, int lightCoords, WoodType type, Model.Simple signModel, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, SubmitNodeCollector collector) {
		// 1.20 drew pirat signs with the entityTranslucent render type instead of the default sign cutout.
		SpriteId sprite = this.getSignSprite(type);
		collector.submitModel(signModel, Unit.INSTANCE, poseStack, sprite.renderType(RenderTypes::entityTranslucent), lightCoords, OverlayTexture.NO_OVERLAY, -1, this.spriteGetter.get(sprite), 0, breakProgress);
	}
}
