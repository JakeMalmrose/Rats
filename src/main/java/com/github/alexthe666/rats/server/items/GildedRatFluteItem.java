package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import com.github.alexthe666.rats.registry.RatsSoundRegistry;
import com.github.alexthe666.rats.server.entity.projectile.RatShot;
import com.github.alexthe666.rats.server.misc.RatVariant;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GildedRatFluteItem extends LoreTagItem {

	// 26.1: Item.isValidRepairItem is gone; repairability is data-driven via the REPAIRABLE component.
	// The tag must contain rats:tangled_rat_tails (data/rats/tags/item/repairs/gilded_rat_flute.json).
	private static final TagKey<Item> REPAIR_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RatsMod.MODID, "repairs/gilded_rat_flute"));

	public GildedRatFluteItem(Item.Properties properties) {
		super(properties.repairable(REPAIR_ITEMS), 1);
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.TOOT_HORN;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)) {
			RatShot ratShot = new RatShot(RatsEntityRegistry.RAT_SHOT.get(), level, player);
			ratShot.setColorVariant(RatVariant.getRandomVariant(player.getRandom(), false));
			Vec3 vector3d = player.getViewVector(1.0F);
			ratShot.shoot(vector3d.x(), vector3d.y(), vector3d.z(), 1.0F, 1.5F);
			level.addFreshEntity(ratShot);
			stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
			player.swing(hand);
			player.getCooldowns().addCooldown(stack, 10);
			level.playSound(player, player.blockPosition(), RatsSoundRegistry.getFluteSound(), SoundSource.PLAYERS, 0.5F, 0.75F);
		}
		return InteractionResult.SUCCESS;
	}
}
