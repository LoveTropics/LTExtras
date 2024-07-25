package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.ClientCollectiblesList;
import com.lovetropics.extras.collectible.Collectible;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ClientboundCollectiblesListPacket(List<Collectible> collectibles, boolean silent, boolean hasUnseen) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCollectiblesListPacket> STREAM_CODEC = StreamCodec.composite(
            Collectible.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundCollectiblesListPacket::collectibles,
            ByteBufCodecs.BOOL, ClientboundCollectiblesListPacket::silent,
            ByteBufCodecs.BOOL, ClientboundCollectiblesListPacket::hasUnseen,
            ClientboundCollectiblesListPacket::new
    );

    public static final Type<ClientboundCollectiblesListPacket> TYPE = new Type<>(LTExtras.location("collectibles_list"));

    public static void handle(ClientboundCollectiblesListPacket packet, IPayloadContext context) {
        ClientCollectiblesList.get().update(packet.collectibles, packet.silent, packet.hasUnseen);
    }

    @Override
    public Type<ClientboundCollectiblesListPacket> type() {
        return TYPE;
    }
}
