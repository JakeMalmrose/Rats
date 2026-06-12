package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.registry.RatsSoundRegistry;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class OreRatNuggetItem extends Item {

	public OreRatNuggetItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), RatsSoundRegistry.RAT_NUGGET_ORE.get(), SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
		ItemStack poopStack = getStoredItem(itemstack, new ItemStack(Items.IRON_INGOT));
		if (!player.getInventory().add(poopStack)) {
			player.drop(poopStack, false);
		}
		if (!player.isCreative()) {
			itemstack.shrink(1);
		}
		return InteractionResult.SUCCESS;
	}

	public static ItemStack getStoredItem(ItemStack poopItem, ItemStack fallback) {
		CustomData data = poopItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag tag = data.copyTag();
		// 26.1: ItemStack NBT round-trips go through ItemStack.CODEC (CompoundTag.read/store).
		ItemStack oreItem = tag.read("OreItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
		return oreItem.isEmpty() ? fallback : oreItem;
	}

	public static ItemStack getIngot(Level level, ItemStack stack) {
		// 26.1: recipes are only accessible server-side (ServerLevel.recipeAccess()); the recipe result is
		// produced via assemble(input) instead of getResultItem(RegistryAccess).
		if (level instanceof ServerLevel serverLevel) {
			SingleRecipeInput input = new SingleRecipeInput(stack);
			SmeltingRecipe recipe = serverLevel.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, serverLevel).map(net.minecraft.world.item.crafting.RecipeHolder::value).orElse(null);
			if (recipe != null) {
				ItemStack result = recipe.assemble(input);
				if (!result.isEmpty()) {
					return result.copy();
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack saveResourceToNugget(ItemStack resource) {
		ItemStack stack = new ItemStack(RatsItemRegistry.RAT_NUGGET_ORE.get());
		CompoundTag nuggetTag = new CompoundTag();
		nuggetTag.store("OreItem", ItemStack.CODEC, resource);
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nuggetTag));
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		if (flag.isCreative()) {
			ItemStack ingot = getStoredItem(stack, new ItemStack(Items.AIR));
			tooltip.accept(Component.translatable(RatsLangConstants.ORE_NUGGET_CONTAINS, ingot.getDisplayName().getString()).withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.accept(Component.translatable("item.rats.rat_nugget_ore.desc").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
		}
	}
}
