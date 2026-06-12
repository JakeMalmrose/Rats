package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.RatsMod;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RatsVillagerRegistry {

	public static final DeferredRegister<PoiType> POIS = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.POINT_OF_INTEREST_TYPE, RatsMod.MODID);
	public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.VILLAGER_PROFESSION, RatsMod.MODID);

	public static final DeferredHolder<PoiType, PoiType> RATLANTIS_PORTAL = POIS.register("ratlantis_portal", () -> new PoiType(ImmutableSet.copyOf(RatlantisBlockRegistry.RATLANTIS_PORTAL.get().getStateDefinition().getPossibleStates()), 1, 1));
	public static final DeferredHolder<PoiType, PoiType> TRASH_CAN = POIS.register("trash_can", () -> new PoiType(ImmutableSet.copyOf(RatsBlockRegistry.TRASH_CAN.get().getStateDefinition().getPossibleStates()), 1, 1));

	// 26.1: VillagerProfession is a record with a Component name and data-driven trades — the
	// per-level TradeSets live at data/rats/trade_set/pet_shop_owner/level_N.json (replacing the
	// removed VillagerTradesEvent registrations in ForgeEvents).
	public static final DeferredHolder<VillagerProfession, VillagerProfession> PET_SHOP_OWNER = PROFESSIONS.register("pet_shop_owner", () -> new VillagerProfession(
			Component.translatable("entity.minecraft.villager.rats.pet_shop_owner"),
			(poiType) -> RatConfig.villagePetShops && poiType.is(TRASH_CAN.getKey()),
			(poiType) -> RatConfig.villagePetShops && poiType.is(TRASH_CAN.getKey()),
			ImmutableSet.of(),
			ImmutableSet.of(),
			RatsSoundRegistry.TRASH_CAN.get(),
			petShopTrades()));

	private static Int2ObjectMap<ResourceKey<TradeSet>> petShopTrades() {
		Int2ObjectMap<ResourceKey<TradeSet>> map = new Int2ObjectOpenHashMap<>();
		for (int level = 1; level <= 5; level++) {
			map.put(level, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(RatsMod.MODID, "pet_shop_owner/level_" + level)));
		}
		return map;
	}
}
