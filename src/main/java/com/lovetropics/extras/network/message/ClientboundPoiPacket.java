package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.ClientMapPoiManager;
import com.lovetropics.extras.data.poi.Poi;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundPoiPacket(Poi poi, boolean delete) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPoiPacket> STREAM_CODEC = StreamCodec.composite(
            Poi.STREAM_CODEC, ClientboundPoiPacket::poi,
            ByteBufCodecs.BOOL, ClientboundPoiPacket::delete,
            ClientboundPoiPacket::new
    );

    public static final Type<ClientboundPoiPacket> TYPE = new Type<>(LTExtras.location("poi"));

    public static void handle(final ClientboundPoiPacket packet, final IPayloadContext context) {
        ClientMapPoiManager.updatePoi(packet.poi(), packet.delete());
    }

    @Override
    public Type<ClientboundPoiPacket> type() {
        return TYPE;
    }
}
