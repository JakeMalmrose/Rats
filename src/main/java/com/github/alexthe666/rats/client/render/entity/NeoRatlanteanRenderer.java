package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.NeoRatlanteanModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.client.render.entity.layer.GlowingOverlayLayer;
import com.github.alexthe666.rats.server.entity.monster.boss.NeoRatlantean;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class NeoRatlanteanRenderer extends MobRenderer<NeoRatlantean, LivingEntityRenderState, RatsEntityModelBridge<NeoRatlantean>> {

	private static final Identifier BLUE_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/neo_ratlantean/neo_ratlantean_blue.png");
	private static final Identifier BLACK_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/neo_ratlantean/neo_ratlantean_black.png");
	private static final Identifier BROWN_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/neo_ratlantean/neo_ratlantean_brown.png");
	private static final Identifier GREEN_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/neo_ratlantean/neo_ratlantean_green.png");
	private static final Identifier GLOW_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/neo_ratlantean/neo_ratlantean_glow.png");

	public NeoRatlanteanRenderer(EntityRendererProvider.Context context) {
		super(context, new RatsEntityModelBridge<>(new NeoRatlanteanModel<>()), 0.65F);
		this.addLayer(new GlowingOverlayLayer<>(this, GLOW_TEXTURE));
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		if (RatsClientKeys.getLiving(state) instanceof NeoRatlantean entity) {
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
