package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.client.model.RatsModelLayers;
import com.github.alexthe666.rats.client.model.entity.RatStriderMountModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.server.entity.mount.RatStriderMount;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RatStriderMountRenderer extends MobRenderer<RatStriderMount, LivingEntityRenderState, RatStriderMountModel> {

	private static final Identifier STRIDER_LOCATION = Identifier.parse("textures/entity/strider/strider.png");
	private static final Identifier COLD_LOCATION = Identifier.parse("textures/entity/strider/strider_cold.png");

	public RatStriderMountRenderer(EntityRendererProvider.Context context) {
		super(context, new RatStriderMountModel(context.bakeLayer(RatsModelLayers.RAT_STRIDER_MOUNT)), 0.5F);
		this.addLayer(new AlwaysSaddledLayer<>(this, new RatStriderMountModel(context.bakeLayer(RatsModelLayers.RAT_STRIDER_MOUNT)), Identifier.parse("textures/entity/strider/strider_saddle.png")));
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return RatsClientKeys.getLiving(state) instanceof RatStriderMount mount && mount.isSuffocating() ? COLD_LOCATION : STRIDER_LOCATION;
	}

	@Override
	protected boolean isShaking(LivingEntityRenderState state) {
		return super.isShaking(state) || (RatsClientKeys.getLiving(state) instanceof RatStriderMount mount && mount.isSuffocating());
	}

	public static class AlwaysSaddledLayer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
		private final Identifier textureLocation;
		private final M model;

		public AlwaysSaddledLayer(RenderLayerParent<S, M> parent, M model, Identifier texture) {
			super(parent);
			this.model = model;
			this.textureLocation = texture;
		}

		@Override
		public void submit(PoseStack stack, SubmitNodeCollector collector, int light, S state, float netHeadYaw, float headPitch) {
			this.model.setupAnim(state);
			int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
			collector.submitModel(this.model, state, stack, this.textureLocation, light, overlay, state.outlineColor, null);
		}
	}
}
