package com.github.alexthe666.rats.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.data.tags.RatsBannerPatternTags;
import com.github.alexthe666.rats.server.items.*;
import com.github.alexthe666.rats.server.items.upgrades.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class RatsItemRegistry {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, RatsMod.MODID);

	//Currently unused item textures:
	//blue cheese stick
	//blueprint
	//confetti launcher
	//hardhat
	//holocards
	//rat credit card
	//rat diamond
	//rat upgrade fragment
	//rat upgrade nuke
	//rat upgrade redstone
	//rat upgrade shield
	//rat upgrade shield max
	//ratlantean arrow
	//stick of cheese

	public static final DeferredHolder<Item, Item> CHEESE = ITEMS.register("cheese", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5F).build())));
	public static final DeferredHolder<Item, Item> RAW_RAT = ITEMS.register("raw_rat", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build())));
	public static final DeferredHolder<Item, Item> COOKED_RAT = ITEMS.register("cooked_rat", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).build())));
	public static final DeferredHolder<Item, Item> RAT_PELT = ITEMS.register("rat_pelt", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_PAW = ITEMS.register("rat_paw", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> CHEESE_STICK = ITEMS.register("cheese_stick", key -> new RatStaffItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RADIUS_STICK = ITEMS.register("radius_stick", key -> new RatStaffItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> PATROL_STICK = ITEMS.register("patrol_stick", key -> new RatStaffItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RAT_WHISTLE = ITEMS.register("rat_whistle", key -> new RatWhistleItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RAT_FLUTE = ITEMS.register("rat_flute", key -> new RatFluteItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> GILDED_RAT_FLUTE = ITEMS.register("gilded_rat_flute", key -> new GildedRatFluteItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).durability(100)));
	public static final DeferredHolder<Item, Item> CHEF_TOQUE = ITEMS.register("chef_toque", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.CHEF_TOQUE, 0));
	public static final DeferredHolder<Item, Item> PIPER_HAT = ITEMS.register("piper_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.PIPER_HAT, 1));
	public static final DeferredHolder<Item, Item> STRING_CHEESE = ITEMS.register("string_cheese", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.4F).fast().build()), 1));
	public static final DeferredHolder<Item, Item> CREATIVE_CHEESE = ITEMS.register("creative_cheese", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC).fireResistant(), 1, true));
	public static final DeferredHolder<Item, Item> BLUE_CHEESE = ITEMS.register("blue_cheese", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5F).build())));
	public static final DeferredHolder<Item, Item> NETHER_CHEESE = ITEMS.register("nether_cheese", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5F).build()).fireResistant()) {
		@Override
		public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
			if (!entity.fireImmune()) {
				entity.igniteForSeconds(5);
			}
			return super.finishUsingItem(stack, level, entity);
		}
	});
	public static final DeferredHolder<Item, Item> ASSORTED_VEGETABLES = ITEMS.register("assorted_vegetables", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.3F).build())));
	public static final DeferredHolder<Item, Item> RAT_BURGER = ITEMS.register("rat_burger", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(8).saturationModifier(1.0F).build()), 1));
	public static final DeferredHolder<Item, Item> RAT_SACK = ITEMS.register("rat_sack", key -> new RatSackItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RAT_ARROW = ITEMS.register("rat_arrow", key -> new RatArrowItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RAT_CAPTURE_NET = ITEMS.register("rat_capture_net", key -> new RatCaptureNetItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> FEATHERY_WING = ITEMS.register("feathery_wing", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> DRAGON_WING = ITEMS.register("dragon_wing", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> PLASTIC_WASTE = ITEMS.register("plastic_waste", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAW_PLASTIC = ITEMS.register("raw_plastic", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> CONTAMINATED_FOOD = ITEMS.register("contaminated_food", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(
			new FoodProperties.Builder().nutrition(2).saturationModifier(0.5F).build(),
			// 26.1: food effects moved off FoodProperties onto the Consumable component
			Consumable.builder()
					.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(RatsEffectRegistry.PLAGUE, 2400), 0.3F))
					.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 2400), 0.3F))
					.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 2400), 0.3F))
					.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.CONFUSION, 2400), 0.3F))
					.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2400), 0.3F))
					.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.WITHER, 2400), 0.3F))
					.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2400), 0.3F))
					.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.UNLUCK, 2400), 1.0F))
					.build())));
	public static final DeferredHolder<Item, Item> PURIFYING_LIQUID = ITEMS.register("purifying_liquid", key -> new PurifyingLiquidItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), false));
	public static final DeferredHolder<Item, Item> CRIMSON_FLUID = ITEMS.register("crimson_liquid", key -> new PurifyingLiquidItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), true));
	public static final DeferredHolder<Item, Item> PLAGUE_ESSENCE = ITEMS.register("plague_essence", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.UNCOMMON), 1));
	public static final DeferredHolder<Item, Item> PLAGUE_DOCTORATE = ITEMS.register("plague_doctorate", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON), 1));
	public static final DeferredHolder<Item, Item> HERB_BUNDLE = ITEMS.register("herb_bundle", key -> new PlagueHealerItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).build()), 0.1F));
	public static final DeferredHolder<Item, Item> TREACLE = ITEMS.register("treacle", key -> new PlagueHealerItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.6F).build()), 0.25F));
	public static final DeferredHolder<Item, Item> PLAGUE_LEECH = ITEMS.register("plague_leech", key -> new PlagueLeechItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> PLAGUE_STEW = ITEMS.register("plague_stew", key -> new PlagueHealerItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).craftRemainder(Items.BOWL).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()), 1.0F));
	public static final DeferredHolder<Item, Item> RAT_SKULL = ITEMS.register("rat_skull", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 1));
	public static final DeferredHolder<Item, Item> GOLDEN_RAT_SKULL = ITEMS.register("golden_rat_skull", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> CORRUPT_RAT_SKULL = ITEMS.register("corrupt_rat_skull", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> PLAGUE_TOME = ITEMS.register("plague_tome", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).rarity(Rarity.RARE).fireResistant(), 2, true));
	public static final DeferredHolder<Item, Item> PLAGUE_SCYTHE = ITEMS.register("plague_scythe", key -> new PlagueScytheItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.RARE).fireResistant()));
	public static final DeferredHolder<Item, Item> FILTH = ITEMS.register("filth", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> FILTH_CORRUPTION = ITEMS.register("filth_corruption", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.RARE), 1));
	public static final DeferredHolder<Item, Item> TANGLED_RAT_TAILS = ITEMS.register("tangled_rat_tails", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.RARE)));
	public static final DeferredHolder<Item, Item> CHARGED_CREEPER_CHUNK = ITEMS.register("charged_creeper_chunk", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.UNCOMMON)));
	public static final DeferredHolder<Item, Item> RATBOW_ESSENCE = ITEMS.register("ratbow_essence", key -> new RatbowEssenceItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_PAPERS = ITEMS.register("rat_papers", key -> new RatPapersItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item>[] RAT_HAMMOCKS = new DeferredHolder[16];
	public static final DeferredHolder<Item, Item>[] RAT_TUBES = new DeferredHolder[16];
	public static final DeferredHolder<Item, Item>[] RAT_IGLOOS = new DeferredHolder[16];

	static {
		for (int i = 0; i < 16; i++) {
			int finalI = i;
			RAT_TUBES[i] = ITEMS.register("rat_tube_" + DyeColor.byId(i).getName(), key -> new RatTubeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), DyeColor.byId(finalI)));
			RAT_IGLOOS[i] = ITEMS.register("rat_igloo_" + DyeColor.byId(i).getName(), key -> new RatIglooItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), DyeColor.byId(finalI)));
			RAT_HAMMOCKS[i] = ITEMS.register("rat_hammock_" + DyeColor.byId(i).getName(), key -> new RatHammockItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), DyeColor.byId(finalI)));
		}
	}

	public static final DeferredHolder<Item, Item> RAT_WATER_BOTTLE = ITEMS.register("rat_water_bottle", key -> new RatDecorationItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_SEED_BOWL = ITEMS.register("rat_seed_bowl", key -> new RatDecorationItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_BREEDING_LANTERN = ITEMS.register("rat_breeding_lantern", key -> new RatDecorationItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_WHEEL = ITEMS.register("rat_wheel", key -> new RatDecorationItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> CONFIT_BYALDI = ITEMS.register("confit_byaldi", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(50).saturationModifier(1.0F).alwaysEdible().build(), Consumable.builder().onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(RatsEffectRegistry.SYNESTHESIA, 2400), 1.0F)).build())));
	public static final DeferredHolder<Item, Item> POTATO_PANCAKE = ITEMS.register("potato_pancake", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5F).alwaysEdible().build())));
	public static final DeferredHolder<Item, Item> LITTLE_BLACK_SQUASH_BALLS = ITEMS.register("little_black_squash_balls", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> LITTLE_BLACK_WORM = ITEMS.register("little_black_worm", key -> new LoreTagItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 1));
	public static final DeferredHolder<Item, Item> CENTIPEDE = ITEMS.register("centipede", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> POTATO_KNISHES = ITEMS.register("potato_knishes", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).food(new FoodProperties.Builder().nutrition(50).saturationModifier(1.0F).alwaysEdible().build(), Consumable.builder().onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(RatsEffectRegistry.SYNESTHESIA, 2400), 1.0F)).build())));
	public static final DeferredHolder<Item, Item> TINY_COIN = ITEMS.register("tiny_coin", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> TOKEN_FRAGMENT = ITEMS.register("token_fragment", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.UNCOMMON)));
	public static final DeferredHolder<Item, Item> TOKEN_PIECE = ITEMS.register("token_piece", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).rarity(Rarity.RARE)));
	public static final DeferredHolder<Item, Item> ARCHEOLOGIST_HAT = ITEMS.register("archeologist_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON), RatsArmorMaterialRegistry.GENERIC_HAT, 1));
	public static final DeferredHolder<Item, Item> FARMER_HAT = ITEMS.register("farmer_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.FARMER_HAT, 0));
	public static final DeferredHolder<Item, Item> FISHERMAN_HAT = ITEMS.register("fisherman_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.GENERIC_HAT, 0));
	public static final DeferredHolder<Item, Item> RAT_FEZ = ITEMS.register("rat_fez", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.FEZ, 2));
	public static final DeferredHolder<Item, Item> TOP_HAT = ITEMS.register("top_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.TOP_HAT, 0));
	public static final DeferredHolder<Item, Item> SANTA_HAT = ITEMS.register("santa_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.SANTA_HAT, 0));
	public static final DeferredHolder<Item, Item> HALO_HAT = ITEMS.register("halo_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.HALO, 0));
	public static final DeferredHolder<Item, Item> PARTY_HAT = ITEMS.register("party_hat", key -> new PartyHatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> PIRAT_HAT = ITEMS.register("pirat_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.GENERIC_HAT, 0));
	public static final DeferredHolder<Item, Item> RAT_KING_CROWN = ITEMS.register("rat_king_crown", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).rarity(Rarity.RARE), RatsArmorMaterialRegistry.CROWN, 0));
	public static final DeferredHolder<Item, Item> PLAGUE_DOCTOR_MASK = ITEMS.register("plague_doctor_mask", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.PLAGUE_MASK, 1));
	public static final DeferredHolder<Item, Item> BLACK_DEATH_MASK = ITEMS.register("black_death_mask", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).fireResistant().rarity(Rarity.RARE), RatsArmorMaterialRegistry.PLAGUE_MASK, 1));
	public static final DeferredHolder<Item, Item> EXTERMINATOR_HAT = ITEMS.register("exterminator_hat", key -> new HatItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), RatsArmorMaterialRegistry.GENERIC_HAT, 0));
	// 1.21: music discs are plain Items carrying DataComponents.JUKEBOX_PLAYABLE referencing a JukeboxSong
	// registered via datapack JSON at data/rats/jukebox_song/{name}.json. The ResourceKey is resolved against
	// Registries.JUKEBOX_SONG at runtime; the JukeboxPlayable record stores it in an EitherHolder so the song
	// holder is bound lazily once the registry is loaded.
	public static final net.minecraft.resources.ResourceKey<net.minecraft.world.item.JukeboxSong> SONG_MICE_ON_VENUS =
			net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.JUKEBOX_SONG,
					net.minecraft.resources.Identifier.fromNamespaceAndPath(RatsMod.MODID, "mice_on_venus"));
	public static final net.minecraft.resources.ResourceKey<net.minecraft.world.item.JukeboxSong> SONG_LIVING_MICE =
			net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.JUKEBOX_SONG,
					net.minecraft.resources.Identifier.fromNamespaceAndPath(RatsMod.MODID, "living_mice"));
	public static final DeferredHolder<Item, Item> MUSIC_DISC_MICE_ON_VENUS = ITEMS.register("music_disc_mice_on_venus", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(SONG_MICE_ON_VENUS)));
	public static final DeferredHolder<Item, Item> MUSIC_DISC_LIVING_MICE = ITEMS.register("music_disc_living_mice", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(SONG_LIVING_MICE)));


	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BASIC = ITEMS.register("rat_upgrade_basic", key -> new BaseRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_CREATIVE = ITEMS.register("rat_upgrade_creative", key -> new CreativeRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant()));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_JURY_RIGGED = ITEMS.register("rat_upgrade_jury_rigged", key -> new JuryRiggedRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_SPEED = ITEMS.register("rat_upgrade_speed", key -> new StatBoostingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 0, () -> Map.of(Attributes.MOVEMENT_SPEED, 0.5D)));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_PLATTER = ITEMS.register("rat_upgrade_platter", key -> new PlatterRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_HEALTH = ITEMS.register("rat_upgrade_health", key -> new StatBoostingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 0, () -> Map.of(Attributes.MAX_HEALTH, 20.0D)));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ARMOR = ITEMS.register("rat_upgrade_armor", key -> new StatBoostingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 0, () -> Map.of(Attributes.ARMOR, 10.0D)));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BOW = ITEMS.register("rat_upgrade_bow", key -> new RangedWeaponRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), false));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_CROSSBOW = ITEMS.register("rat_upgrade_crossbow", key -> new RangedWeaponRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), true));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_DEMON = ITEMS.register("rat_upgrade_demon", key -> new DemonRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_STRENGTH = ITEMS.register("rat_upgrade_strength", key -> new StatBoostingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 0, () -> Map.of(Attributes.ATTACK_DAMAGE, 5.0D)));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_REMEDY = ITEMS.register("rat_upgrade_remedy", key -> new StatBoostingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, 1, Map::of, true));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_WARRIOR = ITEMS.register("rat_upgrade_warrior", key -> new StatBoostingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, () -> Map.of(Attributes.MAX_HEALTH, RatConfig.warriorHealthUpgrade, Attributes.ARMOR, RatConfig.warriorArmorUpgrade, Attributes.ATTACK_DAMAGE, RatConfig.warriorDamageUpgrade)));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_GOD = ITEMS.register("rat_upgrade_god", key -> new StatBoostingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant(), 3, () -> Map.of(Attributes.MAX_HEALTH, RatConfig.godHealthUpgrade, Attributes.ARMOR, RatConfig.godArmorUpgrade, Attributes.ATTACK_DAMAGE, RatConfig.godDamageUpgrade)));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_CHEF = ITEMS.register("rat_upgrade_chef", key -> new ChefRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_CRAFTING = ITEMS.register("rat_upgrade_crafting", key -> new CraftingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BLACKLIST = ITEMS.register("rat_upgrade_blacklist", key -> new RatListUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), 0, 3));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_WHITELIST = ITEMS.register("rat_upgrade_whitelist", key -> new RatListUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1), 0, 3));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_MOB_FILTER = ITEMS.register("rat_upgrade_mob_filter", key -> new MobFilterUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_REPLANTER = ITEMS.register("rat_upgrade_replanter", key -> new BaseRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 0, 1));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_FLIGHT = ITEMS.register("rat_upgrade_flight", key -> new BaseFlightRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 0, 3) {
		public ItemStack getWing() {
			return RatsItemRegistry.FEATHERY_WING.get().getDefaultInstance();
		}
	});
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_DRAGON = ITEMS.register("rat_upgrade_dragon", key -> new DragonRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BEE = ITEMS.register("rat_upgrade_bee", key -> new BeeRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_AQUATIC = ITEMS.register("rat_upgrade_aquatic", key -> new AquaticRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ENDER = ITEMS.register("rat_upgrade_ender", key -> new EnderRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_LUMBERJACK = ITEMS.register("rat_upgrade_lumberjack", key -> new LumberjackRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	//public static final DeferredHolder<Item, Item> RAT_UPGRADE_MINER_ORE = ITEMS.register("rat_upgrade_miner_ore", key -> new OreMinerRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	//public static final DeferredHolder<Item, Item> RAT_UPGRADE_MINER = ITEMS.register("rat_upgrade_miner", key -> new MinerRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 1, 3));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_QUARRY = ITEMS.register("rat_upgrade_quarry", key -> new QuarryRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_FARMER = ITEMS.register("rat_upgrade_farmer", key -> new FarmerRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BREEDER = ITEMS.register("rat_upgrade_breeder", key -> new BreederRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_FISHERMAN = ITEMS.register("rat_upgrade_fisherman", key -> new FishermanRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_TICK_ACCELERATOR = ITEMS.register("rat_upgrade_tick_accelerator", key -> new TickAccelRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_TIME_MANIPULATOR = ITEMS.register("rat_upgrade_time_manipulator", key -> new TimeManipuRatorUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ASBESTOS = ITEMS.register("rat_upgrade_asbestos", key -> new AsbestosRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_UNDERWATER = ITEMS.register("rat_upgrade_underwater", key -> new UnderwaterRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_POISON = ITEMS.register("rat_upgrade_poison", key -> new PoisonRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_DAMAGE_PROTECTION = ITEMS.register("rat_upgrade_damage_protection", key -> new ProtectionRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ORE_DOUBLING = ITEMS.register("rat_upgrade_ore_doubling", key -> new OreDoublingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BASIC_ENERGY = ITEMS.register("rat_upgrade_basic_energy", key -> new EnergyRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 0, RatConfig.ratRFTransferBasic, RatConfig.ratChargeBasic));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ADVANCED_ENERGY = ITEMS.register("rat_upgrade_advanced_energy", key -> new EnergyRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 1, RatConfig.ratRFTransferAdvanced, RatConfig.ratChargeAdvanced));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ELITE_ENERGY = ITEMS.register("rat_upgrade_elite_energy", key -> new EnergyRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, RatConfig.ratRFTransferElite, RatConfig.ratChargeElite));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_EXTREME_ENERGY = ITEMS.register("rat_upgrade_extreme_energy", key -> new EnergyRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).fireResistant(), 3, RatConfig.ratRFTransferExtreme, RatConfig.ratChargeExtreme));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BOTTLER = ITEMS.register("rat_upgrade_bottler", key -> new BottlerRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BUCKET = ITEMS.register("rat_upgrade_bucket", key -> new BucketRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 0, 2, FluidType.BUCKET_VOLUME));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BIG_BUCKET = ITEMS.register("rat_upgrade_big_bucket", key -> new BucketRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, 2, FluidType.BUCKET_VOLUME * 5));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_MILKER = ITEMS.register("rat_upgrade_milker", key -> new MilkerRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_SHEARS = ITEMS.register("rat_upgrade_shears", key -> new ShearsRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ARISTOCRAT = ITEMS.register("rat_upgrade_aristocrat", key -> new AristocratRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_TNT = ITEMS.register("rat_upgrade_tnt", key -> new TNTRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_TNT_SURVIVOR = ITEMS.register("rat_upgrade_tnt_survivor", key -> new TNTSurvivorRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_PLACER = ITEMS.register("rat_upgrade_placer", key -> new PlacerRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_VOODOO = ITEMS.register("rat_upgrade_voodoo", key -> new StatBoostingRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, 0, () -> Map.of(Attributes.MAX_HEALTH, RatConfig.voodooHealthUpgrade), true));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ENCHANTER = ITEMS.register("rat_upgrade_enchanter", key -> new EnchanterRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 1, 2));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_DISENCHANTER = ITEMS.register("rat_upgrade_disenchanter", key -> new EnchanterRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 1, 1));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_CHRISTMAS = ITEMS.register("rat_upgrade_christmas", key -> new ChristmasRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_NO_FLUTE = ITEMS.register("rat_upgrade_no_flute", key -> new BaseRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 0, 1));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_CARRAT = ITEMS.register("rat_upgrade_carrat", key -> new CarratRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_DJ = ITEMS.register("rat_upgrade_dj", key -> new DJRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_SUPPORT = ITEMS.register("rat_upgrade_support", key -> new SupportRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_ANGEL = ITEMS.register("rat_upgrade_angel", key -> new BaseRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, 2));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_UNDEAD = ITEMS.register("rat_upgrade_undead", key -> new UndeadRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_PICKPOCKET = ITEMS.register("rat_upgrade_pickpocket", key -> new PickpocketRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_GARDENER = ITEMS.register("rat_upgrade_gardener", key -> new RatGardenerUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_IDOL = ITEMS.register("rat_upgrade_idol", key -> new IdolRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_SCULKED = ITEMS.register("rat_upgrade_sculked", key -> new SculkedRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BASIC_MOUNT = ITEMS.register("rat_upgrade_basic_mount", key -> new BaseRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_CHICKEN_MOUNT = ITEMS.register("rat_upgrade_chicken_mount", key -> new MountRatUpgradeItem<>(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, 2, RatsEntityRegistry.RAT_MOUNT_CHICKEN));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_GOLEM_MOUNT = ITEMS.register("rat_upgrade_golem_mount", key -> new MountRatUpgradeItem<>(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, 2, RatsEntityRegistry.RAT_MOUNT_GOLEM));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_STRIDER_MOUNT = ITEMS.register("rat_upgrade_strider_mount", key -> new StriderMountRatUpgradeItem<>(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, 2, RatsEntityRegistry.RAT_STRIDER_MOUNT));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_BEAST_MOUNT = ITEMS.register("rat_upgrade_beast_mount", key -> new MountRatUpgradeItem<>(RatsRegistryHelper.withItemId(key, new Item.Properties()), 2, 2, RatsEntityRegistry.RAT_MOUNT_BEAST));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_COMBINED = ITEMS.register("rat_upgrade_combined", key -> new CombinedRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1)));
	public static final DeferredHolder<Item, Item> RAT_UPGRADE_COMBINED_CREATIVE = ITEMS.register("rat_upgrade_combined_creative", key -> new CombinedRatUpgradeItem(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).fireResistant()));

	public static final DeferredHolder<Item, Item> RAT_NUGGET = ITEMS.register("rat_nugget", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_NUGGET_ORE = ITEMS.register("rat_nugget_ore", key -> new OreRatNuggetItem(RatsRegistryHelper.withItemId(key, new Item.Properties())));
	public static final DeferredHolder<Item, Item> RAT_BANNER_PATTERN = ITEMS.register("rat_banner_pattern", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).delayedComponent(DataComponents.PROVIDES_BANNER_PATTERNS, context -> context.getOrThrow(RatsBannerPatternTags.RAT_BANNER_PATTERN))));
	public static final DeferredHolder<Item, Item> CHEESE_BANNER_PATTERN = ITEMS.register("cheese_banner_pattern", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).delayedComponent(DataComponents.PROVIDES_BANNER_PATTERNS, context -> context.getOrThrow(RatsBannerPatternTags.CHEESE_BANNER_PATTERN))));
	public static final DeferredHolder<Item, Item> RAC_BANNER_PATTERN = ITEMS.register("rat_and_crossbones_banner_pattern", key -> new Item(RatsRegistryHelper.withItemId(key, new Item.Properties()).stacksTo(1).delayedComponent(DataComponents.PROVIDES_BANNER_PATTERNS, context -> context.getOrThrow(RatsBannerPatternTags.RAC_BANNER_PATTERN))));
}
