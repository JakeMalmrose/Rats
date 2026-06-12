package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RatSackItem extends Item {

	public RatSackItem(Item.Properties properties) {
		super(properties);
	}

	private static CompoundTag readTag(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	private static void writeTag(ItemStack stack, CompoundTag tag) {
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}

	public static void packRatIntoSack(ItemStack sack, TamedRat rat, int ratCount) {
		CompoundTag tag = readTag(sack);
		// 26.1: entity save data goes through ValueOutput, bridged via TagValueOutput
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, rat.registryAccess());
		rat.addAdditionalSaveData(output);
		CompoundTag ratTag = output.buildResult();
		if (rat.hasCustomName()) {
			ratTag.store("CustomName", ComponentSerialization.CODEC, rat.getCustomName());
		}
		tag.put("Rat_" + ratCount, ratTag);
		writeTag(sack, tag);
	}

	public static int getRatsInSack(ItemStack sack) {
		int ratCount = 0;
		CompoundTag tag = readTag(sack);
		for (String tagInfo : tag.keySet()) {
			if (tagInfo.contains("Rat")) ratCount++;
		}
		return ratCount;
	}

	public static int ejectRatsFromSack(ItemStack stack, Level level, BlockPos pos) {
		int ratCount = 0;
		CompoundTag tag = readTag(stack);
		for (String tagInfo : tag.keySet()) {
			if (tagInfo.contains("Rat")) {
				ratCount++;
				CompoundTag ratTag = tag.getCompoundOrEmpty(tagInfo);
				TamedRat rat = new TamedRat(RatsEntityRegistry.TAMED_RAT.get(), level);
				rat.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), ratTag));
				ratTag.read("CustomName", ComponentSerialization.CODEC).ifPresent(rat::setCustomName);
				rat.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				if (!level.isClientSide()) {
					level.addFreshEntity(rat);
				}
			}
		}
		return ratCount;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		int ratCount = 0;
		List<String> ratNames = new ArrayList<>();
		CompoundTag tag = readTag(stack);
		for (String tagInfo : tag.getAllKeys()) {
			if (tagInfo.contains("Rat")) {
				CompoundTag ratTag = tag.getCompoundOrEmpty(tagInfo);
				ratCount++;
				String ratName = Component.translatable("entity.rats.rat").getString();
				if (!ratTag.getStringOr("CustomName", "").isEmpty()) {
					Component ratNameTag = Component.Serializer.fromJson(ratTag.getStringOr("CustomName", ""), RegistryAccess.EMPTY);
					if (ratNameTag != null) {
						ratName = ratNameTag.getString();
					}
				}
				ratNames.add(ratName);
			}
		}
		tooltip.add(Component.translatable(RatsLangConstants.RAT_SACK_CONTAINED_RATS, ratCount, RatConfig.ratSackCapacity).withStyle(ChatFormatting.GRAY));
		if (!ratNames.isEmpty()) {
			for (int i = 0; i < ratNames.size(); i++) {
				if (i < 3) {
					tooltip.add(Component.literal(ratNames.get(i)).withStyle(ChatFormatting.GRAY));
				} else {
					break;
				}
			}
			if (ratNames.size() > 3) {
				tooltip.add(Component.translatable(RatsLangConstants.AND_MORE, ratNames.size() - 3).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
		if (stack.is(RatsItemRegistry.RAT_SACK.get()) && getRatsInSack(stack) > 0) {
			int ratCount = ejectRatsFromSack(stack, context.getLevel(), context.getClickedPos().relative(context.getClickedFace()));

			if (ratCount > 0) {
				context.getPlayer().displayClientMessage(Component.translatable(RatsLangConstants.RAT_SACK_RELEASED_RATS, ratCount), true);
				writeTag(stack, new CompoundTag());
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void onDestroyed(ItemEntity entity, DamageSource source) {
		ItemStack stack = entity.getItem();
		if (getRatsInSack(stack) > 0) {
			ejectRatsFromSack(stack, entity.level(), entity.blockPosition());
		}
	}
}
