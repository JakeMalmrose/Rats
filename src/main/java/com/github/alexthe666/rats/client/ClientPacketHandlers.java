package com.github.alexthe666.rats.client;

import com.github.alexthe666.rats.client.gui.CheeseStaffScreen;
import com.github.alexthe666.rats.client.gui.PatrolStaffScreen;
import com.github.alexthe666.rats.client.gui.RadiusStaffScreen;
import com.github.alexthe666.rats.client.gui.RatScreen;
import com.github.alexthe666.rats.client.util.RatRecordSoundInstance;
import com.github.alexthe666.rats.server.block.entity.AutoCurdlerBlockEntity;
import com.github.alexthe666.rats.server.capability.SelectedRat;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.inventory.RatMenu;
import com.github.alexthe666.rats.server.message.ManageRatStaffPacket;
import com.github.alexthe666.rats.server.message.OpenRatScreenPacket;
import com.github.alexthe666.rats.server.message.SyncPlaguePacket;
import com.github.alexthe666.rats.server.message.UpdateCurdlerFluidPacket;
import com.github.alexthe666.rats.server.message.UpdateRatMusicPacket;
import com.github.alexthe666.rats.server.message.UpdateSelectedRatPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

// Client-side packet handling lives here (instead of inside the packet classes) so that packet
// classes never reference client-only types — loading them on a dedicated server would crash.
public final class ClientPacketHandlers {

	private ClientPacketHandlers() {
	}

	public static void handleManageRatStaff(ManageRatStaffPacket packet) {
		Minecraft mc = Minecraft.getInstance();
		if (packet.clear()) {
			SelectedRat.clear(mc.player);
		} else if (mc.player.level().getEntity(packet.entityId()) instanceof TamedRat rat && packet.openGUI()) {
			switch (packet.staffToOpen()) {
				case 1 -> mc.setScreen(new RadiusStaffScreen(rat, packet.pos()));
				case 2 -> mc.setScreen(new PatrolStaffScreen(rat, packet.pos()));
				default -> mc.setScreen(new CheeseStaffScreen(rat, packet.pos(), Direction.values()[packet.dirOrd()]));
			}
		}
	}

	public static void handleOpenRatScreen(OpenRatScreenPacket packet) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.level.getEntity(packet.entityId()) instanceof TamedRat rat) {
			LocalPlayer localplayer = mc.player;
			RatMenu menu;
			if (localplayer.containerMenu instanceof RatMenu existing && existing.containerId == packet.containerId()) {
				menu = existing;
			} else {
				menu = new RatMenu(packet.containerId(), new SimpleContainer(6), localplayer.getInventory());
				localplayer.containerMenu = menu;
			}
			mc.setScreen(new RatScreen(menu, localplayer.getInventory(), rat));
		}
	}

	public static void handleSyncPlague(SyncPlaguePacket packet) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.level.getEntity(packet.entityId()) instanceof LivingEntity living) {
			Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.get(packet.effectId()).map(h -> (Holder<MobEffect>) h).orElse(null);
			if (effect != null) {
				if (packet.duration() == 0) {
					living.removeEffect(effect);
				} else {
					MobEffectInstance instance = new MobEffectInstance(effect, packet.duration(), packet.amplifier(), packet.isEffectAmbient(), packet.isEffectVisible(), packet.effectShowsIcon());
					living.forceAddEffect(instance, null);
				}
			}
		}
	}

	public static void handleUpdateCurdlerFluid(UpdateCurdlerFluidPacket packet) {
		Minecraft mc = Minecraft.getInstance();
		BlockPos pos = BlockPos.of(packet.blockPos());
		Level level = mc.level;
		if (level != null && level.getBlockEntity(pos) instanceof AutoCurdlerBlockEntity curdler) {
			curdler.getTank().setFluid(packet.fluid());
		}
	}

	public static void handleUpdateRatMusic(UpdateRatMusicPacket packet) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.level.getEntity(packet.id()) instanceof TamedRat rat) {
			mc.getSoundManager().queueTickingSound(new RatRecordSoundInstance(rat, packet.record().value()));
			mc.gui.setNowPlaying(packet.record().value().getName(new net.minecraft.world.item.ItemStack(packet.record().value())));
		}
	}

	public static void handleUpdateSelectedRat(UpdateSelectedRatPacket packet) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.level.getEntity(packet.entityId()) instanceof LivingEntity living) {
			SelectedRat.setLocal(living, packet.ratId());
		}
	}
}
