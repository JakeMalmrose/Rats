package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.RatsModelLayers;
import com.github.alexthe666.rats.client.model.entity.PlagueDoctorModel;
import com.github.alexthe666.rats.server.entity.misc.PlagueDoctor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;

public class PlagueDoctorRenderer extends MobRenderer<PlagueDoctor, VillagerRenderState, PlagueDoctorModel> {
	private static final Identifier DOCTOR = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/plague_doctor.png");

	public PlagueDoctorRenderer(EntityRendererProvider.Context context) {
		super(context, new PlagueDoctorModel(context.bakeLayer(RatsModelLayers.PLAGUE_DOCTOR)), 0.5F);
		// 26.1: CrossedArmsItemLayer reads the held item off the render state instead of an ItemInHandRenderer.
		this.addLayer(new CrossedArmsItemLayer<>(this));
	}

	@Override
	public VillagerRenderState createRenderState() {
		return new VillagerRenderState();
	}

	@Override
	public void extractRenderState(PlagueDoctor entity, VillagerRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		// Fills state.heldItem for the CrossedArmsItemLayer.
		HoldingEntityRenderState.extractHoldingEntityRenderState(entity, state, this.itemModelResolver);
	}

	@Override
	public Identifier getTextureLocation(VillagerRenderState state) {
		return DOCTOR;
	}

	@Override
	protected void scale(VillagerRenderState state, PoseStack stack) {
		float f = 0.9375F;
		if (state.isBaby) {
			f = (float) ((double) f * 0.5D);
		}
		stack.scale(f, f, f);
	}

	// 26.1: shadowRadius is no longer a mutable field on the renderer; it is queried per render state.
	@Override
	protected float getShadowRadius(VillagerRenderState state) {
		return state.isBaby ? 0.25F : 0.5F;
	}
}
