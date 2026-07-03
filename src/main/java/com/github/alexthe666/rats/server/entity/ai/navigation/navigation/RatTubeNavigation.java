package com.github.alexthe666.rats.server.entity.ai.navigation.navigation;

import com.github.alexthe666.rats.server.block.RatTubeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * In-tube navigation. Vanilla A* cannot traverse tube networks (it has no straight-vertical moves,
 * and tube interiors confuse its floor/collision model), which is why tubes never really worked —
 * the mod's original custom tube A* has been commented out since ~1.16. This navigation walks the
 * tube graph directly: a BFS over connected tube blocks that either reaches the target inside the
 * network (e.g. a connected cage) or exits at the open end nearest to the target and hands control
 * back to ground navigation once the rat pops out.
 */
public class RatTubeNavigation extends RatNavigation {

	private static final int MAX_VISITED_TUBES = 2048;

	public RatTubeNavigation(Mob mob, Level level) {
		super(mob, level);
	}

	@Override
	protected boolean canUpdatePath() {
		// tube rats float (noGravity) through the pipe, so the onGround gate must not apply
		return true;
	}

	@Override
	protected Vec3 getTempMobPos() {
		return this.mob.position();
	}

	@Override
	@Nullable
	protected Path createPath(Set<BlockPos> targets, int radiusOffset, boolean above, int reachRange, float maxPathLength) {
		if (targets.isEmpty()) {
			return null;
		}
		BlockPos start = this.mob.blockPosition();
		if (!(this.level.getBlockState(start).getBlock() instanceof RatTubeBlock)) {
			// not actually inside a tube (nav switch race) — behave like ground navigation
			return super.createPath(targets, radiusOffset, above, reachRange, maxPathLength);
		}
		BlockPos target = targets.iterator().next();
		double reachSq = Math.max(1, reachRange) * (double) Math.max(1, reachRange);

		Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		cameFrom.put(start, start);
		queue.add(start);

		BlockPos reachedInNetwork = null;
		BlockPos bestExitOutside = null;
		double bestExitDistSq = Double.MAX_VALUE;
		int visited = 0;

		while (!queue.isEmpty() && visited++ < MAX_VISITED_TUBES) {
			BlockPos current = queue.poll();
			if (current.equals(target) || current.distSqr(target) <= reachSq) {
				reachedInNetwork = current;
				break;
			}
			BlockState state = this.level.getBlockState(current);
			if (!(state.getBlock() instanceof RatTubeBlock)) {
				// connected cage or an outside block reached through an open end — terminal node
				continue;
			}
			for (Direction direction : Direction.values()) {
				BlockPos next = current.relative(direction);
				if (state.getValue(RatTubeBlock.connectionProperty(direction))) {
					if (!cameFrom.containsKey(next)) {
						cameFrom.put(next, current);
						queue.add(next);
					}
				} else if (state.getValue(RatTubeBlock.openProperty(direction))) {
					double distSq = next.distSqr(target);
					if (distSq < bestExitDistSq) {
						bestExitDistSq = distSq;
						bestExitOutside = next;
						if (!cameFrom.containsKey(next)) {
							cameFrom.put(next, current);
						}
					}
				}
			}
		}

		BlockPos end;
		boolean reached;
		if (reachedInNetwork != null) {
			end = reachedInNetwork;
			reached = true;
		} else if (bestExitOutside != null) {
			end = bestExitOutside;
			reached = false;
		} else {
			// sealed network with no route — nothing sensible to do
			return null;
		}

		List<BlockPos> route = new ArrayList<>();
		for (BlockPos walk = end; !walk.equals(start); walk = cameFrom.get(walk)) {
			route.add(walk);
		}
		route.add(start);
		Collections.reverse(route);

		List<Node> nodes = new ArrayList<>(route.size());
		for (BlockPos pos : route) {
			nodes.add(new Node(pos.getX(), pos.getY(), pos.getZ()));
		}
		return new Path(nodes, end, reached);
	}
}
