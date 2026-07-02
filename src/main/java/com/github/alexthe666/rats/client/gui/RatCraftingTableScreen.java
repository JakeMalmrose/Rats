package com.github.alexthe666.rats.client.gui;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.inventory.RatCraftingTableMenu;
import com.github.alexthe666.rats.server.message.CycleRatRecipePacket;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 1.21: RecipeBookComponent / RecipeUpdateListener require the menu to be a RecipeBookMenu<I,R>,
// which RatCraftingTableMenu intentionally is not (see comment there). The recipe-book sidebar is
// therefore not present on this screen; cycle-result buttons and the cooking-progress display
// remain so the rat-driven crafting flow still has full UI affordance.
public class RatCraftingTableScreen extends AbstractContainerScreen<RatCraftingTableMenu> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/gui/container/rat_crafting_table.png");
	private final Inventory playerInventory;
	private final RatCraftingTableMenu table;

	public RatCraftingTableScreen(RatCraftingTableMenu container, Inventory inv, Component name) {
		// 26.1: imageWidth/imageHeight are final; dimensions go through the super constructor.
		super(container, inv, name, 176, 211);
		this.playerInventory = inv;
		this.table = container;
	}

	@Override
	protected void init() {
		super.init();
		this.renderables.clear();
		this.addRenderableWidget(new CycleResultButton(this.leftPos + 100, this.topPos + 58, false, button -> {
			this.table.incrementRecipeIndex(false);
			ClientPacketDistributor.sendToServer(new CycleRatRecipePacket(this.table.getCraftingTable().getBlockPos().asLong(), false));
		}));
		this.addRenderableWidget(new CycleResultButton(this.leftPos + 100, this.topPos + 28, true, button -> {
			this.table.incrementRecipeIndex(true);
			ClientPacketDistributor.sendToServer(new CycleRatRecipePacket(this.table.getCraftingTable().getBlockPos().asLong(), true));
		}));
	}

	// 26.1: the z=300/disableDepthTest overlay hack is gone; drawing after super.extractContents
	// layers the ghost-grid shading and guide item above the slot items.
	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractContents(graphics, mouseX, mouseY, partialTicks);

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				int xPos = this.leftPos + 36 + i * 18;
				int yPos = this.topPos + 22 + j * 18;
				graphics.fill(xPos, yPos, xPos + 16, yPos + 16, 0x9f8b8b8b);
			}
		}

		Optional<CraftingRecipe> recipe = this.table.getCraftingTable().getGuideRecipe();
		if (recipe.isPresent() && !this.table.getSlot(0).hasItem()) {
			// 26.1: Recipe.getResultItem(RegistryAccess) is gone; the guide recipe matched the ghost
			// matrix, so assembling against it yields the display result.
			var matrix = this.table.getCraftingTable().matrixHandler;
			List<ItemStack> items = new ArrayList<>(matrix.getSlots());
			for (int slot = 0; slot < matrix.getSlots(); slot++) {
				items.add(matrix.getStackInSlot(slot));
			}
			ItemStack result = recipe.get().assemble(CraftingInput.of(3, 3, items));
			graphics.item(result, this.leftPos + 130, this.topPos + 40);
			graphics.fill(this.leftPos + 130, this.topPos + 40, this.leftPos + 146, this.topPos + 56, 0x9f8b8b8b);
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(graphics, mouseX, mouseY, partialTicks);
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
		int l = this.table.getCookProgressionScaled();
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + 96, this.topPos + 39, 0, 211, l, 16, 256, 256);
		if (this.table.getCraftingTable().hasRat()) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + 8, this.topPos + 20, 176, 0, 21, 21, 256, 256);
		} else {
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + 7, this.topPos + 40, 198, 0, 21, 21, 256, 256);
		}
		if (this.table.getCraftingTable().getRecipeUsed() == null) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + 95, this.topPos + 38, 220, 0, 21, 21, 256, 256);
		}
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		String s = this.getTitle().getString();
		graphics.text(this.font, s, this.imageWidth / 2 - this.font.width(s) / 2, 5, 4210752, false);
		graphics.text(this.font, this.playerInventory.getDisplayName().getString(), 8, this.imageHeight - 93, 4210752, false);
		graphics.text(this.font, Component.translatable(RatsLangConstants.CRAFTING_INPUT), 8, this.imageHeight - 125, 4210752, false);

		if (!this.table.getCraftingTable().hasRat()) {
			if (this.isHovering(6, 34, 25, 29, mouseX, mouseY)) {
				Component ratDesc = Component.translatable(RatsLangConstants.CRAFTING_NEEDS_RAT);
				// 26.1: tooltips are queued via setTooltipForNextFrame (absolute screen coordinates) instead of drawn immediately.
				graphics.setTooltipForNextFrame(this.font, this.font.split(ratDesc, 200), mouseX - 4, mouseY - 12);
			}
		}
	}

	public boolean shouldRenderButtons() {
		return this.table.getCraftingTable().getPossibleRecipes().size() > 1;
	}

	private static class CycleResultButton extends Button {
		private final boolean up;

		public CycleResultButton(int x, int y, boolean up, OnPress onClick) {
			super(x, y, 14, 9, Component.empty(), onClick, message -> Component.empty());
			this.up = up;
		}

		// 26.1: renderWidget(GuiGraphics, ...) became extractContents(GuiGraphicsExtractor, ...); the visible check is handled by AbstractWidget.
		@Override
		protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
			if (Minecraft.getInstance().screen instanceof RatCraftingTableScreen table && !table.shouldRenderButtons())
				return;
			boolean hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

			int textureX = 22;
			int textureY = 211;

			if (hovered) textureX += this.width;

			if (!this.up) textureY += this.height;

			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), textureX, textureY, this.width, this.height, 256, 256);
		}
	}
}
