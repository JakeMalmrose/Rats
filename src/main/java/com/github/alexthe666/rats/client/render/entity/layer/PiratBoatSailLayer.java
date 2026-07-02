package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.entity.PiratBoatModel;
import com.github.alexthe666.rats.client.model.entity.PiratCannonModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.server.entity.misc.PiratBoat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;

public class PiratBoatSailLayer extends RenderLayer<LivingEntityRenderState, PiratBoatModel> {
	public static final PiratCannonModel<PiratBoat> MODEL_PIRAT_CANNON = new PiratCannonModel<>();
	public static final Identifier TEXTURE_PIRATE_CANNON = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/pirat/pirat_cannon.png");
	public static final Identifier TEXTURE_PIRATE_CANNON_FIRE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/pirat/pirat_cannon_fire.png");

	public PiratBoatSailLayer(RenderLayerParent<LivingEntityRenderState, PiratBoatModel> parent) {
		super(parent);
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!(RatsClientKeys.getLiving(state) instanceof PiratBoat boat)) {
			return;
		}
		int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);

		// 26.1: ItemRenderer.renderStatic is gone; items render through ItemStackRenderState.
		stack.pushPose();
		stack.mulPose(Axis.XP.rotationDegrees(180));
		stack.mulPose(Axis.YP.rotationDegrees(90));
		stack.translate(0F, -0.8F, -0.9F);
		stack.scale(4F, 4F, 4F);
		ItemStackRenderState bannerState = new ItemStackRenderState();
		Minecraft mc = Minecraft.getInstance();
		mc.getItemModelResolver().updateForTopItem(bannerState, boat.banner, ItemDisplayContext.GROUND, mc.level instanceof ClientLevel cl ? cl : null, null, 0);
		bannerState.submit(stack, collector, light, overlay, 0);
		stack.popPose();

		stack.pushPose();
		stack.pushPose();
		stack.mulPose(Axis.YN.rotationDegrees(90));
		stack.translate(0, 0.1F, -0.6F);
		stack.scale(0.75F, 0.75F, 0.75F);
		collector.submitCustomGeometry(stack, RenderTypes.entityCutout(TEXTURE_PIRATE_CANNON), (pose, consumer) -> {
				PoseStack drawStack = new PoseStack();
				drawStack.pushPose();
				drawStack.last().set(pose);
				MODEL_PIRAT_CANNON.renderToBuffer(drawStack, consumer, light, overlay, -1);
				drawStack.popPose();
			});
		stack.popPose();

		if (boat.isFiring()) {
			stack.pushPose();
			stack.mulPose(Axis.YN.rotationDegrees(90));
			stack.translate(0, 0.1F, -0.6F);
			stack.scale(0.75F, 0.75F, 0.75F);
			collector.submitCustomGeometry(stack, RenderTypes.entityCutout(TEXTURE_PIRATE_CANNON_FIRE), (pose, consumer) -> {
					PoseStack drawStack = new PoseStack();
					drawStack.pushPose();
					drawStack.last().set(pose);
					MODEL_PIRAT_CANNON.renderToBuffer(drawStack, consumer, 240, OverlayTexture.NO_OVERLAY, -1);
					drawStack.popPose();
				});
			stack.popPose();
		}
		stack.popPose();
	}
}
