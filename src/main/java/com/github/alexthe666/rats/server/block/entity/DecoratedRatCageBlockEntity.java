package com.github.alexthe666.rats.server.block.entity;

import com.github.alexthe666.rats.registry.RatsBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DecoratedRatCageBlockEntity extends BlockEntity {
	private NonNullList<ItemStack> containedDeco = NonNullList.withSize(1, ItemStack.EMPTY);

	public DecoratedRatCageBlockEntity(BlockPos pos, BlockState state) {
		super(RatsBlockEntityRegistry.RAT_CAGE_DECORATED.get(), pos, state);
	}

	public DecoratedRatCageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// 26.1: replaces RatCageDecoratedBlock.onRemove - drop the contained decoration when the block changes.
	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		Level level = this.getLevel();
		if (level != null && !this.getContainedItem().isEmpty()) {
			Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, this.getContainedItem());
		}
		super.preRemoveSideEffects(pos, state);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ValueInput valueInput) {
		this.containedDeco = NonNullList.withSize(1, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(valueInput, this.containedDeco);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveWithoutMetadata(registries);
	}

	@Override
	protected void saveAdditional(ValueOutput compound) {
		super.saveAdditional(compound);
		ContainerHelper.saveAllItems(compound, this.containedDeco);
	}

	@Override
	protected void loadAdditional(ValueInput compound) {
		super.loadAdditional(compound);
		this.containedDeco = NonNullList.withSize(1, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.containedDeco);
	}

	public ItemStack getContainedItem() {
		return this.containedDeco.get(0);
	}

	public void setContainedItem(ItemStack stack) {
		this.containedDeco.set(0, stack);
		this.setChanged();
	}
}
