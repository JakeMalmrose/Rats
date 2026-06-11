package com.github.alexthe666.rats.data.ratlantis.tags;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.data.tags.RatsBlockTags;
import com.github.alexthe666.rats.registry.RatlantisBlockRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;


// 26.1: stripped to a TagKey constant holder; the datagen provider half targeted the
// removed 1.21 datagen API and the shipped JSON under src/generated/resources is canonical.
public class RatlantisBlockTags {
	public static final TagKey<Block> PIRAT_LOGS = BlockTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "pirat_logs"));
	public static final TagKey<Block> PIRAT_ONLY_BLOCKS = BlockTags.create(Identifier.fromNamespaceAndPath(RatsMod.MODID, "pirat_blocks"));

	public static final TagKey<Block> STORAGE_BLOCKS_ORATCHALCUM = BlockTags.create(Identifier.fromNamespaceAndPath("c", "storage_blocks/oratchalcum"));

	public static final TagKey<Block> ORES_CHEESE = BlockTags.create(Identifier.fromNamespaceAndPath("c", "ores/cheese"));
	public static final TagKey<Block> ORES_GEM_OF_RATLANTIS = BlockTags.create(Identifier.fromNamespaceAndPath("c", "ores/gem_of_ratlantis"));
	public static final TagKey<Block> ORES_ORATCHALCUM = BlockTags.create(Identifier.fromNamespaceAndPath("c", "ores/oratchalcum"));

}
