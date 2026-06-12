package com.github.alexthe666.rats.server.entity.ai.goal;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import com.github.alexthe666.rats.server.entity.monster.boss.BlackDeath;
import com.github.alexthe666.rats.server.entity.rat.Rat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.neoforged.neoforge.event.EventHooks;

public class BlackDeathSummonRatGoal extends BlackDeathAbstractSummonGoal {
	public BlackDeathSummonRatGoal(BlackDeath death) {
		super(death);
	}

	@Override
	public int getAttackCooldown() {
		return 40;
	}

	@Override
	public void summonEntity() {
		this.death.level().broadcastEntityEvent(this.death, (byte) 82);

		Rat rat = new Rat(RatsEntityRegistry.RAT.get(), this.death.level());
		ServerLevel serverLevel = (ServerLevel) this.death.level();
		EventHooks.finalizeMobSpawn(rat, serverLevel, serverLevel.getCurrentDifficultyAt(this.death.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
		rat.copyPosition(this.death);
		rat.setPlagued(true);
		this.death.level().addFreshEntity(rat);
		rat.setOwner(this.death); //26.1: TamableAnimal.setOwnerUUID was replaced by entity-reference setters
		if (this.death.getTarget() != null) {
			rat.setTarget(this.death.getTarget());
		}
		this.death.setRatsSummoned(this.death.getRatsSummoned() + 1);
	}

	@Override
	public boolean hasSummonedEnough() {
		return this.death.getRatsSummoned() >= RatConfig.bdMaxRatSpawns;
	}
}
