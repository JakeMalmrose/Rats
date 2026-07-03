package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.client.model.entity.AbstractRatModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.registry.RatlantisBlockRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.AbstractRat;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.items.HatItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class RatHelmetLayer<T extends AbstractRat> extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<T>> {
	private final HumanoidModel<HumanoidRenderState> ratArmorModel;
	private final EquipmentLayerRenderer equipmentRenderer;
	// 26.1: armor models pose from a render state at draw time; feed them a neutral humanoid state so the
	// helmet stays at the model origin and the PoseStack transform (rat head) does the positioning.
	private final HumanoidRenderState armorState = new HumanoidRenderState();

	public RatHelmetLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<T>> parent, HumanoidModel<HumanoidRenderState> armorModel, EntityRendererProvider.Context context) {
		super(parent);
		this.ratArmorModel = armorModel;
		this.equipmentRenderer = context.getEquipmentRenderer();
	}

	//for the sake of easier maintenance in the future, I will be commenting what each sections of this does.
	//this is partially a [VanillaCopy] of HumanoidArmorLayer.renderArmorPiece unless specified otherwise
	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!(RatsClientKeys.getLiving(state) instanceof AbstractRat rat)) {
			return;
		}
		ItemStack itemstack = rat.getItemBySlot(EquipmentSlot.HEAD);
		//Rats: always render a halo on top of dead rats
		if (rat instanceof TamedRat tamed && tamed.getRespawnCountdown() > 0) {
			itemstack = new ItemStack(RatsItemRegistry.HALO_HAT.get());
		}
		if (!itemstack.isEmpty()) {
			stack.pushPose();
			// 26.1: ArmorItem is gone; head armor is identified by the EQUIPPABLE component's slot + asset id.
			Equippable equippable = itemstack.get(DataComponents.EQUIPPABLE);
			if (equippable != null && equippable.assetId().isPresent()) {
				if (equippable.slot() == EquipmentSlot.HEAD) {
					// 26.1: render the armor model directly via submitCustomGeometry (same pattern as
					// Alex's Mobs 26.1 LayerKangarooArmor) — EquipmentLayerRenderer's deferred model
					// submit re-poses the model from a humanoid state at draw time, which does not
					// compose with the citadel-space transforms this layer builds on the stack.
					Model replaced = IClientItemExtensions.of(itemstack)
							.getHumanoidArmorModel(itemstack, EquipmentClientInfo.LayerType.HUMANOID, this.ratArmorModel);
					HumanoidModel<?> armorModel = replaced instanceof HumanoidModel<?> humanoid ? humanoid : this.ratArmorModel;
					//Rats: instead of using setPartVisibility, just toggle the helmet on. Its the only piece of armor we care about rendering anyway.
					armorModel.head.visible = true;
					armorModel.hat.visible = true;
					armorModel.body.visible = false;
					armorModel.rightArm.visible = false;
					armorModel.leftArm.visible = false;
					armorModel.rightLeg.visible = false;
					armorModel.leftLeg.visible = false;
					// Zero the head chain so the PoseStack transform below does all the positioning.
					// The hat geometry pivots were authored against the vanilla neck-level head pivot;
					// translateToHead ends at the rat's skull, so those offsets must be flattened too —
					// including models whose geometry isn't named "hat" (pirat's main_hat, toque's toupe).
					zeroPart(armorModel.head);
					zeroPart(armorModel.hat);
					for (String geometryChild : new String[]{"main_hat", "toupe"}) {
						if (armorModel.head.hasChild(geometryChild)) {
							zeroPart(armorModel.head.getChild(geometryChild));
						}
					}
					//Rats: do some extra transforms based on which model is being used and what item is rendering.
					this.ratModel().translateToHead(stack);
					if (rat.isBaby()) {
						stack.translate(0.0D, 0.025D, -0.05D);
						stack.scale(0.65F, 0.65F, 0.65F);
					}
					stack.translate(0, -0.325F, -0.045F);
					stack.scale(0.55F, 0.55F, 0.55F);
					if (itemstack.getItem() instanceof HatItem hat) {
						hat.transformOnHead(rat, stack);
					}
					// The NeoForge extension returns null for "no override" (all vanilla armor); fall
					// back to the equipment-asset texture ourselves or plain helmets render nothing.
					Identifier texture = IClientItemExtensions.of(itemstack)
							.getArmorTexture(itemstack, EquipmentClientInfo.LayerType.HUMANOID, null, fallbackArmorTexture(equippable));
					if (texture == null) {
						texture = fallbackArmorTexture(equippable);
					}
					int tint = -1;
					if (itemstack.has(DataComponents.DYED_COLOR)) {
						tint = 0xFF000000 | itemstack.get(DataComponents.DYED_COLOR).rgb();
					}
					submitArmorModel(collector, stack, armorModel, RenderTypes.armorCutoutNoCull(texture), light, tint);
					if (itemstack.hasFoil()) {
						submitArmorModel(collector, stack, armorModel, RenderTypes.armorEntityGlint(), light, -1);
					}
				}
			} else {
				//Rats: handle some special case hats in the mod
				if (!itemstack.is(RatsItemRegistry.PARTY_HAT.get())) {
					this.ratModel().translateToHead(stack);
					stack.translate(0, 0.025F, -0.15F);
					stack.mulPose(Axis.XP.rotationDegrees(180));
					stack.mulPose(Axis.YP.rotationDegrees(180));
					stack.scale(0.5F, 0.5F, 0.5F);
					if (itemstack.is(RatlantisBlockRegistry.MARBLED_CHEESE_RAT_HEAD.get().asItem())) {
						stack.translate(0, -0.1F, 0.1F);
						stack.mulPose(Axis.XP.rotationDegrees(15));
						if (rat.isBaby()) {
							stack.scale(0.4F, 0.4F, 0.4F);
							stack.translate(0.0D, 0.25D, 0.0D);
						}
					} else if (itemstack.is(net.minecraft.tags.ItemTags.SKULLS) && BuiltInRegistries.ITEM.getKey(itemstack.getItem()).getNamespace().equals("minecraft")) {
						stack.mulPose(Axis.YP.rotationDegrees(180));
						stack.translate(0.0D, 0.55D, -0.0D);
						stack.scale(2.0F, 2.0F, 2.0F);
						if (rat.isBaby()) {
							stack.scale(0.3F, 0.3F, 0.3F);
							stack.translate(0.0D, -0.75D, -0.25D);
						}
					}
					stack.translate(0.0D, -0.1D, 0.0D);
					stack.scale(0.85F, 0.85F, 0.85F);
					this.submitItem(itemstack, ItemDisplayContext.HEAD, stack, collector, light, state);
				}
			}
			stack.popPose();

			//Rats: render a banner if the rat has one in its banner slot
			stack.pushPose();
			ItemStack banner = rat.getItemBySlot(EquipmentSlot.OFFHAND);
			if (banner.getItem() instanceof BannerItem) {
				this.ratModel().translateToBody(stack);
				stack.translate(0.0D, -0.5D, -0.2D);
				stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
				float sitProgress = rat.sitProgress / 20.0F;
				stack.mulPose(Axis.XP.rotationDegrees(sitProgress * -40.0F));
				stack.translate(0.0D, 0.0D, -sitProgress * 0.04F);
				stack.scale(1.7F, 1.7F, 1.7F);
				this.submitItem(banner, ItemDisplayContext.FIXED, stack, collector, light, state);
			}
			stack.popPose();
		}
	}

	private static void zeroPart(net.minecraft.client.model.geom.ModelPart part) {
		part.setPos(0.0F, 0.0F, 0.0F);
		part.xRot = 0.0F;
		part.yRot = 0.0F;
		part.zRot = 0.0F;
	}

	/** 26.1 equipment-asset layer texture layout: textures/entity/equipment/humanoid/&lt;asset&gt;.png */
	private static Identifier fallbackArmorTexture(Equippable equippable) {
		Identifier asset = equippable.assetId().orElseThrow().identifier();
		return Identifier.fromNamespaceAndPath(asset.getNamespace(), "textures/entity/equipment/humanoid/" + asset.getPath() + ".png");
	}

	private static void submitArmorModel(SubmitNodeCollector collector, PoseStack stack, HumanoidModel<?> armorModel, RenderType renderType, int light, int tint) {
		collector.submitCustomGeometry(stack, renderType, (pose, consumer) -> {
			PoseStack drawStack = new PoseStack();
			drawStack.pushPose();
			drawStack.last().set(pose);
			armorModel.renderToBuffer(drawStack, consumer, light, OverlayTexture.NO_OVERLAY, tint);
			drawStack.popPose();
		});
	}

	private AbstractRatModel<T> ratModel() {
		return (AbstractRatModel<T>) this.getParentModel().citadel();
	}

	// 26.1: ItemRenderer.renderStatic is gone; items render through ItemStackRenderState.
	private void submitItem(ItemStack itemStack, ItemDisplayContext context, PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state) {
		ItemStackRenderState renderState = new ItemStackRenderState();
		Minecraft mc = Minecraft.getInstance();
		mc.getItemModelResolver().updateForTopItem(renderState, itemStack, context, mc.level instanceof ClientLevel cl ? cl : null, null, 0);
		renderState.submit(stack, collector, light, LivingEntityRenderer.getOverlayCoords(state, 0.0F), state.outlineColor);
	}
}
