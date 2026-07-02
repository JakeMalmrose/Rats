package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.RatsModelLayers;
import com.github.alexthe666.rats.client.model.entity.BlackDeathModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.server.entity.monster.boss.BlackDeath;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

// 26.1: extends MobRenderer directly rather than HumanoidMobRenderer so the held item stays behind the
// summoning/melee condition (HumanoidMobRenderer adds an unconditional ItemInHandLayer).
public class BlackDeathRenderer extends MobRenderer<BlackDeath, HumanoidRenderState, BlackDeathModel> {

	private static final Identifier BLACK_DEATH_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/black_death/black_death.png");
	private static final Identifier GLOW_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/black_death/black_death_overlay.png");

	public BlackDeathRenderer(EntityRendererProvider.Context context) {
		super(context, new BlackDeathModel(context.bakeLayer(RatsModelLayers.BLACK_DEATH)), 0.5F);
		this.addLayer(new GlowLayer(this));
		this.addLayer(new ItemInHandLayer<>(this) {
			@Override
			public void submit(PoseStack stack, SubmitNodeCollector collector, int light, HumanoidRenderState state, float netHeadYaw, float headPitch) {
				if (RatsClientKeys.getLiving(state) instanceof BlackDeath death && (death.isSummoning() || death.isMeleeAttacking())) {
					super.submit(stack, collector, light, state, netHeadYaw, headPitch);
				}
			}
		});
	}

	@Override
	public HumanoidRenderState createRenderState() {
		return new HumanoidRenderState();
	}

	@Override
	public void extractRenderState(BlackDeath entity, HumanoidRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		// Fills arm poses + hand item render states for the ItemInHandLayer.
		HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTicks, this.itemModelResolver);
	}

	@Override
	public Identifier getTextureLocation(HumanoidRenderState state) {
		return BLACK_DEATH_TEXTURE;
	}

	// 26.1: the shared GlowingOverlayLayer is typed to the Citadel model bridge; Black Death uses a vanilla
	// HumanoidModel now, so its glow overlay is a local layer over the vanilla model instead.
	private static class GlowLayer extends RenderLayer<HumanoidRenderState, BlackDeathModel> {
		private static final RenderType GLOW = RenderTypes.eyes(GLOW_TEXTURE);

		public GlowLayer(RenderLayerParent<HumanoidRenderState, BlackDeathModel> parent) {
			super(parent);
		}

		@Override
		public void submit(PoseStack stack, SubmitNodeCollector collector, int light, HumanoidRenderState state, float netHeadYaw, float headPitch) {
			collector.submitModel(this.getParentModel(), state, stack, GLOW, light, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
		}
	}
}
