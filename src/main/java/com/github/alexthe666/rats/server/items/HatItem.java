package com.github.alexthe666.rats.server.items;

import net.minecraft.core.registries.BuiltInRegistries;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.model.RatsModelLayers;
import com.github.alexthe666.rats.client.model.hats.*;
import com.github.alexthe666.rats.registry.RatlantisItemRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.monster.GhostPirat;
import com.github.alexthe666.rats.server.entity.rat.AbstractRat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

// 26.1: ArmorItem is gone — armor is a plain Item with Properties.humanoidArmor(material, type),
// which applies durability, attributes, enchantability, repair tag and the EQUIPPABLE component.
public class HatItem extends Item {

	private final int loreLines;

	public HatItem(Item.Properties properties, ArmorMaterial material, int loreLines) {
		super(properties.humanoidArmor(material, ArmorType.HELMET));
		this.loreLines = loreLines;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		if (stack.is(RatsItemRegistry.BLACK_DEATH_MASK.get())) {
			tooltip.accept(Component.translatable("item.rats.plague_doctor_mask.desc").withStyle(ChatFormatting.GRAY));
		}
		if (this.loreLines > 0) {
			for (int i = 0; i < this.loreLines; i++) {
				tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc" + (this.loreLines == 1 ? "" : i)).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	@Override
	public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
		return stack.is(RatsItemRegistry.RAT_KING_CROWN.get());
	}

	// 26.1: IItemExtension#isEnderMask was generalized into isGazeDisguise.
	@Override
	public boolean isGazeDisguise(ItemStack stack, Player player, @Nullable LivingEntity entity) {
		return stack.is(RatsItemRegistry.BLACK_DEATH_MASK.get()) || stack.is(RatsItemRegistry.PLAGUE_DOCTOR_MASK.get());
	}

	/**
	 * Override this to add a custom transformation to the hat when on a rat's head.
	 *
	 * @param rat   the rat to render the hat on
	 * @param stack a PoseStack to allow translation, rotation, and scaling
	 */
	public void transformOnHead(AbstractRat rat, PoseStack stack) {
		if (this == RatsItemRegistry.CHEF_TOQUE.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(25.0F));
			stack.translate(0.0F, 0.1F, 0.3F);
		}
		if (this == RatsItemRegistry.PIPER_HAT.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(10.0F));
			stack.translate(0.0F, 0.0F, 0.1F);
		}
		if (this == RatsItemRegistry.PIRAT_HAT.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(5.0F));
			stack.translate(0.0F, -0.125F, 0.0F);
			stack.scale(1.425F, 1.425F, 1.425F);
		}
		if (this == RatlantisItemRegistry.GHOST_PIRAT_HAT.get()) {
			float piratScale = rat instanceof GhostPirat ? 1.1F : 1.425F;
			float piratTranslate = rat instanceof GhostPirat ? 0.05F : -0.125F;
			// 26.1: RenderSystem.setShaderColor is gone with the global shader-color state; the ghostly
			// tint now has to come from the render layer's buffer/color, so the old 1.3F alpha boost is dropped.
			stack.mulPose(Axis.XN.rotationDegrees(5.0F));
			stack.translate(0.0F, piratTranslate, 0.0F);
			stack.scale(piratScale, piratScale, piratScale);
		}
		if (this == RatsItemRegistry.ARCHEOLOGIST_HAT.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(5.0F));
			stack.translate(0.0F, -0.1F, 0.0F);
			stack.scale(1.425F, 1.425F, 1.425F);
		}
		if (this == RatsItemRegistry.FARMER_HAT.get() || this == RatsItemRegistry.FISHERMAN_HAT.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(5.0F));
			stack.translate(0.0F, -0.1F, 0.0F);
			stack.scale(1.425F, 1.425F, 1.425F);
		}
		if (this == RatsItemRegistry.PLAGUE_DOCTOR_MASK.get() || this == RatsItemRegistry.BLACK_DEATH_MASK.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(15.0F));
			stack.translate(0.0F, -0.1F, 0.0F);
			stack.scale(1.5F, 1.2F, 1.5F);
		}
		if (this == RatsItemRegistry.RAT_FEZ.get()) {
			stack.translate(-0.05F, -0.15F, -0.1F);
			stack.scale(1.425F, 1.425F, 1.425F);
		}
		if (this == RatsItemRegistry.TOP_HAT.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(5.0F));
			stack.translate(0.0F, -0.125F, 0.0F);
			stack.scale(1.425F, 1.425F, 1.425F);
		}
		if (this == RatsItemRegistry.SANTA_HAT.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(5.0F));
			stack.translate(0.0F, 0.0F, 0.1F);
			stack.scale(1.25F, 1.25F, 1.25F);
		}
		if (this == RatlantisItemRegistry.MILITARY_HAT.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(5.0F));
			stack.translate(0.0F, -0.1F, 0.0F);
			stack.scale(1.425F, 1.425F, 1.425F);
		}
		if (this == RatsItemRegistry.RAT_KING_CROWN.get()) {
			stack.mulPose(Axis.XN.rotationDegrees(5.0F));
			stack.translate(0.0F, -0.05F, 0.0F);
			stack.scale(1.25F, 1.25F, 1.25F);
		}
		if (this == RatlantisItemRegistry.AVIATOR_HAT.get()) {
			stack.scale(1.25F, 1.25F, 1.25F);
			stack.translate(0, -0.035F, 0.01F);
		}
	}

	public float getRatOffsetOnHead() {
		if (this == RatsItemRegistry.TOP_HAT.get()) {
			return -0.85F;
		} else if (this == RatlantisItemRegistry.MILITARY_HAT.get() || this == RatlantisItemRegistry.GHOST_PIRAT_HAT.get() || this == RatsItemRegistry.PIRAT_HAT.get()) {
			return -0.45F;
		}
		return 0.0F;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		// A separate @OnlyIn class (not an anonymous one) keeps client-only types out of this
		// class's constant pool so it stays loadable on a dedicated server.
		consumer.accept(new ClientExtensions(this));
	}

	@OnlyIn(Dist.CLIENT)
	private static final class ClientExtensions implements IClientItemExtensions {
		private final HatItem hat;

		ClientExtensions(HatItem hat) {
			this.hat = hat;
		}

		// 26.1: vanilla derives armor textures from equipment assets; the per-stack hook moved from
		// IItemExtension to IClientItemExtensions. We use it to point each hat at its real texture
		// under model/hat/, regardless of which shared ArmorMaterial (e.g. GENERIC_HAT) it registered with.
		@Override
		public Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, Identifier _default) {
			String item = BuiltInRegistries.ITEM.getKey(this.hat).getPath();
			if (!item.equals("air")) {
				return Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/model/hat/" + item + ".png");
			}
			return Identifier.withDefaultNamespace("textures/particle/flea_0.png");
		}

		@Override
		public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
			EntityModelSet models = Minecraft.getInstance().getEntityModels();
			return switch (BuiltInRegistries.ITEM.getKey(this.hat).getPath()) {
				case "chef_toque" -> new ChefToqueModel(models.bakeLayer(RatsModelLayers.CHEF_TOQUE));
				case "piper_hat" -> new PiperHatModel(models.bakeLayer(RatsModelLayers.PIPER_HAT));
				case "archeologist_hat" -> new ArcheologistHatModel(models.bakeLayer(RatsModelLayers.ARCHEOLOGIST_HAT));
				case "farmer_hat", "fisherman_hat" -> new FarmerHatModel(models.bakeLayer(RatsModelLayers.FARMER_HAT));
				case "rat_fez" -> new RatFezModel(models.bakeLayer(RatsModelLayers.FEZ));
				case "top_hat" -> new TopHatModel(models.bakeLayer(RatsModelLayers.TOP_HAT));
				case "santa_hat" -> new SantaHatModel(models.bakeLayer(RatsModelLayers.SANTA_HAT));
				case "halo_hat" -> new HaloHatModel(models.bakeLayer(RatsModelLayers.HALO));
				case "pirat_hat" -> new PiratHatModel(models.bakeLayer(RatsModelLayers.PIRATE_HAT));
				case "rat_king_crown" -> new CrownModel(models.bakeLayer(RatsModelLayers.CROWN));
				case "plague_doctor_mask", "black_death_mask" -> new PlagueDoctorMaskModel(models.bakeLayer(RatsModelLayers.PLAGUE_DOCTOR_MASK));
				case "exterminator_hat" -> new ExterminatorHatModel(models.bakeLayer(RatsModelLayers.EXTERMINATOR_HAT));
				case "aviator_hat" -> new AviatorHatModel(models.bakeLayer(RatsModelLayers.AVIATOR_HAT));
				case "ghost_pirat_hat" -> new GhostPiratHatModel(models.bakeLayer(RatsModelLayers.PIRATE_HAT));
				case "military_hat" -> new MilitaryHatModel(models.bakeLayer(RatsModelLayers.OFFICER_HAT));
				default -> original;
			};
		}
	}
}
