package com.github.alexthe666.rats.server.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

/**
 * Spawn egg keeping the 1.21.1 DeferredSpawnEggItem base/overlay RGB pair. 26.1 spawn eggs are
 * untinted single-texture items by default; tinting is applied client-side via the
 * rats:spawn_egg_layer item tint source on the vanilla two-layer template model.
 */
public class RatsSpawnEggItem extends SpawnEggItem {

	private final int backgroundColor;
	private final int highlightColor;

	public RatsSpawnEggItem(Item.Properties properties, int backgroundColor, int highlightColor) {
		super(properties);
		this.backgroundColor = 0xFF000000 | (backgroundColor & 0xFFFFFF);
		this.highlightColor = 0xFF000000 | (highlightColor & 0xFFFFFF);
	}

	public int getTintColor(int layer) {
		return layer == 0 ? this.backgroundColor : this.highlightColor;
	}
}
