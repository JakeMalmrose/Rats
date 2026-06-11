package com.github.alexthe666.rats.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 1.21.2+ requires every Item/Block to carry its registry id on its Properties before construction.
 * Registrations use the DeferredRegister register(name, key -> ...) overload and wrap their
 * Properties with these helpers.
 */
public final class RatsRegistryHelper {

	private RatsRegistryHelper() {
	}

	public static Item.Properties withItemId(Identifier id, Item.Properties props) {
		return props.setId(ResourceKey.create(Registries.ITEM, id));
	}

	public static BlockBehaviour.Properties withBlockId(Identifier id, BlockBehaviour.Properties props) {
		return props.setId(ResourceKey.create(Registries.BLOCK, id));
	}
}
