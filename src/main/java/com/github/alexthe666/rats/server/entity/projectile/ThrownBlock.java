package com.github.alexthe666.rats.server.entity.projectile;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ThrownBlock extends Entity {
	public LivingEntity shootingEntity;
	private static final EntityDataAccessor<Optional<BlockState>> CARRIED_BLOCK = SynchedEntityData.defineId(ThrownBlock.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
	public boolean dropBlock = true;
	public CompoundTag tileEntityData;
	private int ticksAlive;
	private int ticksInAir;

	public ThrownBlock(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	public ThrownBlock(EntityType<? extends Entity> type, Level level, BlockState blockState, LivingEntity entityNeoRatlantean) {
		super(type, level);
		this.setHeldBlockState(blockState);
		this.shootingEntity = entityNeoRatlantean;
	}

	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(CARRIED_BLOCK, Optional.empty());
	}

	public void setHeldBlockState(@Nullable BlockState state) {
		this.getEntityData().set(CARRIED_BLOCK, Optional.ofNullable(state));
	}

	@Nullable
	public BlockState getHeldBlockState() {
		return this.getEntityData().get(CARRIED_BLOCK).orElse(null);
	}

	public boolean shouldRenderAtSqrDistance(double distance) {
		double d0 = this.getBoundingBox().getSize() * 4.0D;

		if (Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		// 26.1: canBeCollidedWith takes the colliding entity as a parameter.
		if (!entity.isSpectator() && entity.isAlive() && entity.canBeCollidedWith(this)) {
			Entity shooter = this.shootingEntity;
			return shooter == null || !entity.isPassengerOfSameVehicle(entity);
		} else {
			return false;
		}
	}

	public void tick() {
		if (this.level().isClientSide() || (this.shootingEntity == null || this.shootingEntity.isAlive()) && this.level().isLoaded(this.blockPosition())) {
			super.tick();

			++this.ticksInAir;
			if (ticksInAir > 25) {
				this.noPhysics = true;
				HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canCollideWith);
				this.onHit(raytraceresult);
			} else {
				this.noPhysics = false;
			}
			this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
			ProjectileUtil.rotateTowardsMovement(this, 0.2F);
			if (this.isInWater()) {
				for (int i = 0; i < 4; ++i) {
					this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - this.getDeltaMovement().x * 0.25D, this.getY() - this.getDeltaMovement().y * 0.25D, this.getZ() - this.getDeltaMovement().z * 0.25D, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
				}
			}
			if (this.shootingEntity != null && shootingEntity instanceof Mob mob) {
				if (mob.getTarget() != null) {
					LivingEntity target = mob.getTarget();
					double d0 = target.getX() - this.getX();
					double d1 = target.getY() - this.getY();
					double d2 = target.getZ() - this.getZ();
					double d3 = d0 * d0 + d1 * d1 + d2 * d2;
					d3 = Mth.sqrt((float) d3);
					Vec3 vec3d = this.getDeltaMovement();
					vec3d = vec3d.add(d0 / d3 * 0.2D, d1 / d3 * 0.2D, d2 / d3 * 0.2D);
					this.setDeltaMovement(vec3d);
				}
			}
		} else {
			this.discard();
		}
		this.move(MoverType.SELF, this.getDeltaMovement());
	}

	protected void onHit(HitResult result) {
		if (this.getHeldBlockState() != null) {
			Block block = this.getHeldBlockState().getBlock();
			BlockPos pos = null;
			if (result instanceof BlockHitResult blockResult) {
				pos = blockResult.getBlockPos();
			}
			if (result instanceof EntityHitResult entityResult) {
				pos = entityResult.getEntity().blockPosition();
			}
			if (pos != null) {
				for (Entity hitMobs : this.level().getEntities(this, this.getBoundingBox().inflate(1.0F, 1.0F, 1.0F))) {
					hitMobs.hurt(this.damageSources().inWall(), 8.0F);
				}
				BlockPos blockpos1 = pos.above();

				if (this.dropBlock) {
					this.level().setBlockAndUpdate(blockpos1, this.getHeldBlockState());
				}
				if (this.tileEntityData != null && this.getHeldBlockState().hasBlockEntity()) {
					BlockEntity tileentity = this.level().getBlockEntity(blockpos1);

					if (tileentity != null) {
						// 1.21: BlockEntity.saveWithoutMetadata now requires HolderLookup.Provider.
						CompoundTag CompoundTag = tileentity.saveWithoutMetadata(this.level().registryAccess());

						for (String s : this.tileEntityData.keySet()) {
							Tag nbtbase = this.tileEntityData.get(s);

							if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
								CompoundTag.put(s, nbtbase.copy());
							}
						}
						tileentity.setChanged();
					}
				}
				this.discard();

				// 26.1: doMobLoot game rule is now GameRules.MOB_DROPS.
				if (this.level().getGameRules().get(GameRules.MOB_DROPS)) {
					if (this.level() instanceof ServerLevel serverLevel && this.dropBlock) {
						this.spawnAtLocation(serverLevel, new ItemStack(block, 1), 0.0F);
					}
					this.discard();
				}
			}
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void addAdditionalSaveData(ValueOutput compound) {
		// 26.1: ValueOutput stores structured data through codecs instead of raw NBT tags.
		compound.store("direction", Vec3.CODEC, this.getDeltaMovement());
		compound.putInt("life", this.ticksAlive);
		compound.storeNullable("carriedBlockState", BlockState.CODEC, this.getHeldBlockState());
		compound.storeNullable("TileEntityData", CompoundTag.CODEC, this.tileEntityData);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditionalSaveData(ValueInput compound) {
		this.ticksAlive = compound.getIntOr("life", 0);

		Optional<Vec3> direction = compound.read("direction", Vec3.CODEC);
		if (direction.isPresent()) {
			this.setDeltaMovement(direction.get());
		} else {
			this.discard();
		}

		BlockState blockstate = compound.read("carriedBlockState", BlockState.CODEC).orElse(null);
		if (blockstate != null && !blockstate.isAir()) {
			this.setHeldBlockState(blockstate);
		}
		this.tileEntityData = compound.read("TileEntityData", CompoundTag.CODEC).orElse(null);
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean canBeCollidedWith(@Nullable Entity other) {
		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	// 26.1: Entity#hurt is final; damage handling moved to the abstract hurtServer.
	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		if (this.isInvulnerableToBase(source)) {
			return false;
		} else {
			this.markHurt();

			if (source.getEntity() != null) {
				Vec3 vec3d = source.getEntity().getLookAngle();
				this.setDeltaMovement(vec3d);
				if (source.getEntity() instanceof LivingEntity living) {
					this.shootingEntity = living;
				}

				return true;
			} else {
				return false;
			}
		}
	}
}