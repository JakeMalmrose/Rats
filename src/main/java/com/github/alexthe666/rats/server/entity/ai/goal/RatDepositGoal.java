package com.github.alexthe666.rats.server.entity.ai.goal;

import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.block.entity.AutoCurdlerBlockEntity;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.message.UpdateCurdlerFluidPacket;
import com.github.alexthe666.rats.server.message.UpdateRatFluidPacket;
import com.github.alexthe666.rats.server.misc.RatUpgradeUtils;
import com.github.alexthe666.rats.server.misc.RatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.EnumSet;
import java.util.List;

public class RatDepositGoal extends Goal implements RatWorkGoal {
	private final TamedRat rat;
	private final DepositType type;
	private BlockPos targetBlock = null;

	public RatDepositGoal(TamedRat entity, DepositType type) {
		super();
		this.rat = entity;
		this.type = type;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (!this.rat.canMove()) return false;
		if (!this.rat.getCommand().workCommand) return false;
		if (this.rat.getTarget() != null) return false;
		if (this.rat.getDepositPos().isEmpty() || !this.rat.getDepositPos().get().dimension().equals(this.rat.level().dimension()) || RatUtils.isBlockProtected(this.rat.level(), this.rat.getDepositPos().get().pos(), this.rat))
			return false;

		BlockEntity te = this.rat.level().getBlockEntity(this.rat.getDepositPos().get().pos());
		if (te == null) return false;

		if (this.type == DepositType.INVENTORY) {
			if (!this.rat.shouldDepositItem(this.rat.getMainHandItem())) return false;
			if (this.rat.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
				return false;
			}
			if (RatUpgradeUtils.hasUpgrade(this.rat, RatsItemRegistry.RAT_UPGRADE_PLATTER.get())) {
				if (this.rat.getMainHandItem().getCount() < 64 && !this.getItemsOfTypeAround(this.rat.getMainHandItem()).isEmpty())
					return false;
			}
			if (te.getLevel().getCapability(Capabilities.Item.BLOCK, te.getBlockPos(), this.rat.depositFacing) == null) {
				return false;
			}

		} else if (this.type == DepositType.ENERGY) {
			if (this.rat.getRFTransferRate() <= 0 || this.rat.getHeldRF() <= 0) {
				return false;
			}
			if (te.getLevel().getCapability(Capabilities.Energy.BLOCK, te.getBlockPos(), this.rat.depositFacing) == null) {
				return false;
			}
		} else if (this.type == DepositType.FLUID) {
			if (this.rat.transportingFluid.isEmpty() || this.rat.transportingFluid.getAmount() == 0) {
				return false;
			}
			if (te.getLevel().getCapability(Capabilities.Fluid.BLOCK, te.getBlockPos(), this.rat.depositFacing) == null) {
				return false;
			}
		}
		this.resetTarget();
		return targetBlock != null;
	}

	private List<ItemEntity> getItemsOfTypeAround(ItemStack stack) {
		return this.rat.level().getEntitiesOfClass(ItemEntity.class, this.rat.getBoundingBox().inflate(this.rat.getRadius()), item -> {
			if (!ItemStack.isSameItemSameComponents(stack, item.getItem())) return false;
			Path path = this.rat.getNavigation().createPath(item, 1);
			return path != null && path.canReach();
		});
	}

	private void resetTarget() {
		this.targetBlock = this.rat.getDepositPos().get().pos();
	}

	@Override
	public boolean canContinueToUse() {
		if (RatUpgradeUtils.hasUpgrade(this.rat, RatsItemRegistry.RAT_UPGRADE_PLATTER.get())) {
			if (this.rat.getMainHandItem().getCount() < 64 && this.getItemsOfTypeAround(this.rat.getMainHandItem()).isEmpty())
				return false;
		}
		return this.targetBlock != null && !this.rat.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && this.rat.shouldDepositItem(this.rat.getMainHandItem());
	}

	@Override
	public void start() {
		this.rat.isCurrentlyWorking = true;
	}

	@Override
	public void stop() {
		this.rat.isCurrentlyWorking = false;
	}

	private Vec3 getMovePos() {
		return Vec3.atBottomCenterOf(this.targetBlock.relative(this.rat.depositFacing));
	}

	@Override
	public void tick() {
		BlockEntity te = this.rat.level().getBlockEntity(this.targetBlock);
		if (this.targetBlock != null && te != null) {
			this.rat.getNavigation().moveTo(this.getMovePos().x(), this.getMovePos().y(), this.getMovePos().z(), 1.25D);
			double distance = Math.sqrt(this.rat.distanceToSqr(this.getMovePos().x(), this.getMovePos().y(), this.getMovePos().z()));
			if (distance < 4.5D * this.rat.getRatDistanceModifier() && distance > 2.5D * this.rat.getRatDistanceModifier() && te instanceof Container container) {
				this.toggleChest(container, true);
			}
			if (distance <= 2.0D * this.rat.getRatDistanceModifier()) {
				if (te instanceof Container container) {
					this.toggleChest(container, false);
				}

				this.executeTask(te);
				this.targetBlock = null;
				this.stop();
			}
		}
	}

