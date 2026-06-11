package com.github.alexthe666.rats.data.tags;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatsBlockRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;


// 26.1: stripped to a TagKey constant holder; the datagen provider half targeted the
// removed 1.21 datagen API and the shipped JSON under src/generated/resources is canonical.
public class RatsBlockTags {

	public static final TagKey<Block> MARBLED_CHEESE = BlockTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "marbled_cheese"));
	public static final TagKey<Block> TRASH_CAN_BLACKLIST = BlockTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "trash_can_blacklist"));
	public static final TagKey<Block> QUARRY_IGNORABLES = BlockTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "quarry_ignoreables"));
	public static final TagKey<Block> UNRAIDABLE_CONTAINERS = BlockTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "unraidable_containers"));
	public static final TagKey<Block> DIGGABLE_BLOCKS = BlockTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "diggable_blocks"));

	public static final TagKey<Block> STORAGE_BLOCKS_CHEESE = BlockTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/cheese"));
	public static final TagKey<Block> STORAGE_BLOCKS_BLUE_CHEESE = BlockTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/blue_cheese"));
	public static final TagKey<Block> STORAGE_BLOCKS_NETHER_CHEESE = BlockTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/nether_cheese"));

	public static final TagKey<Block> CRAFTING_TABLES = BlockTags.create(Identifier.fromNamespaceAndPath("c", "crafting_tables"));

}
