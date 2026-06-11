package com.github.alexthe666.rats.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.block.*;
import com.github.alexthe666.rats.server.items.RatsBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Function;
import java.util.function.Supplier;

public class RatsBlockRegistry {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, RatsMod.MODID);

	public static final DeferredHolder<Block, Block> BLOCK_OF_CHEESE = register("block_of_cheese", key -> new Block(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).mapColor(MapColor.COLOR_YELLOW).sound(SoundType.SLIME_BLOCK).strength(0.6F, 0.0F)));
	public static final DeferredHolder<Block, Block> MILK_CAULDRON = register("cauldron_milk", key -> new MilkCauldronBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(Blocks.CAULDRON))));
	public static final DeferredHolder<Block, Block> CHEESE_CAULDRON = register("cauldron_cheese", key -> new CheeseCauldronBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(Blocks.CAULDRON)), RatsBlockRegistry.BLOCK_OF_CHEESE, RatsCauldronRegistry.CHEESE));
	public static final DeferredHolder<Block, Block> BLUE_CHEESE_CAULDRON = register("cauldron_blue_cheese", key -> new CheeseCauldronBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(Blocks.CAULDRON)), RatsBlockRegistry.BLOCK_OF_BLUE_CHEESE, RatsCauldronRegistry.BLUE_CHEESE));
	public static final DeferredHolder<Block, Block> NETHER_CHEESE_CAULDRON = register("cauldron_nether_cheese", key -> new CheeseCauldronBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(Blocks.CAULDRON)), RatsBlockRegistry.BLOCK_OF_NETHER_CHEESE, RatsCauldronRegistry.NETHER_CHEESE));
	public static final DeferredHolder<Block, Block> RAT_HOLE = BLOCKS.register("rat_hole", key -> new RatHoleBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.WOOD).noOcclusion().dynamicShape().strength(1.3F, 0.0F)));
	public static final DeferredHolder<Block, Block> RAT_CAGE = register("rat_cage", key -> new RatCageBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.METAL).noOcclusion().strength(2.0F, 0.0F)));
	public static final DeferredHolder<Block, Block> RAT_CAGE_DECORATED = BLOCKS.register("rat_cage_decorated", key -> new RatCageDecoratedBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(RAT_CAGE.get()))));
	public static final DeferredHolder<Block, Block> RAT_CAGE_BREEDING_LANTERN = BLOCKS.register("rat_cage_breeding_lantern", key -> new RatCageBreedingLanternBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(RAT_CAGE.get()))));
	public static final DeferredHolder<Block, Block> RAT_CAGE_WHEEL = BLOCKS.register("rat_cage_wheel", key -> new RatCageWheelBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(RAT_CAGE.get()))));
	public static final DeferredHolder<Block, Block> FISH_BARREL = register("fish_barrel", key -> new Block(RatsRegistryHelper.withBlockId(key, BlockBehaviour.Properties.of()).strength(2.0F, 10.0F).sound(SoundType.WOOD)));
	public static final DeferredHolder<Block, Block> RAT_CRAFTING_TABLE = register("rat_crafting_table", key -> new RatCraftingTableBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.SLIME_BLOCK).strength(2.0F, 0.0F)));
	public static final DeferredHolder<Block, Block> AUTO_CURDLER = register("auto_curdler", key -> new AutoCurdlerBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion().strength(2.0F, 0.0F)));
	public static final DeferredHolder<Block, Block> RAT_TRAP = register("rat_trap", key -> new RatTrapBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.WOOD).noCollission().strength(1.0F, 0.0F)));
	public static final DeferredHolder<Block, Block> RAT_TUBE_COLOR = BLOCKS.register("rat_tube", key -> new RatTubeBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.WOOD).strength(0.9F, 0.0F)));
	public static final DeferredHolder<Block, Block> RAT_UPGRADE_BLOCK = register("rat_upgrade_block", key -> new RatUpgradeBlock(RatsRegistryHelper.withBlockId(key, BlockBehaviour.Properties.of()).sound(SoundType.SLIME_BLOCK).strength(0.6F, 0.0F)));
	public static final DeferredHolder<Block, Block> DYE_SPONGE = register("dye_sponge", key -> new DyeSpongeBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).strength(0.6F, 0.0F).sound(SoundType.CROP)));
	public static final DeferredHolder<Block, Block> GARBAGE_PILE = register("garbage_pile", key -> new GarbageBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.GRAVEL).strength(0.7F, 1.0F).randomTicks(), 1.0F));
	public static final DeferredHolder<Block, Block> CURSED_GARBAGE = register("cursed_garbage", key -> new CursedGarbageBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(GARBAGE_PILE.get()))));
	public static final DeferredHolder<Block, Block> COMPRESSED_GARBAGE = register("compressed_garbage", key -> new GarbageBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(GARBAGE_PILE.get())), 2.0F));
	public static final DeferredHolder<Block, Block> PURIFIED_GARBAGE = register("purified_garbage", key -> new PurifiedGarbageBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(GARBAGE_PILE.get()))));
	public static final DeferredHolder<Block, Block> PIED_GARBAGE = register("pied_garbage", key -> new PiedGarbageBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.ofFullCopy(GARBAGE_PILE.get()))));
	public static final DeferredHolder<Block, Block> MARBLED_CHEESE_RAW = register("marbled_cheese_raw", key -> new Block(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).requiresCorrectToolForDrops().strength(2.0F, 10.0F).sound(SoundType.STONE)));
	public static final DeferredHolder<Block, Block> JACK_O_RATERN = register("jack_o_ratern", key -> new SetupHorizontalBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.WOOD).strength(1.0F, 0).lightLevel(value -> 15)));
	public static final DeferredHolder<Block, Block> BLOCK_OF_BLUE_CHEESE = register("block_of_blue_cheese", key -> new Block(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).strength(0.6F, 0.0F).sound(SoundType.SLIME_BLOCK)));
	public static final DeferredHolder<Block, Block> BLOCK_OF_NETHER_CHEESE = register("block_of_nether_cheese", key -> new Block(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).strength(0.6F, 0.0F).sound(SoundType.SLIME_BLOCK)));
	public static final DeferredHolder<Block, Block> PIED_WOOL = register("pied_wool", key -> new Block(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).strength(1.0F, 0.0F).sound(SoundType.WOOL)));
	public static final DeferredHolder<Block, Block> UPGRADE_COMBINER = register("upgrade_combiner", key -> new UpgradeCombinerBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.WOOD).requiresCorrectToolForDrops().noOcclusion().strength(5.0F, 0.0F).lightLevel(value -> 4)));
	public static final DeferredHolder<Block, Block> UPGRADE_SEPARATOR = register("upgrade_separator", key -> new UpgradeSeparatorBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.WOOD).requiresCorrectToolForDrops().noOcclusion().strength(5.0F, 0.0F).lightLevel(value -> 4)));
	public static final DeferredHolder<Block, Block> MANHOLE = register("manhole", key -> new TrapDoorBlock(BlockSetType.IRON, RatsRegistryHelper.withBlockId(key, Block.Properties.of()).mapColor(MapColor.TERRACOTTA_BROWN).requiresCorrectToolForDrops().strength(10.0F).sound(SoundType.ANVIL).noOcclusion()));
	public static final DeferredHolder<Block, Block> TRASH_CAN = register("trash_can", key -> new TrashCanBlock(RatsRegistryHelper.withBlockId(key, BlockBehaviour.Properties.of()).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion().strength(2.0F, 0.0F)));
	public static final DeferredHolder<Block, Block> RAT_ATTRACTOR = register("rat_attractor", key -> new RatAttractorBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.LANTERN).requiresCorrectToolForDrops().noOcclusion().randomTicks().strength(1.0F, 0.0F)));
	public static final DeferredHolder<Block, Block> RAT_QUARRY = register("rat_quarry", key -> new RatQuarryBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.SLIME_BLOCK).strength(2.0F, 0.0F)));
	public static final DeferredHolder<Block, Block> RAT_QUARRY_PLATFORM = register("rat_quarry_platform", key -> new RatQuarryPlatformBlock(RatsRegistryHelper.withBlockId(key, Block.Properties.of()).sound(SoundType.SLIME_BLOCK).noOcclusion().strength(1.0F, 0.0F)));

	public static DeferredHolder<Block, Block> register(String name, Function<Identifier, Block> blockFactory) {
		DeferredHolder<Block, Block> ret = BLOCKS.register(name, blockFactory);
		RatsItemRegistry.ITEMS.register(name, key -> new RatsBlockItem(ret.get(), RatsRegistryHelper.withItemId(key, new Item.Properties()).useBlockDescriptionPrefix()));
		return ret;
	}
}
