package com.github.alexthe666.rats.server.entity.ai.navigation.evaluator;

import com.github.alexthe666.rats.server.block.*;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.misc.RatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class RatNodeEvaluator extends WalkNodeEvaluator {

	@Override
	public PathType getPathTypeOfMob(net.minecraft.world.level.pathfinder.PathfindingContext context, int x, int y, int z, net.minecraft.world.entity.Mob mob) {
		PathType types = super.getPathTypeOfMob(context, x, y, z, mob);
		BlockPos pos = new BlockPos(x, y, z);
		BlockGetter getter = context.level();
		Block block = getter.getBlockState(pos).getBlock();
		if (mob instanceof TamedRat rat) {
			// Tube rework: ALL tube blocks are walkable for tamed rats, not just open ends. The block's
			// getBlockPathType hook can't help — 26.1 pathfinding evaluates it with a null mob, so it
			// always reported BLOCKED (this is why tubes never pathed). Ground A* can now route through
			// horizontal tube runs and reach entrances; vertical runs are RatTubeNavigation's job.
			if (block instanceof RatHoleBlock || block instanceof RatTrapBlock || block instanceof RatCageBlock || block instanceof RatTubeBlock) {
				types = PathType.WALKABLE;
			}
			if (block instanceof RatQuarryPlatformBlock) {
				types = PathType.OPEN;
			}
			if (block instanceof SlabBlock) {
				types = PathType.WALKABLE;
			}
			if (types == PathType.RAIL && !(block instanceof BaseRailBlock) && !(getter.getBlockState(pos.below()).getBlock() instanceof BaseRailBlock)) {
				types = PathType.UNPASSABLE_RAIL;
			}
			if (rat.isInCage()) {
				if (block instanceof RatCageBlock || block instanceof RatTubeBlock) {
					types = PathType.WALKABLE;
				} else {
					types = PathType.BLOCKED;
				}
			}
		}
		return types;
	}
}