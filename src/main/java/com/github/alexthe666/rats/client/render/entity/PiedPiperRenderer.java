package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.RatsModelLayers;
import com.github.alexthe666.rats.client.model.entity.PiedPiperModel;
import com.github.alexthe666.rats.server.entity.monster.PiedPiper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.resources.Identifier;

public class PiedPiperRenderer extends MobRenderer<PiedPiper, ArmedEntityRenderState, PiedPiperModel<ArmedEntityRenderState>> {
	private static final Identifier PIPER = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/pied_piper.png");

	public PiedPiperRenderer(EntityRendererProvider.Context context) {
		super(context, new PiedPiperModel<>(context.bakeLayer(RatsModelLayers.PIPER)), 0.5F);
		// 26.1: ItemInHandLayer reads the hand items off the render state instead of an ItemInHandRenderer.
		this.addLayer(new ItemInHandLayer<>(this));
	}

	@Override
	public ArmedEntityRenderState createRenderState() {
		return new ArmedEntityRenderState();
	}

	@Override
	public void extractRenderState(PiedPiper entity, ArmedEntityRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		// Fills arm poses + hand item render states for the ItemInHandLayer.
		ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTicks);
	}

	@Override
	public Identifier getTextureLocation(ArmedEntityRenderState state) {
		return PIPER;
	}
}
