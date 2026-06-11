package com.github.alexthe666.rats.client.render;

import com.github.alexthe666.rats.server.items.DemonRatUpgradeItem;
import com.github.alexthe666.rats.server.items.RatSackItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 26.1 replaced ItemProperties.register with data-driven item model properties. These back the
 * range_dispatch/condition entries in assets/rats/items/{rat_sack,ratbow_essence,rat_upgrade_demon}.json
 * and are registered in ModClientEvents.
 */
public final class RatsItemProperties {

	private RatsItemProperties() {
	}

	/** rats:rat_count — number of rats stuffed in a rat sack (capped at 3 for the model swap). */
	public record RatCount() implements RangeSelectItemModelProperty {
		public static final MapCodec<RatCount> MAP_CODEC = MapCodec.unit(new RatCount());

		@Override
		public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
			return Math.min(3, RatSackItem.getRatsInSack(stack));
		}

		@Override
		public MapCodec<? extends RangeSelectItemModelProperty> type() {
			return MAP_CODEC;
		}
	}

	/** rats:glint_type — ratbow essence renamed to a pride-flag keyword (ordinal + 1, 0 = default). */
	public record GlintType() implements RangeSelectItemModelProperty {
		public static final MapCodec<GlintType> MAP_CODEC = MapCodec.unit(new GlintType());

		@Override
		public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
			if (stack.has(DataComponents.CUSTOM_NAME)) {
				RatsRenderType.GlintType type = RatsRenderType.GlintType.getGlintBasedOnKeyword(stack.getHoverName().getString());
				return type != null && type.changesItemTexture() ? type.ordinal() + 1 : 0;
			}
			return 0;
		}

		@Override
		public MapCodec<? extends RangeSelectItemModelProperty> type() {
			return MAP_CODEC;
		}
	}

	/** rats:soul — demon rat upgrade in soul mode. */
	public record DemonSoul() implements ConditionalItemModelProperty {
		public static final MapCodec<DemonSoul> MAP_CODEC = MapCodec.unit(new DemonSoul());

		@Override
		public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable net.minecraft.world.entity.LivingEntity owner, int seed, net.minecraft.world.item.ItemDisplayContext context) {
			return DemonRatUpgradeItem.isSoulVersion(stack);
		}

		@Override
		public MapCodec<? extends ConditionalItemModelProperty> type() {
			return MAP_CODEC;
		}
	}
}
