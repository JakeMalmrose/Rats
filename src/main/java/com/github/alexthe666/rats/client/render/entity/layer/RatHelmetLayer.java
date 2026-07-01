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
import net.minecraft.client.multiplayer.ClientLevel;
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
					//Rats: instead of using setPartVisibility, just toggle the helmet on. Its the only piece of armor we care about rendering anyway.
					this.ratArmorModel.setAllVisible(false);
					this.ratArmorModel.head.visible = true;
					this.ratArmorModel.hat.visible = true;
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
					// 26.1: EquipmentLayerRenderer now handles the model-replacement hook (IClientItemExtensions),
					// per-stack textures (ClientHooks.getArmorTexture), dyed layers, foil and armor trims in one call.
					this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.HUMANOID, equippable.assetId().orElseThrow(),
							this.ratArmorModel, this.armorState, itemstack, stack, collector, light, state.outlineColor);
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
