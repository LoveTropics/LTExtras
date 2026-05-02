package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.map.ClientMapManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundRemovePoiPacket(
        int id
) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRemovePoiPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundRemovePoiPacket::id,
            ClientboundRemovePoiPacket::new
    );

    public static final Type<ClientboundRemovePoiPacket> TYPE = new Type<>(LTExtras.id("remove_poi"));

    public static void handle(ClientboundRemovePoiPacket packet, IPayloadContext context) {
        ClientMapManager.removePoi(packet.id());
    }

    @Override
    public Type<ClientboundRemovePoiPacket> type() {
        return TYPE;
    }
}
