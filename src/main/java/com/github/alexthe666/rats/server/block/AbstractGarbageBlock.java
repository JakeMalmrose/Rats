package com.github.alexthe666.rats.server.block;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.monster.PiedPiper;
import com.github.alexthe666.rats.server.entity.rat.AbstractRat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public abstract class AbstractGarbageBlock extends FallingBlock {

	public final EntitySpawnReason spawnReason;
	public final double spawnRateModifier;

	public AbstractGarbageBlock(BlockBehaviour.Properties properties, double spawnRateModifier) {
		super(properties);
		this.spawnReason = EntitySpawnReason.SPAWNER;
		this.spawnRateModifier = spawnRateModifier;
	}

	protected abstract EntityType<? extends PathfinderMob> getEntityToSpawn();

	protected void postInitSpawn(PathfinderMob mob, RandomSource random) {

	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (random.nextInt(level.getGameRules().get(GameRules.RANDOM_TICK_SPEED)) <= 3) {
			if (!level.getBlockState(pos.above()).isSuffocating(level, pos) && level.getGameRules().get(GameRules.SPAWN_MOBS)) {
				if (random.nextFloat() <= RatConfig.garbageSpawnRate * spawnRateModifier) {
					PathfinderMob mob = this.getEntityToSpawn().create(level, this.spawnReason);
					if (mob == null) return;
					mob.snapTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0F, 0.0F);
					if (!mob.checkSpawnRules(level, this.spawnReason)) return;
					if (!mob.isInWall() && mob.checkSpawnObstruction(level)) {
						if (mob instanceof AbstractRat) {
							if (!level.getGameRules().get(RatsMod.SPAWN_RATS)) return;
							if (Objects.requireNonNull(level.getChunkSource().getLastSpawnState()).getMobCategoryCounts().getInt(RatsMod.RATS) >= RatsMod.RATS.getMaxInstancesPerChunk() * 2)
								return;
							if (RatConfig.ratsSpawnLikeMonsters && !this.isDarkEnoughForMonsterSpawns(level, mob.blockPosition(), random))
								return;
							EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(pos), this.spawnReason, null);
							this.postInitSpawn(mob, random);
							level.tryAddFreshEntityWithPassengers(mob);
						} else {
							if (mob instanceof PiedPiper && !level.getGameRules().get(RatsMod.SPAWN_PIPERS))
								return;
							if (this.isDarkEnoughForMonsterSpawns(level, mob.blockPosition(), random)) {
								EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(pos), this.spawnReason, null);
								this.postInitSpawn(mob, random);
								level.tryAddFreshEntityWithPassengers(mob);
							}
						}
					}
				}
			}
		}
	}

	//copy of Monster.isDarkEnoughToSpawn but without that strange sky check thing
	private boolean isDarkEnoughForMonsterSpawns(ServerLevelAccessor accessor, BlockPos pos, RandomSource random) {
		DimensionType dimensiontype = accessor.dimensionType();
		int i = dimensiontype.monsterSpawnBlockLightLimit();
		if (i < 15 && accessor.getBrightness(LightLayer.BLOCK, pos) > i) {
			return false;
		} else {
			int j = accessor.getLevel().isThundering() ? accessor.getMaxLocalRawBrightness(pos, 10) : accessor.getMaxLocalRawBrightness(pos);
			return j <= dimensiontype.monsterSpawnLightTest().sample(random);
		}
	}

	// 26.1: Block.appendHoverText no longer exists; the block item must delegate here (see RatsBlockItem in items/).
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
	}

	// 1.21: BlockBehaviour.isValidSpawn is no longer overridable on the block class. Spawn validity
	// is configured via Properties.isValidSpawn(...) at registration time. This helper stays as a
	// public predicate for callers that need to ask "would this entity spawn on top of garbage?".
	public boolean canMobSpawnHere(EntityType<?> entityType) {
		return entityType == this.getEntityToSpawn();
	}
}