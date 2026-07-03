package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.ChangesOverlayUpgrade;
import com.github.alexthe666.rats.server.misc.RatColorUtil;
import com.github.alexthe666.rats.server.misc.RatUpgradeUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;

public class TamedRatOverlayLayer extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<TamedRat>> {
	// 26.1: entitySmoothCutout was removed; entityCutout (now no-cull) is the closest cutout variant.
	private static final RenderType TEXTURE_DYED_NOT = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/undyed_part.png"));
	// 26.1: entityNoOutline was removed; entityTranslucent with affectsOutline=false is the equivalent no-outline pass.
	private static final RenderType TEXTURE_DYED = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/dyed_part.png"), false);
	private static final RenderType TOGA_TEX = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/rat/upgrades/toga.png"));

	// 26.1: RenderLayer no longer exposes getTextureLocation; keep the parent renderer around to look up
	// the rat's texture for the god upgrade glint.
	private final LivingEntityRenderer<TamedRat, LivingEntityRenderState, RatsEntityModelBridge<TamedRat>> renderer;

	public TamedRatOverlayLayer(LivingEntityRenderer<TamedRat, LivingEntityRenderState, RatsEntityModelBridge<TamedRat>> parent) {
		super(parent);
		this.renderer = parent;
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!(RatsClientKeys.getLiving(state) instanceof TamedRat rat)) {
			return;
		}
		this.getParentModel().setupAnim(state);
		if (rat.getRespawnCountdown() > 0 && RatConfig.ratAngelGlint) {
			this.submitOverlay(stack, collector, state, RatsRenderType.getWhiteGlint(), light, OverlayTexture.NO_OVERLAY, -1);
		} else {
			if (rat.hasCustomName() && rat.getCustomName().getString().equalsIgnoreCase("ultrakill")) {
				this.submitOverlay(stack, collector, state, RatsRenderType.getGreenGlint(), light, OverlayTexture.NO_OVERLAY, -1);
			} else {
				if (RatUpgradeUtils.hasUpgrade(rat, RatsItemRegistry.RAT_UPGRADE_GOD.get()) && RatConfig.ratGodGlint) {
					// 26.1: ItemRenderer.getFoilBuffer is gone; foil is now an extra entityGlint pass over the base draw.
					this.submitOverlay(stack, collector, state, RenderTypes.entityCutout(this.renderer.getTextureLocation(state)), light, OverlayTexture.NO_OVERLAY, -1);
					this.submitOverlay(stack, collector, state, RenderTypes.entityGlint(), light, OverlayTexture.NO_OVERLAY, -1);
				}

				if (rat.isDyed()) {
					int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
					if (rat.getDyeColor() == 100) {
						RenderType type = RatsRenderType.GlintType.getRenderTypeBasedOnKeyword(rat.getSpecialDye());
						this.submitOverlay(stack, collector, state, type != null ? type : RatsRenderType.getRainbowGlint(), light, overlay, -1);
						if (!RatUpgradeUtils.hasUpgrade(rat, RatsItemRegistry.RAT_UPGRADE_UNDEAD.get())) {
							this.submitOverlay(stack, collector, state, TEXTURE_DYED_NOT, light, overlay, -1);
						}
					} else {
						float[] color = RatColorUtil.getDyeRgb(DyeColor.byId(rat.getDyeColor()));
						this.submitOverlay(stack, collector, state, TEXTURE_DYED, light, overlay, ARGB.colorFromFloat(1.0F, color[0], color[1], color[2]));
					}
				}

				RatUpgradeUtils.forEachUpgrade(rat, item -> item instanceof ChangesOverlayUpgrade, (upgrade, slot) -> {
					if (rat.isSlotVisible(slot)) {
						RenderType overlayType = ((ChangesOverlayUpgrade) upgrade.getItem()).getOverlayTexture(upgrade, rat, state.partialTick);
						if (overlayType != null) {
							this.getParentModel().setupAnim(state);
							this.submitOverlay(stack, collector, state, overlayType, light, OverlayTexture.NO_OVERLAY, -1);
						}
					}
				});

				if (rat.hasToga()) {
					this.submitOverlay(stack, collector, state, TOGA_TEX, light, OverlayTexture.NO_OVERLAY, -1);
				}
			}
		}
	}

	private void submitOverlay(PoseStack stack, SubmitNodeCollector collector, LivingEntityRenderState state, RenderType type, int light, int overlay, int color) {
		collector.submitCustomGeometry(stack, type, (pose, consumer) -> {
			// re-pose the shared model at draw time: other entities re-run setupAnim between submit and draw
			this.getParentModel().setupAnim(state);
			this.getParentModel().renderCitadelToBuffer(pose, consumer, light, overlay, color);
		});
	}
}
