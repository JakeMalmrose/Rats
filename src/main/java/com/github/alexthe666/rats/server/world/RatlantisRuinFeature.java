package com.github.alexthe666.rats.server.world;

import com.github.alexthe666.rats.RatsMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RatlantisRuinFeature extends Feature<RatlantisRuinConfiguration> {

	// Loot tables to inject into otherwise-empty containers of specific ruins. Feature-placed
	// templates don't run the jigsaw loot pass, so chests would generate empty without this.
	private static final Map<String, ResourceKey<LootTable>> STRUCTURE_LOOT = Map.of(
			"plague_doctor_hut", ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(RatsMod.MODID, "chest/plague_doctor_hut"))
	);

	public RatlantisRuinFeature(Codec<RatlantisRuinConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<RatlantisRuinConfiguration> context) {
		RandomSource random = context.random();
		WorldGenLevel level = context.level();
		BlockPos blockpos = context.origin();
		Rotation rotation = Rotation.getRandom(random);
		RatlantisRuinConfiguration config = context.config();

		StructureTemplateManager structuretemplatemanager = level.getLevel().getServer().getStructureManager();
		StructureTemplate structuretemplate = null;
		Identifier chosenRuin = null;

		//shuffle the map. This makes it so the last entries are actually used more often
		List<Identifier> shuffledList = new ArrayList<>(config.ruins().keySet());
		Collections.shuffle(shuffledList);

		for (Identifier entry : shuffledList) {
			if (random.nextFloat() < config.ruins().get(entry)) {
				structuretemplate = structuretemplatemanager.getOrCreate(entry);
				chosenRuin = entry;
				break;
			}
		}
		if (structuretemplate == null) {
			chosenRuin = config.defaultRuin();
			structuretemplate = structuretemplatemanager.getOrCreate(chosenRuin);
		}

		ChunkPos chunkpos = ChunkPos.containing(blockpos);
		BoundingBox boundingbox = new BoundingBox(chunkpos.getMinBlockX() - 16, level.getMinY(), chunkpos.getMinBlockZ() - 16, chunkpos.getMaxBlockX() + 16, level.getMaxY(), chunkpos.getMaxBlockZ() + 16);
		StructurePlaceSettings structureplacesettings = new StructurePlaceSettings().setRotation(rotation).setBoundingBox(boundingbox).setRandom(random);
		Vec3i vec3i = structuretemplate.getSize(rotation);
		BlockPos blockpos1 = blockpos.offset(-vec3i.getX() / 2, 0, -vec3i.getZ() / 2);
		BlockPos blockpos2 = structuretemplate.getZeroPositionWithTransform(blockpos1, Mirror.NONE, rotation);

		if (!level.getBlockState(blockpos2.below()).isSolidRender()) return false;

		structureplacesettings.clearProcessors();
		if (config.processor() != null) {
			config.processor().value().list().forEach(structureplacesettings::addProcessor);
		}

		boolean placed = structuretemplate.placeInWorld(level, blockpos2, blockpos2, structureplacesettings, random, 20);
		if (placed && level.getLevel() instanceof ServerLevel serverLevel) {
			ResourceKey<LootTable> lootTable = chosenRuin != null ? STRUCTURE_LOOT.get(chosenRuin.getPath()) : null;
			if (lootTable != null) {
				this.injectLootTables(serverLevel, structuretemplate, blockpos2, rotation, lootTable, random);
			}
			this.spawnStructureEntities(serverLevel, structuretemplate, blockpos2, rotation, random);
		}
		return placed;
	}

	private void injectLootTables(ServerLevel level, StructureTemplate template, BlockPos origin, Rotation rotation, ResourceKey<LootTable> lootTable, RandomSource random) {
		Vec3i size = template.getSize(rotation);
		BlockPos end = origin.offset(size.getX(), size.getY(), size.getZ());

		for (BlockPos pos : BlockPos.betweenClosed(origin, end)) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof RandomizableContainerBlockEntity container) {
				CompoundTag tag = container.saveWithoutMetadata(level.registryAccess());
				if (!tag.contains("LootTable")) {
					container.setLootTable(lootTable, random.nextLong());
					container.setChanged();
				}
			}
		}
	}

	// Feature placement skips a template's stored entities (placeInWorld only spawns them when the
	// settings keep entity info, which the worldgen path drops), so armor stands and the like in the
	// ruin NBT files would silently vanish. Re-read the template's raw entity list and spawn them.
	private void spawnStructureEntities(ServerLevel level, StructureTemplate template, BlockPos origin, Rotation rotation, RandomSource random) {
		StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation);
		try {
			Field field = StructureTemplate.class.getDeclaredField("entityInfoList");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<StructureTemplate.StructureEntityInfo> rawList = (List<StructureTemplate.StructureEntityInfo>) field.get(template);
			if (rawList == null || rawList.isEmpty()) return;

			for (StructureTemplate.StructureEntityInfo entityInfo : StructureTemplate.processEntityInfos(template, level, origin, settings, rawList)) {
				CompoundTag nbt = entityInfo.nbt.copy();
				nbt.remove("UUID");
				// 26.1: EntityType.by/Entity#load read from ValueInput now; wrap the template's CompoundTag.
				ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), nbt);
				EntityType.by(input).ifPresent(type -> {
					Entity entity = type.create(level, EntitySpawnReason.STRUCTURE);
					if (entity != null) {
						entity.load(input);
						entity.snapTo(entityInfo.pos.x, entityInfo.pos.y, entityInfo.pos.z, entity.getYRot(), entity.getXRot());
						level.addFreshEntity(entity);
					}
				});
			}
		} catch (Exception exception) {
			RatsMod.LOGGER.warn("Failed to spawn structure entities: {}", exception.getMessage());
		}
	}
}
