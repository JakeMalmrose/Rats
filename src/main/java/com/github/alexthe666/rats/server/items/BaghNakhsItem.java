package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.registry.RatsToolMaterialRegistry;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

// 26.1: SwordItem is gone — sword behavior comes from Properties.sword(material, damage, speed);
// the trailing .attributes(...) overrides the generated modifiers with our absolute values.
public class BaghNakhsItem extends Item {

	public BaghNakhsItem(Item.Properties properties) {
		super(properties.sword(RatsToolMaterialRegistry.BAGHNAKHS, 3.0F, -2.4F).attributes(BUILT_ATTRIBUTES));
	}

	// 1.21: +6 damage, +6 speed via ItemAttributeModifiers (replaces the legacy getAttributeModifiers override).
	private static final ItemAttributeModifiers BUILT_ATTRIBUTES = ItemAttributeModifiers.builder()
			.add(Attributes.ATTACK_DAMAGE,
					new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 6.0D, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.add(Attributes.ATTACK_SPEED,
					new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, 6.0D, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.build();
}
