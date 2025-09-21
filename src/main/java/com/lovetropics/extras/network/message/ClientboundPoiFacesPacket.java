package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.map.ClientMapManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public record ClientboundPoiFacesPacket(
        int id,
        List<UUID> faces
) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPoiFacesPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundPoiFacesPacket::id,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundPoiFacesPacket::faces,
            ClientboundPoiFacesPacket::new
    );

    public static final Type<ClientboundPoiFacesPacket> TYPE = new Type<>(LTExtras.location("poi_faces"));

    public static void handle(ClientboundPoiFacesPacket packet, IPayloadContext context) {
        ClientMapManager.updateFaces(packet.id(), packet.faces());
    }

    @Override
    public Type<ClientboundPoiFacesPacket> type() {
        return TYPE;
    }
}
