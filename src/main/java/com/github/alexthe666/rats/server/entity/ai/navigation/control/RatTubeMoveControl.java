package com.github.alexthe666.rats.server.entity.ai.navigation.control;

import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

/**
 * 26.1 tube rework: rats travel tubes as gravity-free 3D movement toward the next path node
 * (RatTubeNavigation supplies node-by-node tube-center waypoints), the same steering model as the
 * ethereal move control. The old strafe/ladder-climb hybrid depended on the long-dead BE-driven
 * tube A* and never worked with vanilla pathfinding.
 */
public class RatTubeMoveControl extends RatMoveControl {
	private final TamedRat rat;

	public RatTubeMoveControl(TamedRat rat) {
		super(rat);
		this.rat = rat;
	}

	@Override
	public void tick() {
		if (this.operation == MoveControl.Operation.MOVE_TO && this.rat.canMove()) {
			double dx = this.getWantedX() - this.rat.getX();
			double dy = this.getWantedY() + 0.2D - this.rat.getY();
			double dz = this.getWantedZ() - this.rat.getZ();
			double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
			if (dist < 0.05D) {
				this.operation = MoveControl.Operation.WAIT;
				this.rat.setDeltaMovement(this.rat.getDeltaMovement().multiply(0.5D, 0.5D, 0.5D));
				this.rat.climbingTube = false;
				return;
			}
			double speed = this.speedModifier * this.rat.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.6D;
			this.rat.setDeltaMovement(this.rat.getDeltaMovement().scale(0.7D)
					.add(dx / dist * speed, dy / dist * speed, dz / dist * speed));
			float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
			this.rat.setYRot(this.rotlerp(this.rat.getYRot(), yaw, 90.0F));
			this.rat.yBodyRot = this.rat.getYRot();
			this.rat.climbingTube = dy > 0.05D;
		} else {
			this.rat.setZza(0.0F);
			this.rat.climbingTube = false;
		}
	}
}
