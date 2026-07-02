package com.github.alexthe666.rats.client.gui;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.inventory.AutoCurdlerMenu;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

public class AutoCurdlerScreen extends AbstractContainerScreen<AutoCurdlerMenu> {

	private static final Identifier TEXTURE =
		Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/gui/container/auto_curdler.png");

	private final AutoCurdlerMenu curdler;

	public AutoCurdlerScreen(AutoCurdlerMenu container, Inventory inv, Component name) {
		super(container, inv, name);
		this.curdler = container;
	}

	// 26.1: fluid still textures now come from the data-driven fluid model set; the old
	// IClientFluidTypeExtensions#getStillTexture + Tesselator/BufferUploader immediate path is gone.
	public static void renderFluidStack(GuiGraphicsExtractor graphics,
										int xPosition,
										int yPosition,
										int desiredWidth,
										int desiredHeight,
										Fluid fluid) {

		if (fluid == null || desiredWidth <= 0 || desiredHeight <= 0) return;

		TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager()
			.getFluidStateModelSet()
			.get(fluid.defaultFluidState())
			.stillMaterial().sprite();

		int xTileCount = desiredWidth / 16;
		int xRemainder = desiredWidth - (xTileCount * 16);

		int yTileCount = desiredHeight / 16;
		int yRemainder = desiredHeight - (yTileCount * 16);

		float uMin = sprite.getU0();
		float uMax = sprite.getU1();
		float vMin = sprite.getV0();
		float vMax = sprite.getV1();

		float uDif = uMax - uMin;
		float vDif = vMax - vMin;

		for (int xTile = 0; xTile <= xTileCount; xTile++) {

			int width = (xTile == xTileCount) ? xRemainder : 16;
			if (width <= 0) continue;

			int x = xPosition + (xTile * 16);
			int maskRight = 16 - width;

			float uLocalDif = uDif * maskRight / 16f;

			for (int yTile = 0; yTile <= yTileCount; yTile++) {

				int height = (yTile == yTileCount) ? yRemainder : 16;
				if (height <= 0) continue;

				int y = yPosition - ((yTile + 1) * 16);
				int maskTop = 16 - height;

				float vLocalDif = vDif * maskTop / 16f;

				graphics.blit(sprite.atlasLocation(), x, y + maskTop, x + width, y + 16,
						uMin + uLocalDif, uMax, vMin + vLocalDif, vMax);
			}
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(graphics, mouseX, mouseY, partialTicks);

		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;

		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

		int progress = this.curdler.getCookProgressionScaled();
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 63, j + 35, 176, 0, progress + 1, 16, 256, 256);

		int tankWidth = 24;
		int tankHeight = 63;

		int capacity = this.curdler.getTankCapacity();
		int amount = 0;

		if (capacity > 0) {
			amount = Math.round((this.curdler.getFluidAmount() / (float) capacity) * (tankHeight - 4));
		}

		if (amount > 0) {
			renderFluidStack(
				graphics,
				i + 29,
				j + 73,
				tankWidth,
				amount,
				NeoForgeMod.MILK.get()
			);
		}

		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 29, j + 12, 0, 166, tankWidth, tankHeight, 256, 256);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {

		if (this.isHovering(29, 15, 24, 58, mouseX, mouseY)) {

			String fluidName = new FluidStack(
				NeoForgeMod.MILK.get(),
				Math.max(this.curdler.getTankCapacity(), 1)
			).getHoverName().getString();

			String fluidSize =
				this.curdler.getFluidAmount() + " " +
					Component.translatable(RatsLangConstants.CURDLER_MB).getString();

			List<Component> list = Arrays.asList(
				Component.literal(fluidName).withStyle(ChatFormatting.BLUE),
				Component.literal(fluidSize).withStyle(ChatFormatting.GRAY)
			);

			// 26.1: tooltips are queued via setTooltipForNextFrame (absolute screen coordinates) instead of drawn immediately.
			graphics.setTooltipForNextFrame(
				this.font,
				Lists.transform(list, Component::getVisualOrderText),
				mouseX,
				mouseY
			);
		}
	}
}
