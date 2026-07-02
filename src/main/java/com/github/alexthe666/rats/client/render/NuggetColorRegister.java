package com.github.alexthe666.rats.client.render;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.items.OreRatNuggetItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NuggetColorRegister {

	public static final ItemStack FALLBACK_STACK = new ItemStack(Items.IRON_INGOT);
	public static final Map<String, Integer> TEXTURES_TO_COLOR = new HashMap<>();

	public static int getNuggetColor(ItemStack stack) {
		ItemStack poopStack = OreRatNuggetItem.getStoredItem(stack, FALLBACK_STACK);
		String poopName = poopStack.getDisplayName().getString();
		if (TEXTURES_TO_COLOR.get(poopName) != null) {
			return TEXTURES_TO_COLOR.get(poopName);
		} else {
			int color = 0XFFFFFF;
			try {
				Color texColour = getAverageColour(getTextureAtlas(poopStack));
				color = texColour.getARGB();
			} catch (NullPointerException e) {
				// 26.1: Item#getDescription is gone; getName(stack) is the equivalent display name lookup.
				RatsMod.LOGGER.warn("Could not fetch average nugget color for resource {}, defaulting to white.", poopStack.getItem().getName(poopStack).getString());
			}
			TEXTURES_TO_COLOR.put(poopName, color);
			return color;
		}
	}

	private static Color getAverageColour(TextureAtlasSprite image) {
		float red = 0;
		float green = 0;
		float blue = 0;
		float count = 0;
		int uMax = image.contents().width();
		int vMax = image.contents().height();
		// 26.1: getPixelRGBA now delegates to NativeImage#getPixel and returns ARGB (it used to be ABGR),
		// so red/blue channel shifts are swapped relative to the 1.20 code.
		for (float i = 0; i < uMax; i++)
			for (float j = 0; j < vMax; j++) {
				int alpha = image.getPixelRGBA(0, (int) i, (int) j) >> 24 & 0xFF;
				if (alpha != 255) {
					continue;
				}
				red += image.getPixelRGBA(0, (int) i, (int) j) >> 16 & 0xFF;
				green += image.getPixelRGBA(0, (int) i, (int) j) >> 8 & 0xFF;
				blue += image.getPixelRGBA(0, (int) i, (int) j) & 0xFF;
				count++;
			}
		//Average color
		return new Color((int) (red / count), (int) (green / count), (int) (blue / count));
	}

	// 26.1: ItemModelShaper/BakedModel#getParticleIcon are gone; resolve the item's render state and
	// take a layer's particle material instead.
	private static TextureAtlasSprite getTextureAtlas(ItemStack oreStack) {
		ItemStackRenderState renderState = new ItemStackRenderState();
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(renderState, oreStack, ItemDisplayContext.GUI, Minecraft.getInstance().level, null, 0);
		Material.Baked material = renderState.pickParticleMaterial(RandomSource.create(42L));
		return Objects.requireNonNull(material).sprite();
	}

	//java.awt bad
	//awt crashes macs, so here's a color holder
	public static class Color {
		private final int value;

		public Color(int r, int g, int b) {
			this(r, g, b, 255);
		}

		public Color(int r, int g, int b, int a) {
			this.value = ((a & 0xFF) << 24) |
					((r & 0xFF) << 16) |
					((g & 0xFF) << 8) |
					((b & 0xFF));
		}

		public int getARGB() {
			return this.value;
		}
	}

	/**
	 * 26.1 item tint source replacing the old ItemColor handler for the ore rat nugget
	 * (layer 1 tinting); register as {@code rats:nugget} via RegisterColorHandlersEvent.ItemTintSources
	 * and reference it from assets/rats/items/rat_nugget_ore.json.
	 */
	public record NuggetTintSource() implements ItemTintSource {

		public static final MapCodec<NuggetTintSource> MAP_CODEC = MapCodec.unit(new NuggetTintSource());

		@Override
		public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
			return 0xFF000000 | getNuggetColor(stack);
		}

		@Override
		public MapCodec<? extends ItemTintSource> type() {
			return MAP_CODEC;
		}
	}
}
