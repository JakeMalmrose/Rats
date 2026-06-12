package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.registry.RatsToolMaterialRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.function.Consumer;

// 26.1: SwordItem is gone — sword behavior comes from Properties.sword(material, damage, speed);
// the trailing .attributes(...) overrides the generated modifiers with our absolute values.
public class PlagueScytheItem extends Item {
	public PlagueScytheItem(Item.Properties properties) {
		super(properties.sword(RatsToolMaterialRegistry.PLAGUE_SCYTHE, 3.0F, -2.4F).attributes(BUILT_ATTRIBUTES));
	}

	// 1.21: +12 damage, -0.5 speed via ItemAttributeModifiers (replaces legacy getAttributeModifiers override).
	private static final ItemAttributeModifiers BUILT_ATTRIBUTES = ItemAttributeModifiers.builder()
			.add(Attributes.ATTACK_DAMAGE,
					new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 12.0D, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.add(Attributes.ATTACK_SPEED,
					new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -0.5D, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.build();

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return 1.0F;
	}

	// 1.21: signature changed to (ItemStack, BlockState). Plague scythe is a weapon, never a mining tool.
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return false;
	}

	// 26.1: IItemExtension#canPerformAction takes ItemInstance instead of ItemStack.
	@Override
	public boolean canPerformAction(net.minecraft.world.item.ItemInstance stack, ItemAbility toolAction) {
		return toolAction == ItemAbilities.SWORD_SWEEP;
	}
}