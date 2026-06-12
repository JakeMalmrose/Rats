package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.client.model.RatsModelLayers;
import com.github.alexthe666.rats.client.model.hats.RatlantisArmorModel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

// 26.1: ArmorItem is gone — armor is a plain Item with Properties.humanoidArmor(material, type).
public class RatlantisArmorItem extends Item {

	private final ArmorType type;

	public RatlantisArmorItem(ArmorMaterial material, ArmorType type, Item.Properties properties) {
		super(properties.humanoidArmor(material, type));
		this.type = type;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		tooltip.accept(Component.translatable("item.rats.ratlantis_armor.desc0").withStyle(ChatFormatting.YELLOW));
		tooltip.accept(Component.translatable("item.rats.ratlantis_armor.desc1").withStyle(ChatFormatting.GRAY));
		tooltip.accept(Component.translatable("item.rats.ratlantis_armor.desc2").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		// A separate @OnlyIn class (not an anonymous one) keeps client-only types out of this
		// class's constant pool so it stays loadable on a dedicated server.
		consumer.accept(new ClientExtensions(this));
	}

	@OnlyIn(Dist.CLIENT)
	private static final class ClientExtensions implements IClientItemExtensions {
		private final RatlantisArmorItem armor;

		ClientExtensions(RatlantisArmorItem armor) {
			this.armor = armor;
		}

		// 26.1: the per-stack armor texture hook moved from IItemExtension to IClientItemExtensions.
		// Returns rats:textures/model/armor/ratlantis_armor_{0,1}.png (singular `model` — matches the
		// existing asset layout from 1.20.1).
		@Override
		public Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, Identifier _default) {
			return Identifier.fromNamespaceAndPath(
				"rats", "textures/model/armor/" + (type == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS ? "ratlantis_armor_1" : "ratlantis_armor_0") + ".png");
		}

		@Override
		public Model getHumanoidArmorModel(ItemStack stack, EquipmentClientInfo.LayerType layerType, Model original) {
			EntityModelSet models = Minecraft.getInstance().getEntityModels();
			ModelPart root = models.bakeLayer(this.armor.type == ArmorType.LEGGINGS ? RatsModelLayers.RATLANTIS_ARMOR_INNER : RatsModelLayers.RATLANTIS_ARMOR_OUTER);
			return new RatlantisArmorModel(root);
		}
	}
}
