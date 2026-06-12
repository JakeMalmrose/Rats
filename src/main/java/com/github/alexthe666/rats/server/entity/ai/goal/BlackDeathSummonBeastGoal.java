package com.github.alexthe666.rats.server.entity.ai.goal;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import com.github.alexthe666.rats.server.entity.monster.PlagueBeast;
import com.github.alexthe666.rats.server.entity.monster.boss.BlackDeath;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.neoforged.neoforge.event.EventHooks;

public class BlackDeathSummonBeastGoal extends BlackDeathAbstractSummonGoal {
	public BlackDeathSummonBeastGoal(BlackDeath death) {
		super(death);
	}

	@Override
	public boolean canUse() {
		if (this.death.getRatsSummoned() >= 15 && this.death.getCloudsSummoned() > 1) {
			return super.canUse();
		}
		return false;
	}

	@Override
	public int getAttackCooldown() {
		return 200;
	}

	@Override
	public void summonEntity() {
		PlagueBeast beast = new PlagueBeast(RatsEntityRegistry.PLAGUE_BEAST.get(), this.death.level());
		ServerLevel serverLevel = (ServerLevel) this.death.level();
		EventHooks.finalizeMobSpawn(beast, serverLevel, serverLevel.getCurrentDifficultyAt(this.death.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
		beast.copyPosition(this.death);
		this.death.level().addFreshEntity(beast);
		beast.setOwnerId(this.death.getUUID());
		if (this.death.getTarget() != null) {
			beast.setTarget(this.death.getTarget());
		}
		this.death.setBeastsSummoned(this.death.getBeastsSummoned() + 1);
	}

	@Override
	public boolean hasSummonedEnough() {
		return this.death.getBeastsSummoned() >= RatConfig.bdMaxBeastSpawns;
	}
}
