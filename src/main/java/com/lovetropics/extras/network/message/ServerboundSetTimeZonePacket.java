package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.schedule.PlayerTimeZone;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;

public record ServerboundSetTimeZonePacket(ZoneId id) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, ServerboundSetTimeZonePacket> STREAM_CODEC = ByteBufCodecs.stringUtf8(64).map(
            id -> {
                try {
                    return new ServerboundSetTimeZonePacket(ZoneId.of(id));
                } catch (final DateTimeException e) {
                    return new ServerboundSetTimeZonePacket(ZoneOffset.UTC);
                }
            },
            packet -> packet.id.getId()
    );

    public static final Type<ServerboundSetTimeZonePacket> TYPE = new Type<>(LTExtras.location("set_time_zone"));

    public static void handle(final ServerboundSetTimeZonePacket packet, final IPayloadContext ctx) {
        final ServerPlayer player = (ServerPlayer) ctx.player();
        PlayerTimeZone.set(player, packet.id());
    }

    @Override
    public Type<ServerboundSetTimeZonePacket> type() {
        return TYPE;
    }
}
