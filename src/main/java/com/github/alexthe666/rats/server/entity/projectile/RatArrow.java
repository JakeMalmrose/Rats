package com.github.alexthe666.rats.server.entity.projectile;

import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.RatCommand;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

public class RatArrow extends AbstractArrow {

	private final ItemStack stack;

	public RatArrow(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
		this.stack = new ItemStack(RatsItemRegistry.RAT_ARROW.get());
	}

	public RatArrow(EntityType<? extends AbstractArrow> type, Level level, LivingEntity shooter, ItemStack stack) {
		// 1.21: AbstractArrow ctor takes (type, shooter, level, pickup, weapon).
		super(type, shooter, level, stack, null);
		this.stack = stack;
	}

	@Override
	protected ItemStack getPickupItem() {
		return new ItemStack(Items.ARROW);
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(RatsItemRegistry.RAT_ARROW.get());
	}

	private void spawnRat(@Nullable Entity entity, BlockPos pos) {
		if (this.pickup == Pickup.ALLOWED) {
			TamedRat rat = new TamedRat(RatsEntityRegistry.TAMED_RAT.get(), this.level());
			CompoundTag stored = this.stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
			CompoundTag ratTag = stored.contains("Rat") ? stored.getCompoundOrEmpty("Rat") : new CompoundTag();
			// 26.1: entity save data is read through ValueInput; bridge the stored CompoundTag.
			ValueInput ratInput = TagValueInput.create(ProblemReporter.DISCARDING, this.level().registryAccess(), ratTag);
			rat.readAdditionalSaveData(ratInput);
			// 26.1: Component.Serializer is gone; custom names round-trip through ComponentSerialization.CODEC.
			ratInput.read("CustomName", ComponentSerialization.CODEC).ifPresent(rat::setCustomName);
			if (ratTag.isEmpty() && this.level() instanceof ServerLevelAccessor accessor) {
				EventHooks.finalizeMobSpawn(rat, accessor, accessor.getCurrentDifficultyAt(rat.blockPosition()), EntitySpawnReason.EVENT, null);
				if (this.getOwner() instanceof Player player) {
					rat.tame(player);
				}
			}
			rat.setCommand(RatCommand.WANDER);
			rat.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			if (!this.level().isClientSide()) {
				this.level().addFreshEntity(rat);
			}
			if (entity instanceof LivingEntity living && !rat.isAlliedTo(entity)) {
				rat.setTarget(living);
			}
		}
	}

	// 26.1: AbstractArrow#onHitEntity now handles damage, piercing, crits and pickup drops itself
	// (its piercing bookkeeping fields went private), so the old full override is gone; the only
	// custom behavior left is spawning the rat on a successful hit.
	@Override
	protected void doPostHurtEffects(LivingEntity living) {
		super.doPostHurtEffects(living);
		if (!this.level().isClientSide()) {
			this.spawnRat(living, living.blockPosition());
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (this.isInGround()) {
			if (this.level() instanceof ServerLevel serverLevel) {
				this.spawnRat(null, result.getBlockPos().relative(result.getDirection()));
				this.spawnAtLocation(serverLevel, this.getPickupItem(), 0.0F);
			}
			this.discard();
		}
	}
}
