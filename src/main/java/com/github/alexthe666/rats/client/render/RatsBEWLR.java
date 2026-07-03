package com.github.alexthe666.rats.client.render;

import com.github.alexthe666.rats.server.block.entity.RatHoleBlockEntity;
import com.github.alexthe666.rats.server.items.RatsBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * 26.1: BlockEntityWithoutLevelRenderer + IClientItemExtensions#getCustomRenderer are gone; item custom
 * renderers are {@link SpecialModelRenderer}s reached through an {@link ItemModel}. {@link SpecialItemModel}
 * below is swapped in for the affected block items during {@code ModelEvent.ModifyBakingResult}.
 */
public class RatsBEWLR implements SpecialModelRenderer<ItemStack> {

	public static final RatsBEWLR INSTANCE = new RatsBEWLR();

	public RatsBEWLR() {
	}

	@Override
	@Nullable
	public ItemStack extractArgument(ItemStack stack) {
		return stack;
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		output.accept(new Vector3f(0.0F, 0.0F, 0.0F));
		output.accept(new Vector3f(1.0F, 1.0F, 1.0F));
	}

	@Override
	public void submit(@Nullable ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
		if (stack != null && stack.getItem() instanceof RatsBlockItem bi) {
			Block block = bi.getBlock();
			if (block instanceof EntityBlock be) {
				BlockEntity entity = be.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
				if (entity != null && !(entity instanceof RatHoleBlockEntity)) {
					// The dispatcher's renderItem path is gone; extract + submit through the BE renderer directly.
					entity.setLevel(Minecraft.getInstance().level);
					this.submitBlockEntity(entity, poseStack, collector);
				}
			}
		}
	}

	private <E extends BlockEntity, S extends BlockEntityRenderState> void submitBlockEntity(E entity, PoseStack poseStack, SubmitNodeCollector collector) {
		BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
		BlockEntityRenderer<E, S> renderer = dispatcher.getRenderer(entity);
		if (renderer != null) {
			S state = renderer.createRenderState();
			renderer.extractRenderState(entity, state, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true), Vec3.ZERO, null);
			renderer.submit(state, poseStack, collector, new CameraRenderState());
		}
	}

	/**
	 * Data-driven hook: registered as the {@code rats:block_entity} special model type in ModClientEvents,
	 * referenced from assets/rats/items/&lt;name&gt;.json via {@code {"type": "minecraft:special",
	 * "base": "rats:item/&lt;name&gt;", "model": {"type": "rats:block_entity"}}} — the base model supplies
	 * the display transforms the old builtin/entity models carried.
	 */
	public record Unbaked() implements SpecialModelRenderer.Unbaked<ItemStack> {
		public static final Unbaked INSTANCE = new Unbaked();
		public static final com.mojang.serialization.MapCodec<Unbaked> MAP_CODEC = com.mojang.serialization.MapCodec.unit(INSTANCE);

		@Override
		public SpecialModelRenderer<ItemStack> bake(SpecialModelRenderer.BakingContext context) {
			return RatsBEWLR.INSTANCE;
		}

		@Override
		public com.mojang.serialization.MapCodec<? extends SpecialModelRenderer.Unbaked<ItemStack>> type() {
			return MAP_CODEC;
		}
	}

	/**
	 * {@link ItemModel} bridging the model system to {@link RatsBEWLR}; substituted for each block-entity
	 * item's baked model in {@code ModelEvent.ModifyBakingResult}.
	 */
	public static final class SpecialItemModel implements ItemModel {
		public static final SpecialItemModel INSTANCE = new SpecialItemModel();

		private SpecialItemModel() {
		}

		@Override
		public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
			state.appendModelIdentityElement(SpecialItemModel.INSTANCE);
			// The BEWLR drew every frame; without this the render state caches geometry and skips animated BE renderers.
			state.setAnimated();
			ItemStackRenderState.LayerRenderState layer = state.newLayer();
			if (stack.hasFoil()) {
				layer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
				state.appendModelIdentityElement(ItemStackRenderState.FoilType.STANDARD);
			}
			layer.setExtents(ItemStackRenderState.LayerRenderState.NO_EXTENTS_SUPPLIER);
			layer.setupSpecialModel(RatsBEWLR.INSTANCE, stack);
			state.appendModelIdentityElement(stack);
		}
	}
}
