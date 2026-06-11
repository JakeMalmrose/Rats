package com.github.alexthe666.rats.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RatsBlockEntityRegistry {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, RatsMod.MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RatHoleBlockEntity>> RAT_HOLE = BLOCK_ENTITIES.register("rat_hole", () -> new BlockEntityType<>(RatHoleBlockEntity::new, RatsBlockRegistry.RAT_HOLE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RatTrapBlockEntity>> RAT_TRAP = BLOCK_ENTITIES.register("rat_trap", () -> new BlockEntityType<>(RatTrapBlockEntity::new, RatsBlockRegistry.RAT_TRAP.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MilkCauldronBlockEntity>> MILK_CAULDRON = BLOCK_ENTITIES.register("milk_cauldron", () -> new BlockEntityType<>(MilkCauldronBlockEntity::new, RatsBlockRegistry.MILK_CAULDRON.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecoratedRatCageBlockEntity>> RAT_CAGE_DECORATED = BLOCK_ENTITIES.register("rat_cage_decorated", () -> new BlockEntityType<>(DecoratedRatCageBlockEntity::new, RatsBlockRegistry.RAT_CAGE_DECORATED.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RatCageBreedingLanternBlockEntity>> RAT_CAGE_BREEDING_LANTERN = BLOCK_ENTITIES.register("rat_cage_breeding_lantern", () -> new BlockEntityType<>(RatCageBreedingLanternBlockEntity::new, RatsBlockRegistry.RAT_CAGE_BREEDING_LANTERN.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RatCraftingTableBlockEntity>> RAT_CRAFTING_TABLE = BLOCK_ENTITIES.register("rat_crafting_table", () -> new BlockEntityType<>(RatCraftingTableBlockEntity::new, RatsBlockRegistry.RAT_CRAFTING_TABLE.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RatTubeBlockEntity>> RAT_TUBE = BLOCK_ENTITIES.register("rat_tube", () -> new BlockEntityType<>(RatTubeBlockEntity::new, RatsBlockRegistry.RAT_TUBE_COLOR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UpgradeSeparatorBlockEntity>> UPGRADE_SEPERATOR = BLOCK_ENTITIES.register("upgrade_separator", () -> new BlockEntityType<>(UpgradeSeparatorBlockEntity::new, RatsBlockRegistry.UPGRADE_SEPARATOR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UpgradeCombinerBlockEntity>> UPGRADE_COMBINER = BLOCK_ENTITIES.register("upgrade_combiner", () -> new BlockEntityType<>(UpgradeCombinerBlockEntity::new, RatsBlockRegistry.UPGRADE_COMBINER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoCurdlerBlockEntity>> AUTO_CURDLER = BLOCK_ENTITIES.register("auto_curdler", () -> new BlockEntityType<>(AutoCurdlerBlockEntity::new, RatsBlockRegistry.AUTO_CURDLER.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrashCanBlockEntity>> TRASH_CAN = BLOCK_ENTITIES.register("trash_can", () -> new BlockEntityType<>(TrashCanBlockEntity::new, RatsBlockRegistry.TRASH_CAN.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RatAttractorBlockEntity>> RAT_ATTRACTOR = BLOCK_ENTITIES.register("rat_attractor", () -> new BlockEntityType<>(RatAttractorBlockEntity::new, RatsBlockRegistry.RAT_ATTRACTOR.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RatQuarryBlockEntity>> RAT_QUARRY = BLOCK_ENTITIES.register("rat_quarry", () -> new BlockEntityType<>(RatQuarryBlockEntity::new, RatsBlockRegistry.RAT_QUARRY.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RatCageWheelBlockEntity>> RAT_CAGE_WHEEL = BLOCK_ENTITIES.register("rat_cage_wheel", () -> new BlockEntityType<>(RatCageWheelBlockEntity::new, RatsBlockRegistry.RAT_CAGE_WHEEL.get()));
}
