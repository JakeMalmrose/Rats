package com.github.alexthe666.rats.server.block.entity;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.github.alexthe666.rats.registry.RatsBlockEntityRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.inventory.RatCraftingTableMenu;
import com.github.alexthe666.rats.server.inventory.container.CraftingContainerWrapper;
import com.github.alexthe666.rats.server.inventory.container.TableItemHandlers;
import com.github.alexthe666.rats.server.misc.RatUpgradeUtils;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

// 1.21: dropped vanilla RecipeHolder *interface* (net.minecraft.world.inventory.RecipeHolder gone).
// Internally we now hold RecipeHolder<CraftingRecipe> records (item.crafting.RecipeHolder) so
// StackedContents.getBiggestCraftableStack and award-recipes calls keep working.
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unchecked", "unused"})
public class RatCraftingTableBlockEntity extends BlockEntity implements MenuProvider, Clearable {

	private static final Component DEFAULT_NAME = Component.translatable(RatsLangConstants.RAT_CRAFTING_TABLE);
	private Component customName;
	public int prevCookTime;
	private boolean hasRat;
	public boolean hasValidRecipe;
	private int cookTime;
	// 26.1: recipe matching uses StackedItemContents (StackedContents is now the generic raw counter).
	protected final StackedItemContents itemHelper = new StackedItemContents();
	protected Optional<RecipeHolder<CraftingRecipe>> guideRecipe = Optional.empty();
	protected Optional<RecipeHolder<CraftingRecipe>> recipeUsed = Optional.empty();
	protected List<RecipeHolder<CraftingRecipe>> possibleRecipes = List.of();
	public int totalCookTime = 200;
	private int selectedRecipeIndex = 0;
	private final ContainerData dataAccess = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case 0 -> RatCraftingTableBlockEntity.this.cookTime;
				case 1 -> RatCraftingTableBlockEntity.this.totalCookTime;
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case 0 -> RatCraftingTableBlockEntity.this.cookTime = value;
				case 1 -> RatCraftingTableBlockEntity.this.totalCookTime = value;
			}

		}

		public int getCount() {
			return 2;
		}
	};

	public final IItemHandlerModifiable bufferHandler = new TableItemHandlers.BufferHandler(this);
	public final IItemHandlerModifiable matrixHandler = new TableItemHandlers.MatrixHandler(this);
	public final IItemHandlerModifiable resultHandler = new TableItemHandlers.ResultHandler(this);

	protected final IItemHandlerModifiable combinedHandler =
			new CombinedInvWrapper(this.matrixHandler, this.bufferHandler);
	public final CraftingContainer matrixWrapper =
			new CraftingContainerWrapper(this.matrixHandler);

	public RatCraftingTableBlockEntity(BlockPos pos, BlockState state) {
		super(RatsBlockEntityRegistry.RAT_CRAFTING_TABLE.get(), pos, state);
	}

	// 26.1: replaces RatCraftingTableBlock.onRemove - drops the buffer contents and updates comparators when the block changes.
	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		Level level = this.getLevel();
		if (level != null) {
			for (int i = 0; i < this.bufferHandler.getSlots(); i++) {
				net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), this.bufferHandler.getStackInSlot(i));
			}
			level.updateNeighbourForOutputSignal(pos, state.getBlock());
		}
		super.preRemoveSideEffects(pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, RatCraftingTableBlockEntity te) {
		te.hasRat = false;
		te.totalCookTime = 200;

		for (TamedRat rat : level.getEntitiesOfClass(TamedRat.class, new AABB(pos.getX(), (double) pos.getY() + 1, pos.getZ(), (double) pos.getX() + 1, (double) pos.getY() + 2, (double) pos.getZ() + 1))) {
			if (RatUpgradeUtils.hasUpgrade(rat, RatsItemRegistry.RAT_UPGRADE_CRAFTING.get())) {
				te.hasRat = true;
				if (RatUpgradeUtils.hasUpgrade(rat, RatsItemRegistry.RAT_UPGRADE_SPEED.get())) {
					te.totalCookTime = 100;
				}
			}
		}

		if (!level.isClientSide()) {
			te.prevCookTime = te.cookTime;

			if (te.getRecipeUsed() != null && te.hasRat && te.cookTime < te.totalCookTime) {
				te.cookTime++;
			} else {
				te.cookTime = Mth.clamp(te.cookTime - 2, 0, te.totalCookTime);
			}
			if (te.cookTime >= te.totalCookTime) {
				te.cookTime = 0;
				// 26.1: Recipe.assemble takes only the input (no registry access).
				net.minecraft.world.item.crafting.CraftingInput craftingInput = makeCraftingInput(te.matrixWrapper);
				ItemStack addStack = te.recipeUsed.map(r -> r.value().assemble(craftingInput)).orElse(ItemStack.EMPTY);
				if (!addStack.isEmpty()) {
					IItemHandlerModifiable rh = te.resultHandler;
					rh.setStackInSlot(0, addStack.copyWithCount(addStack.getCount() + rh.getStackInSlot(0).getCount()));
					te.consumeIngredients(null);
				}
				te.updateRecipe();
			}
		}
	}

	public boolean hasRat() {
		return this.hasRat;
	}

	public int getCookTime() {
		return this.cookTime;
	}

	public void updateHelper() {
		((TableItemHandlers.BufferHandler) this.bufferHandler).fillStackedContents(this.itemHelper);
		this.checkIfRecipeIsValid(this.recipeUsed, this.itemHelper);
	}

	public void updateRecipe() {
		AtomicBoolean flag = new AtomicBoolean(true);
		// 26.1: recipes are server-only — Level.getRecipeManager is gone; ServerLevel.recipeAccess()
		// returns the RecipeManager and lookups go through its (Neo-exposed) RecipeMap.
		if (this.getLevel() instanceof ServerLevel serverLevel) {
			{
				net.minecraft.world.item.crafting.CraftingInput input = makeCraftingInput(this.matrixWrapper);
				this.possibleRecipes = serverLevel.recipeAccess().recipeMap()
						.getRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING, input, serverLevel)
						.toList();
				if (this.possibleRecipes.isEmpty()) {
					flag.set(false);
				} else {
					this.selectedRecipeIndex = Mth.clamp(this.selectedRecipeIndex, 0, this.possibleRecipes.size() - 1);
					this.guideRecipe = Optional.of(this.possibleRecipes.get(this.selectedRecipeIndex));
					if (!this.checkIfResultFits(this.guideRecipe, input)) {
						flag.set(false);
					}
					this.recipeUsed = Optional.of(this.possibleRecipes.get(this.selectedRecipeIndex))
							.filter(r -> this.setRecipeUsed(serverLevel, null, r));
				}
			}
			if (flag.get()) {
				this.checkIfRecipeIsValid(this.recipeUsed, this.itemHelper);
				if (!this.hasValidRecipe)
					this.setRecipeUsed((RecipeHolder<CraftingRecipe>) null);
			} else {
				this.guideRecipe = Optional.empty();
				this.setRecipeUsed((RecipeHolder<CraftingRecipe>) null);
			}
		}
	}

	// 26.1: Recipe.getResultItem(RegistryAccess) is gone; the recipe already matched this input, so
	// assembling it yields the concrete result stack to size-check against the output slot.
	private boolean checkIfResultFits(Optional<RecipeHolder<CraftingRecipe>> recipe, net.minecraft.world.item.crafting.CraftingInput input) {
		if (recipe.isPresent()) {
			ItemStack checkStack = this.resultHandler.getStackInSlot(0);
			ItemStack resultStack = recipe.get().value().assemble(input);
			return (ItemStack.isSameItemSameComponents(checkStack, resultStack) && checkStack.getCount() + resultStack.getCount() <= checkStack.getMaxStackSize()) || checkStack.isEmpty();
		}
		return false;
	}

	private void checkIfRecipeIsValid(Optional<RecipeHolder<CraftingRecipe>> recipe, StackedItemContents helper) {
		this.hasValidRecipe = recipe.isPresent() && helper.getBiggestCraftableStack(recipe.get().value(), null) > 0;
	}

	// 1.21: CraftingRecipe.assemble/getRemainingItems/getMatching take CraftingInput, not the legacy
	// CraftingContainer. This adapter wraps our matrix container into a CraftingInput so all those
	// recipe APIs type-check.
	private static net.minecraft.world.item.crafting.CraftingInput makeCraftingInput(net.minecraft.world.inventory.CraftingContainer container) {
		java.util.List<ItemStack> items = new java.util.ArrayList<>(container.getContainerSize());
		for (int i = 0; i < container.getContainerSize(); i++) {
			items.add(container.getItem(i));
		}
		return net.minecraft.world.item.crafting.CraftingInput.of(container.getWidth(), container.getHeight(), items);
	}

	@Override
	public void clearContent() {
		IItemHandlerModifiable h = this.combinedHandler;
		for (int i = 0; i < h.getSlots(); i++) {
			h.setStackInSlot(i, ItemStack.EMPTY);
		}
		this.updateRecipe();
	}

	public void incrementSelectedRecipe(boolean negative) {
		if (negative) {
			this.selectedRecipeIndex--;
		} else {
			this.selectedRecipeIndex++;
		}
		this.updateRecipe();
	}

	public void setRecipeUsed(@Nullable RecipeHolder<CraftingRecipe> recipe) {
		this.recipeUsed = Optional.ofNullable(recipe);
	}

	public boolean setRecipeUsed(Level level, @Nullable ServerPlayer player, RecipeHolder<CraftingRecipe> recipe) {
		// 26.1: game rules live on ServerLevel only; rule constant renamed to LIMITED_CRAFTING.
		return !(level instanceof ServerLevel serverLevel)
				|| !serverLevel.getGameRules().get(GameRules.LIMITED_CRAFTING)
				|| recipe.value().isSpecial();
	}

	@Nullable
	public RecipeHolder<CraftingRecipe> getRecipeUsed() {
		return this.recipeUsed.orElse(null);
	}

	public Optional<CraftingRecipe> getGuideRecipe() {
		return this.guideRecipe.map(RecipeHolder::value);
	}

	public List<RecipeHolder<CraftingRecipe>> getPossibleRecipes() {
		return this.possibleRecipes;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		this.updateRecipe();
	}

	@Override
	protected void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);
		// 26.1: handlers implement ValueIOSerializable (serialize/deserialize on Value IO children).
		((ValueIOSerializable) this.bufferHandler).deserialize(tag.childOrEmpty("Buffer"));
		((ValueIOSerializable) this.matrixHandler).deserialize(tag.childOrEmpty("Matrix"));
		((ValueIOSerializable) this.resultHandler).deserialize(tag.childOrEmpty("Result"));
		this.customName = parseCustomNameSafe(tag, "CustomName");
		this.cookTime = tag.getIntOr("CookTime", 0);
		this.selectedRecipeIndex = tag.getIntOr("SelectedRecipe", 0);
		// deserialize() no longer fires the handler's onLoad hook, so refill the crafting helper here.
		this.updateHelper();
	}

	@Override
	protected void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);
		((ValueIOSerializable) this.bufferHandler).serialize(tag.child("Buffer"));
		((ValueIOSerializable) this.matrixHandler).serialize(tag.child("Matrix"));
		((ValueIOSerializable) this.resultHandler).serialize(tag.child("Result"));
		if (this.hasCustomName()) {
			tag.store("CustomName", ComponentSerialization.CODEC, this.customName);
		}
		tag.putInt("CookTime", this.cookTime);
		tag.putInt("SelectedRecipe", this.selectedRecipeIndex);
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return this.hasCustomName() ? this.customName : DEFAULT_NAME;
	}

	public boolean hasCustomName() {
		return this.customName != null;
	}

	public void setCustomName(@Nullable Component name) {
		this.customName = name;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new RatCraftingTableMenu(id, inventory, this, this.dataAccess);
	}

	public IItemHandlerModifiable itemHandler(@Nullable Direction side) {
		if (this.remove) return null;
		return side == Direction.DOWN ? this.resultHandler : this.bufferHandler;
	}

	public void consumeIngredients(@Nullable Player player) {
		// Skip the entire pass when the recipe is unusable. getRemainingItems can read the matrix
		// state and produce inconsistent container items if we run it after the matrix has already
		// been mutated by a prior tick.
		if (!this.hasValidRecipe) return;
		this.recipeUsed.ifPresent(holder -> {
			CraftingRecipe recipe = holder.value();
			NonNullList<ItemStack> remainingStacks = recipe.getRemainingItems(makeCraftingInput(this.matrixWrapper));

			if (this.hasValidRecipe) {
				IItemHandlerModifiable h = this.bufferHandler;
				// 26.1: getIngredients() is gone; placementInfo().ingredients() is the non-empty ingredient list.
				recipe.placementInfo().ingredients().forEach(i -> {
					for (int j = 0; j < h.getSlots(); j++) {
						if (i.test(h.getStackInSlot(j))) {
							h.extractItem(j, 1, false);
							break;
						}
					}
				});
			}

			// Handle container items
			IntStream.range(0, remainingStacks.size())
					.mapToObj(i -> {
						ItemStack stack = remainingStacks.get(i);
						return this.hasValidRecipe ? stack : this.matrixHandler.insertItem(i, stack, false);
					}) // Insert back the corresponding matrix slot if crafted from there
					.filter(stack -> !stack.isEmpty())
					.map(stack -> ItemHandlerHelper.insertItemStacked(this.bufferHandler, stack, false))
					.filter(stack -> !stack.isEmpty())
					.forEach(stack -> {
						if (player != null) {
							ItemHandlerHelper.giveItemToPlayer(player, stack);
						} else {
							this.outputStack(stack);
						}
					});
		});
	}

	private void outputStack(ItemStack stack) {
		ItemStack newStack = ItemHandlerHelper.insertItemStacked(this.bufferHandler, stack, false);

		if (!newStack.isEmpty() && this.getLevel() != null) {
			ItemEntity item = new ItemEntity(this.getLevel(), this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() + 1.0D, this.getBlockPos().getZ() + 0.5F, newStack);
			item.setDeltaMovement(Vec3.ZERO);
			item.setNoPickUpDelay();
			item.setExtendedLifetime();
			this.getLevel().addFreshEntity(item);
		}
	}
}
