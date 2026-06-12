package com.github.alexthe666.rats.registry;

import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.core.registries.BuiltInRegistries;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.data.tags.RatsBannerPatternTags;
import com.github.alexthe666.rats.server.entity.misc.PiratWoodBoat;
import com.github.alexthe666.rats.server.items.*;
import com.github.alexthe666.rats.server.items.upgrades.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RatlantisItemRegistry {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, RatsMod.MODID);

	public static final DeferredHolder<Item, Item> RAS_BANNER_PATTERN = ITEMS.register("rat_and_sickle_banner_pattern", key -> new BannerPatternItem(RatsBannerPatternTags.RAS_BANNER_PATTERN, RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RATLANTIS_RAT_SKULL = ITEMS.register("ratlantis_rat_skull", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(RatsMod.RATLANTIS_SPECIAL).fireResistant()));
	public static final DeferredHolder<Item, Item> AVIATOR_HAT = ITEMS.register("aviator_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), RatsArmorMaterialRegistry.GENERIC_HAT, 0));
	public static final DeferredHolder<Item, Item> RAT_TOGA = ITEMS.register("rat_toga", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2));
	public static final DeferredHolder<Item, Item> RATGLOVE_PETALS = ITEMS.register("ratglove_petals", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> FERAL_RAT_CLAW = ITEMS.register("feral_rat_claw", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> FERAL_BAGH_NAKHS = ITEMS.register("feral_bagh_nakhs", key -> new BaghNakhsItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> GEM_OF_RATLANTIS = ITEMS.register("gem_of_ratlantis", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(RatsMod.RATLANTIS_SPECIAL)));
	public static final DeferredHolder<Item, Item> ORATCHALCUM_INGOT = ITEMS.register("oratchalcum_ingot", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant()));
	public static final DeferredHolder<Item, Item> RAW_ORATCHALCUM = ITEMS.register("raw_oratchalcum", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant()));
	public static final DeferredHolder<Item, Item> ORATCHALCUM_NUGGET = ITEMS.register("oratchalcum_nugget", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant()));
	public static final DeferredHolder<Item, Item> RATLANTIS_HELMET = ITEMS.register("ratlantis_helmet", key -> new RatlantisArmorItem(RatsArmorMaterialRegistry.RATLANTIS, ArmorType.HELMET, RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)));
	public static final DeferredHolder<Item, Item> RATLANTIS_CHESTPLATE = ITEMS.register("ratlantis_chestplate", key -> new RatlantisArmorItem(RatsArmorMaterialRegistry.RATLANTIS, ArmorType.CHESTPLATE, RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)));
	public static final DeferredHolder<Item, Item> RATLANTIS_LEGGINGS = ITEMS.register("ratlantis_leggings", key -> new RatlantisArmorItem(RatsArmorMaterialRegistry.RATLANTIS, ArmorType.LEGGINGS, RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)));
	public static final DeferredHolder<Item, Item> RATLANTIS_BOOTS = ITEMS.register("ratlantis_boots", key -> new RatlantisArmorItem(RatsArmorMaterialRegistry.RATLANTIS, ArmorType.BOOTS, RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)));
	public static final DeferredHolder<Item, Item> RATLANTIS_SWORD = ITEMS.register("ratlantis_sword", key -> new RatlantisSwordItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.UNCOMMON).fireResistant()));
	public static final DeferredHolder<Item, Item> RATLANTIS_PICKAXE = ITEMS.register("ratlantis_pickaxe", key -> new RatlantisToolItem.Pickaxe(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.UNCOMMON).fireResistant()));
	public static final DeferredHolder<Item, Item> RATLANTIS_AXE = ITEMS.register("ratlantis_axe", key -> new RatlantisToolItem.Axe(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.UNCOMMON).fireResistant()));
	public static final DeferredHolder<Item, Item> RATLANTIS_SHOVEL = ITEMS.register("ratlantis_shovel", key -> new RatlantisToolItem.Shovel(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.UNCOMMON).fireResistant()));
	public static final DeferredHolder<Item, Item> RATLANTIS_HOE = ITEMS.register("ratlantis_hoe", key -> new RatlantisToolItem.Hoe(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.UNCOMMON).fireResistant()));
	public static final DeferredHolder<Item, Item> RATLANTIS_BOW = ITEMS.register("ratlantis_bow", key -> new RatlantisBowItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).durability(1500).rarity(Rarity.UNCOMMON).fireResistant()));
	public static final DeferredHolder<Item, Item> ARCANE_TECHNOLOGY = ITEMS.register("arcane_technology", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.RARE).fireResistant(), 2));
	public static final DeferredHolder<Item, Item> ANCIENT_SAWBLADE = ITEMS.register("ancient_sawblade", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.RARE).fireResistant()));
	public static final DeferredHolder<Item, Item> RATLANTEAN_FLAME = ITEMS.register("ratlantean_flame", key -> new RatlanteanFlameItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> VIAL_OF_SENTIENCE = ITEMS.register("vial_of_sentience", key -> new VialOfSentienceItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).rarity(Rarity.RARE)));
	public static final DeferredHolder<Item, Item> PSIONIC_RAT_BRAIN = ITEMS.register("psionic_rat_brain", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.RARE).fireResistant(), 2));
	public static final DeferredHolder<Item, Item> PIRAT_CUTLASS = ITEMS.register("pirat_cutlass", key -> new PiratCutlassItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), false));
	public static final DeferredHolder<Item, Item> CHEESE_CANNONBALL = ITEMS.register("cheese_cannonball", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 1));
	public static final DeferredHolder<Item, Item> GHOST_PIRAT_HAT = ITEMS.register("ghost_pirat_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).fireResistant(), RatsArmorMaterialRegistry.GHOST_HAT, 0));
	public static final DeferredHolder<Item, Item> GHOST_PIRAT_ECTOPLASM = ITEMS.register("ghost_pirat_ectoplasm", key -> new EctoplasmItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant()));
	public static final DeferredHolder<Item, Item> GHOST_PIRAT_CUTLASS = ITEMS.register("ghost_pirat_cutlass", key -> new PiratCutlassItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant(), true));
	public static final DeferredHolder<Item, Item> DUTCHRAT_WHEEL = ITEMS.register("dutchrat_wheel", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.RARE).fireResistant()));
	public static final DeferredHolder<Item, Item> MILITARY_HAT = ITEMS.register("military_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.GENERIC_HAT, 0));
	public static final DeferredHolder<Item, Item> BIPLANE_WING = ITEMS.register("biplane_wing", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.RARE).fireResistant()));
	public static final DeferredHolder<Item, Item> RATFISH = ITEMS.register("ratfish", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.35F).build())));
	// 1.21: MobBucketItem ctor takes (EntityType<?>, Fluid, SoundEvent, Properties) — no Suppliers; unwrap via .get() / .value().
	public static final DeferredHolder<Item, Item> RATFISH_BUCKET = ITEMS.register("ratfish_bucket", key -> new MobBucketItem(RatlantisEntityRegistry.RATFISH.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, RatsRegistryHelper.withItemId(key, new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RATBOT_BARREL = ITEMS.register("ratbot_barrel", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> CHARGED_RATBOT_BARREL = ITEMS.register("charged_ratbot_barrel", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RATTLING_GUN = ITEMS.register("rattling_gun", key -> new RattlingGunItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> IDOL_OF_RATLANTIS = ITEMS.register("idol_of_ratlantis", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(RatsMod.RATLANTIS_SPECIAL).fireResistant(), 1));

	public static final DeferredHolder<Item, Item> PIRAT_SIGN = ITEMS.register("pirat_sign", key -> new SignItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(16), RatlantisBlockRegistry.PIRAT_SIGN.get(), RatlantisBlockRegistry.PIRAT_WALL_SIGN.get()));
	public static final DeferredHolder<Item, Item> PIRAT_HANGING_SIGN = ITEMS.register("pirat_hanging_sign", key -> new HangingSignItem(RatlantisBlockRegistry.PIRAT_HANGING_SIGN.get(), RatlantisBlockRegistry.PIRAT_WALL_HANGING_SIGN.get(), RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(16)));
	public static final DeferredHolder<Item, Item> PIRAT_BOAT = ITEMS.register("pirat_boat", key -> new PiratBoatItem(false, PiratWoodBoat.Type.PIRAT, RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> PIRAT_CHEST_BOAT = ITEMS.register("pirat_chest_boat", key -> new PiratBoatItem(true, PiratWoodBoat.Type.PIRAT, RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));

	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ARCHEOLOGIST = ITEMS.register("rat_upgrade_archeologist", key -> new ArcheologistRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_AUTOMATON_MOUNT = ITEMS.register("rat_upgrade_automaton_mount", key -> new MountRatUpgradeItem<>(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, 2, RatlantisEntityRegistry.RAT_MOUNT_AUTOMATON));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BIPLANE_MOUNT = ITEMS.register("rat_upgrade_biplane_mount", key -> new BiplaneMountUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BASIC_RATLANTEAN = ITEMS.register("rat_upgrade_basic_ratlantean", key -> new BaseRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_FERAL_BITE = ITEMS.register("rat_upgrade_feral_bite", key -> new FeralBiteRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BUCCANEER = ITEMS.register("rat_upgrade_buccaneer", key -> new BuccaneerRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_RATINATOR = ITEMS.register("rat_upgrade_ratinator", key -> new RatinatorRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_PSYCHIC = ITEMS.register("rat_upgrade_psychic", key -> new PsychicRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant()));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ETHEREAL = ITEMS.register("rat_upgrade_ethereal", key -> new EtherealRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant()));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_NONBELIEVER = ITEMS.register("rat_upgrade_nonbeliever", key -> new NonbelieverRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant()));
}
