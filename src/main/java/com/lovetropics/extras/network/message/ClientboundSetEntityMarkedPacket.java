package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.ClientPlayerSensorEffects;
import com.lovetropics.extras.item.sensor.PlayerSensor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record ClientboundSetEntityMarkedPacket(
        int entityId,
        Optional<PlayerSensor.Appearance> appearance
) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetEntityMarkedPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundSetEntityMarkedPacket::entityId,
            PlayerSensor.Appearance.STREAM_CODEC.apply(ByteBufCodecs::optional), ClientboundSetEntityMarkedPacket::appearance,
            ClientboundSetEntityMarkedPacket::new
    );

    public static final Type<ClientboundSetEntityMarkedPacket> TYPE = new Type<>(LTExtras.id("set_entity_marked"));

    public static void handle(ClientboundSetEntityMarkedPacket packet, IPayloadContext context) {
        if (packet.appearance.isPresent()) {
            ClientPlayerSensorEffects.mark(packet.entityId, packet.appearance.get());
        } else {
            ClientPlayerSensorEffects.clear(packet.entityId);
        }
    }

    @Override
    public Type<ClientboundSetEntityMarkedPacket> type() {
        return TYPE;
    }
}
