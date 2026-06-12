package com.github.alexthe666.rats.server.entity.misc;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.github.alexthe666.rats.registry.RatlantisEntityRegistry;
import com.github.alexthe666.rats.registry.RatlantisItemRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PiratWoodChestBoat extends PiratWoodBoat implements HasCustomInventoryScreen, ContainerEntity {

	private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
	@Nullable
	private net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> lootTable;
	private long lootTableSeed;

	public PiratWoodChestBoat(EntityType<? extends PiratWoodBoat> type, Level level) {
		// 26.1: the drop item is fixed at construction time (AbstractBoat.getDropItem() is final)
		super(type, level, RatlantisItemRegistry.PIRAT_CHEST_BOAT::get);
	}

	public PiratWoodChestBoat(Level level, double x, double y, double z) {
		this(RatlantisEntityRegistry.CHEST_BOAT.get(), level);
		this.setPos(x, y, z);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	@Override
	protected float getSinglePassengerXOffset() {
		return 0.15F;
	}

	@Override
	protected int getMaxPassengers() {
		return 1;
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput tag) {
		super.addAdditionalSaveData(tag);
		this.addChestVehicleSaveData(tag);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput tag) {
		super.readAdditionalSaveData(tag);
		this.readChestVehicleSaveData(tag);
	}

	@Override
	public void destroy(ServerLevel level, DamageSource damageSource) {
		this.destroy(level, this.getDropItem());
		this.chestVehicleDestroyed(damageSource, level, this);
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		if (!this.level().isClientSide() && reason.shouldDestroy()) {
			Containers.dropContents(this.level(), this, this);
		}

		super.remove(reason);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
		if (this.canAddPassenger(player) && !player.isSecondaryUseActive()) {
			return super.interact(player, hand, location);
		} else {
			InteractionResult interactionresult = this.interactWithContainerVehicle(player);
			if (interactionresult.consumesAction() && player.level() instanceof ServerLevel serverLevel) {
				this.gameEvent(GameEvent.CONTAINER_OPEN, player);
				PiglinAi.angerNearbyPiglins(serverLevel, player, true);
			}

			return interactionresult;
		}
	}

	@Override
	public void openCustomInventoryScreen(Player player) {
		player.openMenu(this);
		if (player.level() instanceof ServerLevel serverLevel) {
			this.gameEvent(GameEvent.CONTAINER_OPEN, player);
			PiglinAi.angerNearbyPiglins(serverLevel, player, true);
		}

	}

	@Override
	public void clearContent() {
		this.clearChestVehicleContent();
	}

	@Override
	public int getContainerSize() {
		return 27;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.getChestVehicleItem(index);
	}

	@Override
	public ItemStack removeItem(int index, int amount) {
		return this.removeChestVehicleItem(index, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return this.removeChestVehicleItemNoUpdate(index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.setChestVehicleItem(index, stack);
	}

	@Override
	public SlotAccess getSlot(int index) {
		return this.getChestVehicleSlot(index);
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(Player player) {
		return this.isChestVehicleStillValid(player);
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		if (this.lootTable != null && player.isSpectator()) {
			return null;
		} else {
			this.unpackLootTable(inventory.player);
			return ChestMenu.threeRows(id, inventory, this);
		}
	}

	public void unpackLootTable(@Nullable Player player) {
		this.unpackChestVehicleLootTable(player);
	}

	@Nullable
	@Override
	public net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> getContainerLootTable() {
		return this.lootTable;
	}

	@Override
	public void setContainerLootTable(@Nullable net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> key) {
		this.lootTable = key;
	}

	@Override
	public long getContainerLootTableSeed() {
		return this.lootTableSeed;
	}

	@Override
	public void setContainerLootTableSeed(long seed) {
		this.lootTableSeed = seed;
	}

	@Override
	public NonNullList<ItemStack> getItemStacks() {
		return this.itemStacks;
	}

	@Override
	public void clearItemStacks() {
		this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
	}
}