	public void toggleChest(Container te, boolean open) {
		if (te instanceof ChestBlockEntity chest) {
			if (open) {
				this.rat.level().blockEvent(this.targetBlock, chest.getBlockState().getBlock(), 1, 1);
			} else {
				this.rat.level().blockEvent(this.targetBlock, chest.getBlockState().getBlock(), 1, 0);
			}
			this.rat.level().gameEvent(this.rat, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, chest.getBlockPos());
		}
	}

	private void executeTask(BlockEntity entity) {
		if (this.type == DepositType.INVENTORY) {
			ResourceHandler<ItemResource> resolvedHandler = entity.getLevel().getCapability(Capabilities.Item.BLOCK, entity.getBlockPos(), this.rat.depositFacing);
			if (resolvedHandler != null) {
				ItemStack duplicate = this.rat.getItemInHand(InteractionHand.MAIN_HAND).copy();
				//26.1: the transfer API always returns a copied remainder, so compare counts instead of instances
				if (ItemUtil.insertItemReturnRemaining(resolvedHandler, duplicate, true, null).getCount() != duplicate.getCount()) {
					ItemStack shrunkenStack = ItemUtil.insertItemReturnRemaining(resolvedHandler, duplicate, false, null);
					if (shrunkenStack.isEmpty()) {
						this.rat.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
					} else {
						this.rat.setItemInHand(InteractionHand.MAIN_HAND, shrunkenStack);
					}
				}
			}
		} else if (this.type == DepositType.ENERGY) {
			EnergyHandler storage = entity.getLevel().getCapability(Capabilities.Energy.BLOCK, entity.getBlockPos(), this.rat.depositFacing);
			if (storage != null) {
				int howMuchWeHave = this.rat.getHeldRF();
				int inputtedEnergy = 0;
				try {
					if (howMuchWeHave > 0) {
						try (Transaction tx = Transaction.open(null)) {
							inputtedEnergy = storage.insert(howMuchWeHave, tx);
							tx.commit();
						}
					}
				} catch (Exception e) {
					//container is empty
				}
				if (inputtedEnergy > 0) {
					this.rat.setHeldRF(Math.max(0, this.rat.getHeldRF() - inputtedEnergy));
				}
			}
		} else if (this.type == DepositType.FLUID) {
			FluidStack copiedFluid = this.rat.transportingFluid.copy();
			ResourceHandler<FluidResource> fluidHandler = entity.getLevel().getCapability(Capabilities.Fluid.BLOCK, entity.getBlockPos(), this.rat.depositFacing);
			if (fluidHandler != null) {
				if (!this.rat.transportingFluid.isEmpty()) {
					int minusAmount = 0;
					try {
						if (fluidHandler.size() > 0) {
							FluidStack firstTank = FluidUtil.getStack(fluidHandler, 0);
							if (fluidHandler.size() > 1) {
								for (int i = 0; i < fluidHandler.size(); i++) {
									FluidStack otherTank = FluidUtil.getStack(fluidHandler, i);
									if (copiedFluid != null && FluidStack.isSameFluidSameComponents(copiedFluid, otherTank)) {
										firstTank = otherTank;
									}
								}
							}
							if (firstTank.isEmpty() || (copiedFluid == null || FluidStack.isSameFluidSameComponents(copiedFluid, firstTank))) {
								try (Transaction tx = Transaction.open(null)) {
									minusAmount = fluidHandler.insert(FluidResource.of(copiedFluid), copiedFluid.getAmount(), tx);
									tx.commit();
								}
							}
						}
					} catch (Exception e) {
						//container is empty
					}

					if (minusAmount > 0) {
						int total = copiedFluid.getAmount() - minusAmount;
						if (total <= 0) {
							this.rat.transportingFluid = FluidStack.EMPTY;
						} else {
							this.rat.transportingFluid.setAmount(total);
						}
						if (!this.rat.level().isClientSide()) {
							PacketDistributor.sendToAllPlayers(new UpdateRatFluidPacket(this.rat.getId(), this.rat.transportingFluid));
							if (this.rat.level().getBlockEntity(this.targetBlock) instanceof AutoCurdlerBlockEntity curdler) {
								PacketDistributor.sendToAllPlayers(new UpdateCurdlerFluidPacket(this.targetBlock.asLong(), curdler.getTankFluid()));
							}
						}
						SoundEvent sound = this.rat.transportingFluid.isEmpty() ? SoundEvents.BUCKET_EMPTY : SoundEvents.BUCKET_FILL;
						this.rat.playSound(sound, 1, 1);
					}
				}
			}
		}
	}

	@Override
	public TaskType getRatTaskType() {
		return TaskType.DEPOSIT;
	}

	public enum DepositType {
		INVENTORY,
		FLUID,
		ENERGY
	}
}
