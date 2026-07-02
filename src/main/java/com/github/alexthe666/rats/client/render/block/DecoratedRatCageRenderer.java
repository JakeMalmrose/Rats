package com.github.alexthe666.rats.client.render.block;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.RatsModelLayers;
import com.github.alexthe666.rats.client.model.deco.*;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.block.RatCageDecoratedBlock;
import com.github.alexthe666.rats.server.block.entity.DecoratedRatCageBlockEntity;
import com.github.alexthe666.rats.server.block.entity.RatCageBreedingLanternBlockEntity;
import com.github.alexthe666.rats.server.block.entity.RatCageWheelBlockEntity;
import com.github.alexthe666.rats.server.items.RatHammockItem;
import com.github.alexthe666.rats.server.items.RatIglooItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class DecoratedRatCageRenderer implements BlockEntityRenderer<DecoratedRatCageBlockEntity, DecoratedRatCageRenderer.DecoratedRatCageRenderState> {
	private final RatIglooModel igloo;
	private final RatHammockModel hammock;
	private final RatWaterBottleModel water_bottle;
	private final RatSeedBowlModel seed_bowl;
	private static final RatBreedingLanternModel<?> MODEL_RAT_BREEDING_LANTERN = new RatBreedingLanternModel<>();
	private static final RatWheelModel<?> MODEL_RAT_WHEEL = new RatWheelModel<>();
	private static final RenderType TEXTURE_RAT_IGLOO = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/rat_igloo.png"));
	private static final RenderType TEXTURE_RAT_HAMMOCK = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/rat_hammock_0.png"));
	private static final RenderType TEXTURE_RAT_WATER_BOTTLE = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/rat_water_bottle.png"));
	private static final RenderType TEXTURE_RAT_SEED_BOWL = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/rat_seed_bowl.png"));
	private static final RenderType TEXTURE_RAT_BREEDING_LANTERN = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/rat_breeding_lantern.png"));
	private static final RenderType TEXTURE_RAT_WHEEL = RenderTypes.entityTranslucent(Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/block/rat_wheel.png"));

	private final PoseStack scratch = new PoseStack();

	public static final class DecoratedRatCageRenderState extends BlockEntityRenderState {
		public DecoratedRatCageBlockEntity entity;
		public float partialTick;
		public float rotation;
		public ItemStack containedItem = ItemStack.EMPTY;
	}

	public DecoratedRatCageRenderer(BlockEntityRendererProvider.Context context) {
		this.igloo = new RatIglooModel(context.bakeLayer(RatsModelLayers.IGLOO));
		this.hammock = new RatHammockModel(context.bakeLayer(RatsModelLayers.HAMMOCK));
		this.water_bottle = new RatWaterBottleModel(context.bakeLayer(RatsModelLayers.WATER_BOTTLE));
		this.seed_bowl = new RatSeedBowlModel(context.bakeLayer(RatsModelLayers.SEED_BOWL));
	}

	@Override
	public DecoratedRatCageRenderState createRenderState() {
		return new DecoratedRatCageRenderState();
	}

	@Override
	public void extractRenderState(DecoratedRatCageBlockEntity entity, DecoratedRatCageRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPos, breakProgress);
		state.entity = entity;
		state.partialTick = partialTicks;
		state.rotation = entity.getBlockState().getValue(RatCageDecoratedBlock.FACING).getOpposite().toYRot();
		state.containedItem = entity.getLevel() != null ? entity.getContainedItem() : ItemStack.EMPTY;
	}

	@Override
	public void submit(DecoratedRatCageRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
		int light = state.lightCoords;
		int overlay = OverlayTexture.NO_OVERLAY;
		stack.pushPose();
		stack.translate(0.5D, 1.5D, 0.5D);
		stack.mulPose(Axis.ZP.rotationDegrees(180));
		stack.mulPose(Axis.YP.rotationDegrees(state.rotation));
		ItemStack containedItem = state.containedItem;
		if (containedItem.getItem() instanceof RatIglooItem iglooItem) {
			DyeColor color = iglooItem.color;
			collector.submitModel(this.igloo, Unit.INSTANCE, stack, TEXTURE_RAT_IGLOO, light, overlay, color.getTextureDiffuseColor() | 0xFF000000, null, 0, state.breakProgress);
		}

		if (containedItem.getItem() instanceof RatHammockItem hammockItem) {
			DyeColor color = hammockItem.color;
			collector.submitModel(this.hammock, Unit.INSTANCE, stack, TEXTURE_RAT_HAMMOCK, light, overlay, color.getTextureDiffuseColor() | 0xFF000000, null, 0, state.breakProgress);
		}

		if (containedItem.is(RatsItemRegistry.RAT_WATER_BOTTLE.get())) {
			collector.submitModel(this.water_bottle, Unit.INSTANCE, stack, TEXTURE_RAT_WATER_BOTTLE, light, overlay, 0xFFFFFFFF, null, 0, state.breakProgress);
		}

		if (containedItem.is(RatsItemRegistry.RAT_SEED_BOWL.get())) {
			collector.submitModel(this.seed_bowl, Unit.INSTANCE, stack, TEXTURE_RAT_SEED_BOWL, light, overlay, 0xFFFFFFFF, null, 0, state.breakProgress);
		}

		if (containedItem.is(RatsItemRegistry.RAT_WHEEL.get())) {
			if (state.entity instanceof RatCageWheelBlockEntity wheel) {
				MODEL_RAT_WHEEL.animate(wheel, state.partialTick);
			}

			collector.submitCustomGeometry(stack, TEXTURE_RAT_WHEEL, (pose, consumer) ->
					RatsRenderType.withSubmitPose(pose, this.scratch, s ->
							MODEL_RAT_WHEEL.renderToBuffer(s, consumer, light, overlay, 0xFFFFFFFF)));
		}

		if (containedItem.is(RatsItemRegistry.RAT_BREEDING_LANTERN.get()) && state.entity instanceof RatCageBreedingLanternBlockEntity lantern) {
			float brightness = lantern.getBreedingCooldown() > 0 ? 0.5F : 1.0F;
			int channel = (int) (brightness * 255.0F);
			int tint = ARGB.color(255, channel, channel, channel);
			MODEL_RAT_BREEDING_LANTERN.swingChain();
			collector.submitCustomGeometry(stack, TEXTURE_RAT_BREEDING_LANTERN, (pose, consumer) ->
					RatsRenderType.withSubmitPose(pose, this.scratch, s ->
							MODEL_RAT_BREEDING_LANTERN.renderToBuffer(s, consumer, light, overlay, tint)));
		}
		stack.popPose();
	}
}
