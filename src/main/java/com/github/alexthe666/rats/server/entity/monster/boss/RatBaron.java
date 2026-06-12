package com.github.alexthe666.rats.server.entity.monster.boss;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.github.alexthe666.rats.registry.RatlantisBlockRegistry;
import com.github.alexthe666.rats.registry.RatlantisEntityRegistry;
import com.github.alexthe666.rats.registry.RatlantisItemRegistry;
import com.github.alexthe666.rats.registry.RatsSoundRegistry;
import com.github.alexthe666.rats.server.entity.rat.AbstractRat;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class RatBaron extends AbstractRat implements Enemy {

	private final ServerBossEvent bossInfo = new ServerBossEvent(net.minecraft.util.Mth.createInsecureUUID(this.random), this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

	public RatBaron(EntityType<? extends AbstractRat> type, Level level) {
		super(type, level);
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true) {
			@Override
			protected AABB getTargetSearchArea(double dist) {
				return this.mob.getBoundingBox().inflate(dist, 128.0D, dist);
			}
		});
	}

	// 1.21: passenger Y offset is sourced from the carrier's EntityAttachments.PASSENGER. The
	// RatBaronPlane already handles its own passenger positioning, so no override is needed.

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 300.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 7.0D)
				.add(Attributes.FOLLOW_RANGE, 64.0D);
	}

	// 26.1: vanilla Mob now persists the home position/radius itself ("home_pos"/"home_radius"), so the custom "Home" list is gone.
	@Override
	public void readAdditionalSaveData(ValueInput tag) {
		super.readAdditionalSaveData(tag);
		if (this.hasCustomName()) {
			this.bossInfo.setName(this.getDisplayName());
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return RatsSoundRegistry.RAT_HURT.get();
	}

	// 26.1: shouldDespawnInPeaceful() was removed (now an EntityType flag); the checkDespawn override below handles peaceful.

	@Override
	public boolean removeWhenFarAway(double dist) {
		return false;
	}

	@Override
	public void checkDespawn() {
		if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
			if (this.hasHome()) {
				this.level().setBlockAndUpdate(this.getHomePosition(), RatlantisBlockRegistry.AIR_RAID_SIREN.get().defaultBlockState());
			}
			this.discard();
		} else {
			super.checkDespawn();
		}
	}

	@Override
	public void setCustomName(@Nullable Component name) {
		super.setCustomName(name);
		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, EntitySpawnReason type, @Nullable SpawnGroupData data) {
		data = super.finalizeSpawn(accessor, difficulty, type, data);
		this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(RatlantisItemRegistry.AVIATOR_HAT.get()));
		this.setGuaranteedDrop(EquipmentSlot.HEAD);
		if (type != EntitySpawnReason.MOB_SUMMONED) {
			this.setHomeTo(this.blockPosition(), 16);
		}
		if (!this.isPassenger()) {
			RatBaronPlane plane = new RatBaronPlane(RatlantisEntityRegistry.RAT_BARON_PLANE.get(), this.level());
			plane.copyPosition(this);
			plane.setHomeTo(this.blockPosition(), 16);
			if (!this.level().isClientSide()) {
				this.level().addFreshEntity(plane);
			}
			this.startRiding(plane, true, true);
		}
		return data;
	}

	@Override
	public boolean canTeleport(net.minecraft.world.level.Level from, net.minecraft.world.level.Level to) {
		return false;
	}

	@Override
	public boolean isVisuallySitting() {
		return true;
	}
}
