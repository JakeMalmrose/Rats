package com.github.alexthe666.rats.server.entity.mount;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.AbstractRat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.chicken.ChickenSoundVariant;
import net.minecraft.world.entity.animal.chicken.ChickenSoundVariants;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RatChickenMount extends RatMountBase {

	public float wingRotation;
	public float destPos;
	public float oFlapSpeed;
	public float oFlap;
	public float wingRotDelta = 1.0F;

	public RatChickenMount(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.riderY = 0.55F;
		this.riderXZ = 0.1F;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 4.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.STEP_HEIGHT, 1.0D);
	}

	@Override
	public boolean doHurtTarget(ServerLevel level, Entity entity) {
		return entity.hurtServer(level, this.damageSources().mobAttack(this), (float) (1 + this.getRandom().nextInt(2)));
	}

	public void aiStep() {
		super.aiStep();
		this.oFlap = this.wingRotation;
		this.oFlapSpeed = this.destPos;
		this.destPos = (float) ((double) this.destPos + (double) (this.onGround() ? -1 : 4) * 0.3D);
		this.destPos = Mth.clamp(this.destPos, 0.0F, 1.0F);
		if (!this.onGround() && this.wingRotDelta < 1.0F) {
			this.wingRotDelta = 1.0F;
		}

		this.wingRotDelta = (float) ((double) this.wingRotDelta * 0.9D);
		Vec3 vec3d = this.getDeltaMovement();
		if (!this.onGround() && vec3d.y < 0.0D) {
			this.setDeltaMovement(vec3d.multiply(1.0D, 0.6D, 1.0D));
		}

		this.wingRotation += this.wingRotDelta * 2.0F;
	}

	// 26.1: chicken sounds moved into ChickenSoundVariant sets; use the classic adult set.
	private static ChickenSoundVariant.ChickenSoundSet chickenSounds() {
		return SoundEvents.CHICKEN_SOUNDS.get(ChickenSoundVariants.SoundSet.CLASSIC).adultSounds();
	}

	protected SoundEvent getAmbientSound() {
		return chickenSounds().ambientSound().value();
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return chickenSounds().hurtSound().value();
	}

	protected SoundEvent getDeathSound() {
		return chickenSounds().deathSound().value();
	}

	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.CHICKEN_STEP.value(), 0.15F, 1.0F);
	}

	@Override
	public Item getUpgradeItem() {
		return RatsItemRegistry.RAT_UPGRADE_CHICKEN_MOUNT.get();
	}

	@Override
	public void adjustRatTailRotation(AbstractRat rat, AdvancedModelBox upperTail, AdvancedModelBox lowerTail) {
		this.progressRotation(upperTail, rat.sitProgress, 1.0F, 0.0F, 0.0F, 20.0F);
		this.progressRotation(lowerTail, rat.sitProgress, -0.1F, 0.0F, 0.0F, 20.0F);
	}
}
