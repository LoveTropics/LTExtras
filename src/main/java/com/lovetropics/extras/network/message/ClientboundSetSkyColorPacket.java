package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.world_effect.SkyColorEffectHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSetSkyColorPacket(int color, int fadeLength) implements CustomPacketPayload {
    private static final int CLEAR = Integer.MIN_VALUE;

    public static final StreamCodec<ByteBuf, ClientboundSetSkyColorPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSetSkyColorPacket::color,
            ByteBufCodecs.VAR_INT, ClientboundSetSkyColorPacket::fadeLength,
            ClientboundSetSkyColorPacket::new
    );

    public static final Type<ClientboundSetSkyColorPacket> TYPE = new Type<>(LTExtras.location("set_sky_color"));

    public static ClientboundSetSkyColorPacket clear(final int fadeLength) {
        return new ClientboundSetSkyColorPacket(CLEAR, fadeLength);
    }

    public static void handle(final ClientboundSetSkyColorPacket packet, final IPayloadContext ctx) {
        if (packet.color == CLEAR) {
            SkyColorEffectHandler.clear(packet.fadeLength);
        } else {
            SkyColorEffectHandler.apply(packet.color, packet.fadeLength);
        }
    }

    @Override
    public Type<ClientboundSetSkyColorPacket> type() {
        return TYPE;
    }
}
