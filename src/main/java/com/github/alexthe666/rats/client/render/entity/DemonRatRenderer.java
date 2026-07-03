package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.RatModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.rat.DemonRat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class DemonRatRenderer extends AbstractRatRenderer<DemonRat> {

	public static final Identifier BASE_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/demon_rat/demon_rat.png");
	public static final Identifier SOUL_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/demon_rat/soul_demon_rat.png");
	public static final Identifier BASE_EYE_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/demon_rat/demon_rat_eye.png");
	public static final Identifier SOUL_EYE_TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/demon_rat/soul_demon_rat_eye.png");

	public DemonRatRenderer(EntityRendererProvider.Context context) {
		super(context, new RatModel<>());
		this.addLayer(new DemonEyesLayer(this));
	}

	@Override
	protected void scale(LivingEntityRenderState state, PoseStack stack) {
		stack.scale(1.5F, 1.5F, 1.5F);
		super.scale(state, stack);
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return RatsClientKeys.getLiving(state) instanceof DemonRat rat && rat.isSoulVariant() ? SOUL_TEXTURE : BASE_TEXTURE;
	}

	public static class DemonEyesLayer extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<DemonRat>> {
		public DemonEyesLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<DemonRat>> parent) {
			super(parent);
		}

		@Override
		public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
			boolean soul = RatsClientKeys.getLiving(state) instanceof DemonRat rat && rat.isSoulVariant();
			this.getParentModel().setupAnim(state);
			// capture the bridge at submit time — the renderer swaps this.model per entity (adult vs
			// pinkie), so getParentModel() at draw time can be another rat's model (ghost-baby overlay)
			final var submitModel = this.getParentModel();
			collector.submitCustomGeometry(stack, RenderTypes.eyes(soul ? SOUL_EYE_TEXTURE : BASE_EYE_TEXTURE), (pose, consumer) -> {
					// re-pose the shared model at draw time: other entities re-run setupAnim between submit and draw
					submitModel.setupAnim(state);
					submitModel.renderCitadelToBuffer(pose, consumer, 15728640, OverlayTexture.NO_OVERLAY, -1);
				});
		}
	}
}
