package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.client.model.entity.AbstractRatModel;
import com.github.alexthe666.rats.client.model.entity.BiplaneModel;
import com.github.alexthe666.rats.client.render.RatsClientKeys;
import com.github.alexthe666.rats.client.render.RatsEntityModelBridge;
import com.github.alexthe666.rats.client.render.entity.layer.PartyHatLayer;
import com.github.alexthe666.rats.client.render.entity.layer.RatHeldItemLayer;
import com.github.alexthe666.rats.client.render.entity.layer.RatHelmetLayer;
import com.github.alexthe666.rats.registry.RatVariantRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.misc.RattlingGun;
import com.github.alexthe666.rats.server.entity.monster.boss.RatBaronPlane;
import com.github.alexthe666.rats.server.entity.mount.RatBiplaneMount;
import com.github.alexthe666.rats.server.entity.rat.AbstractRat;
import com.github.alexthe666.rats.server.items.HatItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractRatRenderer<T extends AbstractRat> extends MobRenderer<T, LivingEntityRenderState, RatsEntityModelBridge<T>> {

	public AbstractRatRenderer(EntityRendererProvider.Context context, AbstractRatModel<T> model) {
		super(context, new RatsEntityModelBridge<>(model), 0.15F);
		this.addLayer(new RatHelmetLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
		this.addLayer(new RatHeldItemLayer<>(this, context));
		this.addLayer(new PartyHatLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public boolean shouldRender(T rat, Frustum camera, double camX, double camY, double camZ) {
		if (rat.isPassenger() && rat.getVehicle() != null && !rat.getVehicle().getPassengers().isEmpty() && rat.getVehicle().getPassengers().get(0) == rat && rat.getVehicle() instanceof LivingEntity living) {
			if (living.getItemBySlot(EquipmentSlot.HEAD).is(RatsItemRegistry.CHEF_TOQUE.get())) {
				return false;
			}
		}
		return super.shouldRender(rat, camera, camX, camY, camZ);
	}

	@Override
	protected void scale(LivingEntityRenderState state, PoseStack stack) {
		stack.scale(0.6F, 0.6F, 0.6F);
		if (!(RatsClientKeys.getLiving(state) instanceof AbstractRat rat)) {
			return;
		}
		if (rat.isPassenger() && rat.getVehicle() != null && !rat.getVehicle().getPassengers().isEmpty()) {
			if (rat.getVehicle() instanceof Player player) {
				Entity riding = rat.getVehicle();
				if (riding.getPassengers().get(0) != null && riding.getPassengers().get(0) == rat) {
					EntityRenderer<?, ?> playerRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(riding);
					if (playerRender instanceof LivingEntityRenderer<?, ?, ?> renderer && renderer.getModel() instanceof HumanoidModel<?> human) {
						human.getHead().translateAndRotate(stack);
						stack.translate(0.0F, -0.7F, 0.25F);
						if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof HatItem hatItem) {
							stack.translate(0.0F, hatItem.getRatOffsetOnHead(), 0.0F);
						}
					}
				}
			}
			if (rat.getVehicle() instanceof RattlingGun) {
				Entity riding = rat.getVehicle();
				if (riding.getPassengers().get(0) != null && riding.getPassengers().get(0) == rat) {
					RattlingGunRenderer.GUN_MODEL.pivot.translateRotate(stack);
				}
			}
			if (rat.getVehicle() instanceof RatBaronPlane || rat.getVehicle() instanceof RatBiplaneMount) {
				Entity riding = rat.getVehicle();
				if (riding.getPassengers().get(0) != null && riding.getPassengers().get(0) == rat) {
					EntityRenderer<?, ?> planeRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(riding);
					// 26.1: the plane renderer's vanilla model is the bridge; unwrap it to reach the Citadel biplane mesh.
					if (planeRender instanceof LivingEntityRenderer<?, ?, ?> renderer && renderer.getModel() instanceof RatsEntityModelBridge<?> bridge && bridge.citadel() instanceof BiplaneModel<?> plane) {
						stack.translate(0.0F, -0.1F, 0.45F);
						plane.body1.translateRotate(stack);
					}
				}
			}
		}
	}

	@Override
	protected void setupRotations(LivingEntityRenderState state, PoseStack stack, float bodyRot, float entityScale) {
		// 26.1: getFlipDegrees() lost its entity parameter; suppress the death flip for rats dead in a trap here instead.
		if (RatsClientKeys.getLiving(state) instanceof AbstractRat rat && rat.isDeadInTrap()) {
			float deathTime = state.deathTime;
			state.deathTime = 0.0F;
			super.setupRotations(state, stack, bodyRot, entityScale);
			state.deathTime = deathTime;
		} else {
			super.setupRotations(state, stack, bodyRot, entityScale);
		}
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		if (RatsClientKeys.getLiving(state) instanceof AbstractRat rat) {
			return rat.getColorVariant().getTexture();
		}
		return RatVariantRegistry.BLUE.get().getTexture();
	}
}
