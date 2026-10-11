package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.zipline.ZiplineEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundRideZiplinePacket(int entityId) implements CustomPacketPayload {
    public static final Type<ServerboundRideZiplinePacket> TYPE = new Type<>(LTExtras.id("ride_zipline"));
    public static final StreamCodec<ByteBuf, ServerboundRideZiplinePacket> STREAM_CODEC = ByteBufCodecs.VAR_INT
            .map(ServerboundRideZiplinePacket::new, ServerboundRideZiplinePacket::entityId);

    public static void handle(ServerboundRideZiplinePacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player && !player.isPassenger() && !player.isSpectator()) {
            ZiplineEntity.PickResult picked = ZiplineEntity.pickHit(player);
            if (picked != null && picked.cable().getId() == packet.entityId()) {
                picked.cable().boardFromCable(player, picked.progress());
            }
        }
    }

    @Override
    public Type<ServerboundRideZiplinePacket> type() {
        return TYPE;
    }
}
