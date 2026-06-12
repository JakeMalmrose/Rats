package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatsMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

// 26.1: the capability surface switched from IItemHandler/IEnergyStorage to the transaction-based
// transfer API (ResourceHandler<ItemResource> / EnergyHandler) and NeoForge ships no
// legacy->new bridge (only IItemHandler.of(...) for the consuming direction). Rats' block
// entities still run on ItemStackHandler internally, so exposing them to hoppers/pipes needs
// each handler migrated to ItemStacksResourceHandler first.
//
// TODO(26.1 follow-up): migrate BE handlers to ItemStacksResourceHandler subclasses and register:
//   - Capabilities.Item.BLOCK for AUTO_CURDLER (sided), RAT_QUARRY (output), UPGRADE_COMBINER,
//     RAT_CRAFTING_TABLE (buffer/result split by side)
//   - Capabilities.Fluid.BLOCK for AUTO_CURDLER's tank
//   - Capabilities.Energy.BLOCK for RAT_CAGE_WHEEL
//   - Capabilities.Item.ENTITY for TAMED_RAT's carry inventory
// Rats' own item-transport AI does not depend on these (it consumes other blocks' caps via
// Capabilities.Item.BLOCK + IItemHandler.of and touches its own BEs directly), so first-boot
// functionality is unaffected; external automation mods just can't push/pull yet.
@EventBusSubscriber(modid = RatsMod.MODID)
public final class RatsCapabilities {

	private RatsCapabilities() {
	}

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
	}
}
