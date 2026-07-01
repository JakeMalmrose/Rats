package com.github.alexthe666.rats.server.block;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.registry.RatlantisEntityRegistry;
import com.github.alexthe666.rats.registry.RatsSoundRegistry;
import com.github.alexthe666.rats.registry.worldgen.RatlantisDimensionRegistry;
import com.github.alexthe666.rats.server.entity.monster.boss.RatBaron;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class AirRaidSirenBlock extends Block implements CustomItemRarity {

	private static final VoxelShape AABB = Block.box(6, 0, 6, 10, 16, 10);

	public AirRaidSirenBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public Rarity getRarity() {
		return Rarity.RARE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		return this.spawnTheBaron(level, pos) ? InteractionResult.SUCCESS : InteractionResult.PASS;
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
		boolean flag = level.hasNeighborSignal(pos);
		if (flag) {
			this.spawnTheBaron(level, pos);
		}
	}

	private boolean spawnTheBaron(Level level, BlockPos pos) {
		// 26.1: getCurrentDifficultyAt now only exists on ServerLevelAccessor.
		if (!(level instanceof ServerLevel serverLevel)) return true;
		if (serverLevel.getCurrentDifficultyAt(pos).getDifficulty() != Difficulty.PEACEFUL) {
			level.playSound(null, pos, RatsSoundRegistry.AIR_RAID_SIREN.get(), SoundSource.BLOCKS, 1, 1);

			if (RatConfig.summonBaronOnlyInRatlantis && !level.dimension().equals(RatlantisDimensionRegistry.DIMENSION_KEY)) {
				for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(16.0D))) {
					// 26.1: displayClientMessage was removed; send an overlay message from the server instead.
					player.sendSystemMessage(Component.translatable(RatsLangConstants.BARON_RATLANTIS_ONLY), true);
				}
				return true;
			}
			LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
			assert bolt != null;
			bolt.setPos(Vec3.atCenterOf(pos));
			bolt.setVisualOnly(true);
			level.addFreshEntity(bolt);
			level.setBlockAndUpdate(pos, Blocks.OAK_FENCE.defaultBlockState());
			RatBaron baron = new RatBaron(RatlantisEntityRegistry.RAT_BARON.get(), level);
			baron.setPos(pos.getX() + 0.5D, pos.getY() + 5D, pos.getZ() + 0.5D);
			EventHooks.finalizeMobSpawn(baron, serverLevel, serverLevel.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
			// 26.1: Mob.restrictTo was renamed to setHomeTo.
			baron.setHomeTo(pos, 16);

			// 26.1: getGameRules is ServerLevel-only.
			if (serverLevel.getGameRules().get(GameRules.BLOCK_DROPS)) {
				for (int i = 0; i < 2; i++) {
					RandomSource rand = level.getRandom();
					level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D + (rand.nextFloat() - 0.5D) * 3, pos.getY() - 1, pos.getZ() + 0.5D + (rand.nextFloat() - 0.5D) * 3, new ItemStack(Items.IRON_INGOT)));
				}
			}
			return level.addFreshEntity(baron);
		}
		return false;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return false;
	}
}
