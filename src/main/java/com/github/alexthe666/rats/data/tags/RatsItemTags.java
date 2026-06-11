package com.github.alexthe666.rats.data.tags;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatsBlockRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;


// 26.1: stripped to a TagKey constant holder; the datagen provider half targeted the
// removed 1.21 datagen API and the shipped JSON under src/generated/resources is canonical.
public class RatsItemTags {

	public static final TagKey<Item> CHEESE_ITEMS = ItemTags.create(Identifier.fromNamespaceAndPath("c", "cheese"));

	public static final TagKey<Item> CRAFTING_TABLES = ItemTags.create(Identifier.fromNamespaceAndPath("c", "crafting_tables"));
	public static final TagKey<Item> VEGETABLES = ItemTags.create(Identifier.fromNamespaceAndPath("c", "vegetables"));
	public static final TagKey<Item> PLASTICS = ItemTags.create(Identifier.fromNamespaceAndPath("c", "plastics"));
	public static final TagKey<Item> HIDES_RAT_WHISKERS = ItemTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "hides_rat_whiskers"));

	public static final TagKey<Item> IGLOOS = ItemTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "igloos"));
	public static final TagKey<Item> TUBES = ItemTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "tubes"));
	public static final TagKey<Item> HAMMOCKS = ItemTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "hammocks"));
	public static final TagKey<Item> MARBLED_CHEESE = ItemTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "marbled_cheese"));

	public static final TagKey<Item> STORAGE_BLOCKS_CHEESE = ItemTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/cheese"));
	public static final TagKey<Item> STORAGE_BLOCKS_BLUE_CHEESE = ItemTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/blue_cheese"));
	public static final TagKey<Item> STORAGE_BLOCKS_NETHER_CHEESE = ItemTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/nether_cheese"));

}
