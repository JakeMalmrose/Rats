package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.registry.RatsToolMaterialRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class RatlantisToolItem {

	// 26.1: PickaxeItem is gone — pickaxes are plain Items configured via Properties.pickaxe(material, damage, speed).
	public static class Pickaxe extends Item {

		public Pickaxe(Item.Properties properties) {
			super(properties.pickaxe(RatsToolMaterialRegistry.RATLANTIS, 1.0F, -2.8F));
		}

		@Override
		public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
			tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc0").withStyle(ChatFormatting.YELLOW));
			tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc1").withStyle(ChatFormatting.GRAY));
		}

		@Override
		public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
			if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
				if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
					stack.hurtAndBreak(0, living, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
				}
				return true;
			} else {
				return super.mineBlock(stack, level, state, pos, living);
			}
		}
	}

	public static class Axe extends AxeItem {
		public Axe(Item.Properties properties) {
			super(RatsToolMaterialRegistry.RATLANTIS, 5.0F, -3.0F, properties);
		}

		@Override
		public float getDestroySpeed(ItemStack stack, BlockState state) {
			if (state.is(BlockTags.LEAVES)) {
				// 1.21: AxeItem.speed field removed. Use the material's mining speed directly so leaves get
				// the same fast-mine bonus they had in 1.20.1, regardless of whether vanilla considers
				// the axe preferred for leaves.
				return RatsToolMaterialRegistry.RATLANTIS.speed() * 1.5F;
			}
			return super.getDestroySpeed(stack, state);
		}

		@Override
		public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
			tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc0").withStyle(ChatFormatting.YELLOW));
			tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc1").withStyle(ChatFormatting.GRAY));
		}

		@Override
		public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
			if (state.is(BlockTags.LEAVES)) {
				if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
					stack.hurtAndBreak(0, living, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
				}
				return true;
			} else {
				return super.mineBlock(stack, level, state, pos, living);
			}
		}
	}

	public static class Shovel extends ShovelItem {
		public Shovel(Item.Properties properties) {
			super(RatsToolMaterialRegistry.RATLANTIS, 1.5F, -3.0F, properties);
		}

		@Override
		public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
			if (state.is(BlockTags.SAND)) {
				if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
					stack.hurtAndBreak(0, living, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
				}
				return true;
			} else {
				return super.mineBlock(stack, level, state, pos, living);
			}
		}

		@Override
		public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
			tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc0").withStyle(ChatFormatting.YELLOW));
			tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc1").withStyle(ChatFormatting.GRAY));
		}
	}

	public static class Hoe extends HoeItem {
		public Hoe(Item.Properties properties) {
			super(RatsToolMaterialRegistry.RATLANTIS, -3.0F, 0.0F, properties);
		}

		@Override
		public InteractionResult useOn(UseOnContext context) {
			boolean tilledAny = false;
			Level level = context.getLevel();
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos pos = context.getClickedPos().offset(x, 0, z);
					BlockState toolModifiedState = level.getBlockState(pos).getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
					Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = toolModifiedState == null ? null : Pair.of(ctx -> true, changeIntoState(toolModifiedState));
					if (pair != null) {
						Predicate<UseOnContext> predicate = pair.getFirst();
						Consumer<UseOnContext> consumer = pair.getSecond();
						if (predicate.test(context)) {
							Player player = context.getPlayer();
							level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
							if (!level.isClientSide()) {
								BlockHitResult result = new BlockHitResult(Vec3.atCenterOf(pos), context.getClickedFace(), pos, false);
								consumer.accept(new UseOnContext(context.getPlayer(), context.getHand(), result));
								tilledAny = true;
							}
						}
					}
				}
			}

			if (tilledAny && context.getPlayer() != null) {
				context.getItemInHand().hurtAndBreak(1, context.getPlayer(), context.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}


		@Override
		public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
			super.appendHoverText(stack, context, display, tooltip, flag);
			tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc0").withStyle(ChatFormatting.YELLOW));
			tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc1").withStyle(ChatFormatting.GRAY));
		}
	}
}
