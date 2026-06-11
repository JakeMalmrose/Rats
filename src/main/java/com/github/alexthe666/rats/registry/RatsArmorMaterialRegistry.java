package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;
import java.util.Map;

// 26.1: ArmorMaterial is a plain record consumed via Item.Properties.humanoidArmor(material, type) —
// no registry involved. Repair ingredients are item tags (data/rats/tags/item/repairs/<name>.json) and
// visuals come from equipment assets (assets/rats/equipment/<name>.json); hats keep their custom
// models client-side, so their equipment assets are empty.
public final class RatsArmorMaterialRegistry {

	public static final ArmorMaterial PIPER_HAT = material("piper_hat", 25, defense(1, 1, 1, 2), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);
	public static final ArmorMaterial CHEF_TOQUE = material("chef_toque", 0, defense(1, 1, 1, 1), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);
	public static final ArmorMaterial PLAGUE_MASK = material("plague_mask", 25, defense(1, 1, 1, 3), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 0.0F);
	public static final ArmorMaterial RATLANTIS = material("ratlantis", 40, defense(5, 8, 10, 5), 17, SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.1F);
	public static final ArmorMaterial FARMER_HAT = material("farmer_hat", 0, defense(1, 1, 1, 1), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);
	public static final ArmorMaterial TOP_HAT = material("top_hat", 0, defense(1, 1, 1, 1), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);
	public static final ArmorMaterial FEZ = material("fez", 0, defense(1, 1, 1, 1), 100, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);
	public static final ArmorMaterial SANTA_HAT = material("santa_hat", 0, defense(1, 1, 1, 1), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);
	public static final ArmorMaterial HALO = material("halo", 0, defense(1, 1, 1, 1), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);
	public static final ArmorMaterial CROWN = material("crown", 0, defense(1, 1, 1, 1), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);
	public static final ArmorMaterial GHOST_HAT = material("ghost_hat", 0, defense(1, 1, 1, 1), 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);
	public static final ArmorMaterial GENERIC_HAT = material("generic_hat", 0, defense(1, 1, 1, 1), 100, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F);

	private RatsArmorMaterialRegistry() {
	}

	private static EnumMap<ArmorType, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
		EnumMap<ArmorType, Integer> m = new EnumMap<>(ArmorType.class);
		m.put(ArmorType.BOOTS, boots);
		m.put(ArmorType.LEGGINGS, leggings);
		m.put(ArmorType.CHESTPLATE, chestplate);
		m.put(ArmorType.HELMET, helmet);
		return m;
	}

	private static ArmorMaterial material(String name, int durabilityMultiplier, Map<ArmorType, Integer> defense, int enchantability, Holder<SoundEvent> equipSound, float toughness, float knockback) {
		TagKey<Item> repairTag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RatsMod.MODID, "repairs/" + name));
		ResourceKey<EquipmentAsset> asset = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(RatsMod.MODID, name));
		return new ArmorMaterial(durabilityMultiplier, defense, enchantability, equipSound, toughness, knockback, repairTag, asset);
	}
}
