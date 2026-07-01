package com.github.alexthe666.rats.server.block;

import com.github.alexthe666.rats.registry.RatsSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;

public class CheeseCauldronBlock extends AbstractCauldronBlock {
	public static final com.mojang.serialization.MapCodec<CheeseCauldronBlock> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec()
	).apply(instance, props -> new CheeseCauldronBlock(props, null, net.minecraft.core.cauldron.CauldronInteractions.WATER)));

	@Override
	protected com.mojang.serialization.MapCodec<? extends AbstractCauldronBlock> codec() {
		return CODEC;
	}

	private final DeferredHolder<Block, Block> drop;

	// 26.1: CauldronInteraction.InteractionMap became CauldronInteraction.Dispatcher.
	public CheeseCauldronBlock(BlockBehaviour.Properties properties, DeferredHolder<Block, Block> dropBlock, CauldronInteraction.Dispatcher interaction) {
		super(properties, interaction);
		this.drop = dropBlock;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		InteractionResult parent = super.useItemOn(stack, state, level, pos, player, hand, hit);
		// 26.1: ItemInteractionResult is gone; "unhandled" is now expressed as TRY_WITH_EMPTY_HAND or PASS.
		if (parent == InteractionResult.TRY_WITH_EMPTY_HAND || parent == InteractionResult.PASS) {
			level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
			level.playSound(null, pos, RatsSoundRegistry.CHEESE_CAULDRON_EMPTY.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
			if (!player.getInventory().add(new ItemStack(this.drop.get()))) {
				player.drop(new ItemStack(this.drop.get()), false);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
		return this.getShape(state, getter, pos, CollisionContext.empty());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return Shapes.join(Shapes.block(), box(2.0D, 15.0D, 2.0D, 14.0D, 16.0D, 14.0D), BooleanOp.ONLY_FIRST);
	}

	@Override
	protected double getContentHeight(BlockState state) {
		return 0.9375D;
	}

	@Override
	public boolean isFull(BlockState state) {
		return true;
	}

	@Override
	protected ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
		return new ItemStack(Items.CAULDRON);
	}
}
