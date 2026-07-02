package com.github.alexthe666.rats.server.items.upgrades;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.ai.goal.RatDepositGoal;
import com.github.alexthe666.rats.server.entity.ai.goal.RatPickupGoal;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.ChangesAIUpgrade;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.ChangesOverlayUpgrade;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.TickRatUpgrade;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class EnergyRatUpgradeItem extends BaseRatUpgradeItem implements ChangesOverlayUpgrade, ChangesAIUpgrade, TickRatUpgrade {

	private final int transferRate;
	private final int chargeRate;

	public EnergyRatUpgradeItem(Item.Properties properties, int rarity, int transferRate, int itemChargeRate) {
		super(properties, rarity, 0);
		this.transferRate = transferRate;
		this.chargeRate = itemChargeRate;
	}

	public int getRFTransferRate() {
		return this.transferRate;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		tooltip.accept(Component.translatable(RatsLangConstants.RAT_UPGRADE_ENERGY_DESC0).withStyle(ChatFormatting.GRAY));
		tooltip.accept(Component.translatable(RatsLangConstants.RAT_UPGRADE_ENERGY_DESC1).withStyle(ChatFormatting.GRAY));
		tooltip.accept(Component.translatable(RatsLangConstants.RAT_UPGRADE_ENERGY_TRANSFER, this.transferRate).withStyle(ChatFormatting.GRAY));
		if (RatConfig.ratsChargeHeldItems) {
			tooltip.accept(Component.translatable(RatsLangConstants.RAT_UPGRADE_ENERGY_CHARGE, this.chargeRate).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public @Nullable RenderType getOverlayTexture(ItemStack stack, TamedRat rat, float partialTicks) {
		float f = (float) rat.tickCount + partialTicks;
		return rat.getHeldRF() > 0 ? RenderTypes.energySwirl(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/entity/psychic.png"), f * 0.01F, f * 0.01F) : null;
	}

	@Override
	public List<Goal> addNewWorkGoals(TamedRat rat) {
		return List.of(new RatPickupGoal(rat, RatPickupGoal.PickupType.ENERGY), new RatDepositGoal(rat, RatDepositGoal.DepositType.ENERGY));
	}

	@Override
	public void tick(TamedRat rat) {
		if (RatConfig.ratsChargeHeldItems && rat.getHeldRF() > 0 && !rat.getMainHandItem().isEmpty()) {
			ItemStack stack = rat.getMainHandItem();
			// 26.1: IEnergyStorage/Capabilities.EnergyStorage were replaced with the transfer API's EnergyHandler,
			// and item capabilities now take an ItemAccess context.
			EnergyHandler energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
			if (energyStorage != null && energyStorage.getAmountAsLong() < energyStorage.getCapacityAsLong()) {
				int energyToTransfer = Math.min(rat.getHeldRF(), this.chargeRate);
				try (Transaction tx = Transaction.open(null)) {
					energyToTransfer = energyStorage.insert(energyToTransfer, tx);
					tx.commit();
				}
				rat.setHeldRF(Math.max(0, rat.getHeldRF() - energyToTransfer));
			}
		}
	}
}
