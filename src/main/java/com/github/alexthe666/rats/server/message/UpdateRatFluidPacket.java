package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public record UpdateRatFluidPacket(int ratId, FluidStack fluid) implements CustomPacketPayload {

    public static final Type<UpdateRatFluidPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(RatsMod.MODID, "update_rat_fluid"));

    // This packet also syncs the emptied state after a deposit (RatDepositGoal sends FluidStack.EMPTY),
    // and FluidStack.STREAM_CODEC throws EncoderException on empty stacks, kicking every client.
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRatFluidPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, UpdateRatFluidPacket::ratId,
                FluidStack.OPTIONAL_STREAM_CODEC, UpdateRatFluidPacket::fluid,
                UpdateRatFluidPacket::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateRatFluidPacket packet, IPayloadContext context) {
        			context.enqueueWork(() -> {
        				Player player = context.player();
        				if (player != null) {
        					Entity entity = player.level().getEntity(packet.ratId());
        					if (entity instanceof TamedRat rat) {
        						rat.transportingFluid = packet.fluid();

        					}
        				}
        			});
			
    }
}
