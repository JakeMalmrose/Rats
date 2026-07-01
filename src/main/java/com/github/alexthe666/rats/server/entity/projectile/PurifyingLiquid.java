package com.github.alexthe666.rats.server.entity.projectile;

import com.github.alexthe666.rats.data.tags.RatsEntityTags;
import com.github.alexthe666.rats.registry.RatsEffectRegistry;
import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.DemonRat;
import com.github.alexthe666.rats.server.entity.rat.Rat;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.EventHooks;

import java.util.List;
import net.minecraft.network.syncher.SynchedEntityData;

public class PurifyingLiquid extends ThrowableItemProjectile {

	private static final EntityDataAccessor<Boolean> NETHER = SynchedEntityData.defineId(PurifyingLiquid.class, EntityDataSerializers.BOOLEAN);

	public PurifyingLiquid(EntityType<? extends ThrowableItemProjectile> type, Level level) {
		super(type, level);
	}

	public PurifyingLiquid(Level level, LivingEntity thrower, boolean nether) {
		// 26.1: the owner-based ThrowableItemProjectile constructor now also takes the rendered ItemStack.
		super(RatsEntityRegistry.PURIFYING_LIQUID.get(), thrower, level, new ItemStack(nether ? RatsItemRegistry.CRIMSON_FLUID.get() : RatsItemRegistry.PURIFYING_LIQUID.get()));
		this.getEntityData().set(NETHER, nether);
	}

	public PurifyingLiquid(Level level, double x, double y, double z, boolean nether) {
		super(RatsEntityRegistry.PURIFYING_LIQUID.get(), x, y, z, level, new ItemStack(nether ? RatsItemRegistry.CRIMSON_FLUID.get() : RatsItemRegistry.PURIFYING_LIQUID.get()));
		this.getEntityData().set(NETHER, nether);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(NETHER, false);
	}

	@Override
	protected void onHit(HitResult result) {
		if (this.level() instanceof ServerLevel serverLevel) {
			AABB aabb = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
			List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
			if (!list.isEmpty()) {
				for (LivingEntity living : list) {
					if (living.isAffectedByPotions()) {
						double d0 = this.distanceToSqr(living);
						if (d0 < 16.0D) {
							if (this.getEntityData().get(NETHER)) {
								if (living instanceof DemonRat) {
									Rat rat = new Rat(RatsEntityRegistry.RAT.get(), this.level());
									rat.copyPosition(living);
									EventHooks.finalizeMobSpawn(rat, serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), EntitySpawnReason.CONVERSION, null);
									rat.setTame(false, true);
									// 26.1: owner UUIDs are stored as EntityReferences now.
									rat.setOwner(null);
									this.level().addFreshEntity(rat);
									living.discard();
								}
							} else {
								if (living instanceof Rat rat && rat.hasPlague()) {
									rat.setPlagued(false);
								}
								if (living.hasEffect(RatsEffectRegistry.PLAGUE)) {
									living.removeEffect(RatsEffectRegistry.PLAGUE);
								}
								if (living.is(RatsEntityTags.PLAGUE_LEGION)) {
									living.hurtServer(serverLevel, this.damageSources().magic(), 10);
								}
								if (living instanceof ZombieVillager zomb && !zomb.isConverting()) {
									// 26.1 port: ZombieVillager#startConverting is private now; round-trip the zombie's
									// save data with ConversionTime set, which starts the conversion on load.
									TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, zomb.registryAccess());
									zomb.saveWithoutId(output);
									CompoundTag tag = output.buildResult();
									tag.putInt("ConversionTime", 200);
									if (this.getOwner() != null) {
										tag.store("ConversionPlayer", UUIDUtil.CODEC, this.getOwner().getUUID());
									}
									zomb.load(TagValueInput.create(ProblemReporter.DISCARDING, zomb.registryAccess(), tag));
								}
							}
						}
					}
				}
			}
			this.level().levelEvent(2002, this.blockPosition(), this.getEntityData().get(NETHER) ? 0XAD141E : 0XBFDFE2);
			this.discard();
		}
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(this.getDefaultItem());
	}

	@Override
	protected Item getDefaultItem() {
		try {
			return this.getEntityData().get(NETHER) ? RatsItemRegistry.CRIMSON_FLUID.get() : RatsItemRegistry.PURIFYING_LIQUID.get();
		} catch (Exception ignored) {
			return RatsItemRegistry.PURIFYING_LIQUID.get();
		}
	}
}
