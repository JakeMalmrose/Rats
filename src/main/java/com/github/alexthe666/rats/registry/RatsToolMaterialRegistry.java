package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

// 26.1: Tier/SimpleTier were replaced by the ToolMaterial record; repair ingredients are item tags
// (data/rats/tags/item/repairs/*.json) instead of Ingredient suppliers.
public class RatsToolMaterialRegistry {

	public static final ToolMaterial CUTLASS = new ToolMaterial(
			BlockTags.INCORRECT_FOR_IRON_TOOL,
			300, 5.0F, 4.5F, 20, repairs("cutlass"));

	public static final ToolMaterial GHOST_CUTLASS = new ToolMaterial(
			BlockTags.INCORRECT_FOR_IRON_TOOL,
			300, 5.0F, 4.5F, 20, repairs("ghost_cutlass"));

	public static final ToolMaterial BAGHNAKHS = new ToolMaterial(
			BlockTags.INCORRECT_FOR_STONE_TOOL,
			500, 2.0F, 3.5F, 15, repairs("baghnakhs"));

	public static final ToolMaterial PLAGUE_SCYTHE = new ToolMaterial(
			BlockTags.INCORRECT_FOR_IRON_TOOL,
			1500, 5.0F, 6.0F, 20, repairs("plague_scythe"));

	public static final ToolMaterial RATLANTIS = new ToolMaterial(
			BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
			3500, 9.0F, 7.0F, 20, repairs("ratlantis_tools"));

	private static TagKey<Item> repairs(String name) {
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RatsMod.MODID, "repairs/" + name));
	}
}
