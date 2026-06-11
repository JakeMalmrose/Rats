package com.github.alexthe666.rats.data.ratlantis.tags;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatlantisBlockRegistry;
import com.github.alexthe666.rats.registry.RatlantisItemRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;


// 26.1: stripped to a TagKey constant holder; the datagen provider half targeted the
// removed 1.21 datagen API and the shipped JSON under src/generated/resources is canonical.
public class RatlantisItemTags {

	public static final TagKey<Item> ORATCHALCUM_NUGGETS = ItemTags.create(Identifier.fromNamespaceAndPath("c", "nuggets/oratchalcum"));
	public static final TagKey<Item> ORATCHALCUM_INGOTS = ItemTags.create(Identifier.fromNamespaceAndPath("c", "ingots/oratchalcum"));
	public static final TagKey<Item> RAW_ORATCHALCUM_INGOTS = ItemTags.create(Identifier.fromNamespaceAndPath("c", "raw_materials/oratchalcum"));
	public static final TagKey<Item> RATLANTIS_GEMS = ItemTags.create(Identifier.fromNamespaceAndPath("c", "ingots/ratlantis_gem"));

	public static final TagKey<Item> PIRAT_LOGS = ItemTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "pirat_logs"));
	public static final TagKey<Item> STORAGE_BLOCKS_ORATCHALCUM = ItemTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/oratchalcum"));

	public static final TagKey<Item> ORES_CHEESE = ItemTags.create(Identifier.fromNamespaceAndPath("c", "ores/cheese"));
	public static final TagKey<Item> ORES_GEM_OF_RATLANTIS = ItemTags.create(Identifier.fromNamespaceAndPath("c", "ores/gem_of_ratlantis"));
	public static final TagKey<Item> ORES_ORATCHALCUM = ItemTags.create(Identifier.fromNamespaceAndPath("c", "ores/oratchalcum"));

}
