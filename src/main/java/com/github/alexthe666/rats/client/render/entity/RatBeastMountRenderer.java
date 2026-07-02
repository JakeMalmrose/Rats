package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatBeastMountModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.client.render.entity.layer.GlowingOverlayLayer;
import com.github.alexthe666.rats.server.entity.mount.RatBeastMount;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RatBeastMountRenderer extends MobRenderer<RatBeastMount, LivingEntityRenderState, RatsEntityModelBridge<RatBeastMount>> {

	private static final Identifier BLUE_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/beasts/feral_ratlantean_blue.png");
	private static final Identifier BLACK_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/beasts/feral_ratlantean_black.png");
	private static final Identifier BROWN_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/beasts/feral_ratlantean_brown.png");
	private static final Identifier GREEN_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/beasts/feral_ratlantean_green.png");
	private static final Identifier EYE_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/beasts/plague_beast_eyes.png");

	public RatBeastMountRenderer(EntityRendererProvider.Context context) {
		super(context, new RatsEntityModelBridge<>(new RatBeastMountModel<>()), 0.5F);
		this.addLayer(new GlowingOverlayLayer<>(this, EYE_TEXTURE));
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	protected void scale(LivingEntityRenderState state, PoseStack stack) {
		stack.scale(1.2F, 1.2F, 1.2F);
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		if (RatsClientKeys.getLiving(state) instanceof RatBeastMount entity) {
			return switch (entity.getColorVariant()) {
				case 1 -> BLACK_TEXTURE;
				case 2 -> BROWN_TEXTURE;
				case 3 -> GREEN_TEXTURE;
				default -> BLUE_TEXTURE;
			};
		}
		return BLUE_TEXTURE;
	}
}
