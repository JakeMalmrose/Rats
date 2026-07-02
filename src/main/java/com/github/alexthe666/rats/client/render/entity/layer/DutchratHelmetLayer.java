package com.github.alexthe666.rats.client.render.entity.layer;

import com.github.alexthe666.rats.client.model.entity.FlyingDutchratModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.server.entity.monster.boss.Dutchrat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class DutchratHelmetLayer<T extends Dutchrat> extends RenderLayer<LivingEntityRenderState, RatsEntityModelBridge<T>> {
	private final HumanoidModel<HumanoidRenderState> backup;
	private final EquipmentLayerRenderer equipmentRenderer;
	// 26.1: armor models pose from a render state at draw time; a neutral humanoid state keeps the helmet
	// at the model origin so the PoseStack transform (dutchrat head) does the positioning.
	private final HumanoidRenderState armorState = new HumanoidRenderState();

	public DutchratHelmetLayer(RenderLayerParent<LivingEntityRenderState, RatsEntityModelBridge<T>> parent, HumanoidModel<HumanoidRenderState> armorModel, EntityRendererProvider.Context context) {
		super(parent);
		this.backup = armorModel;
		this.equipmentRenderer = context.getEquipmentRenderer();
	}

	@Override
	public void submit(PoseStack stack, SubmitNodeCollector collector, int light, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
		if (!(RatsClientKeys.getLiving(state) instanceof Dutchrat rat)) {
			return;
		}
		if (rat.getBellSummonTicks() <= 0) {
			stack.pushPose();
			FlyingDutchratModel<?> model = (FlyingDutchratModel<?>) this.getParentModel().citadel();
			model.body1.translateRotate(stack);
			model.neck.translateRotate(stack);
			model.head.translateRotate(stack);
			stack.translate(0, -0.77F, 0);
			ItemStack itemstack = rat.getItemBySlot(EquipmentSlot.HEAD);
			// 26.1: ArmorItem is gone; head armor is identified by the EQUIPPABLE component's slot + asset id, and
			// EquipmentLayerRenderer handles the model-replacement hook, per-stack textures (ClientHooks.getArmorTexture),
			// dyed layers, foil and armor trims in one call.
			Equippable equippable = itemstack.get(DataComponents.EQUIPPABLE);
			if (equippable != null && equippable.assetId().isPresent() && equippable.slot() == EquipmentSlot.HEAD) {
				this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.HUMANOID, equippable.assetId().orElseThrow(),
						this.backup, this.armorState, itemstack, stack, collector, light, state.outlineColor);
			}
			stack.popPose();
		}
	}
}
