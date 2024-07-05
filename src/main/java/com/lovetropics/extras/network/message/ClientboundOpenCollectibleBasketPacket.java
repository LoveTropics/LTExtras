package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.ClientCollectiblesList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientboundOpenCollectibleBasketPacket implements CustomPacketPayload {
    public static final Type<ClientboundOpenCollectibleBasketPacket> TYPE = new Type<>(LTExtras.location("open_collectible"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenCollectibleBasketPacket> STREAM_CODEC = StreamCodec.unit(new ClientboundOpenCollectibleBasketPacket());

    public static void handle(ClientboundOpenCollectibleBasketPacket packet, IPayloadContext ctx) {
        ClientCollectiblesList.openScreen();
    }

    @Override
    public Type<ClientboundOpenCollectibleBasketPacket> type() {
        return TYPE;
    }
}
