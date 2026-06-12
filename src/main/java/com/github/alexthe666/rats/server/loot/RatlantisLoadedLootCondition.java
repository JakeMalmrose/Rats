package com.github.alexthe666.rats.server.loot;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatsLootRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import com.mojang.serialization.MapCodec;

public class RatlantisLoadedLootCondition implements LootItemCondition {

	public static final MapCodec<RatlantisLoadedLootCondition> CODEC = MapCodec.unit(RatlantisLoadedLootCondition::new);

	@Override
	public MapCodec<? extends LootItemCondition> codec() {
		return RatsLootRegistry.RATLANTIS_LOADED.get();
	}

	@Override
	public boolean test(LootContext context) {
		return RatsMod.RATLANTIS_DATAPACK_ENABLED;
	}
}
