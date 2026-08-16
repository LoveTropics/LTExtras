package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.world_effect.EnvironmentAttributeEffectHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record ClientboundSetEnvironmentAttributePacket(Optional<EnvironmentAttributeMap> value, int fadeLength) implements CustomPacketPayload {

    private static final StreamCodec<ByteBuf, EnvironmentAttributeMap> MAP_STREAM_CODEC = ByteBufCodecs.fromCodec(EnvironmentAttributeMap.NETWORK_CODEC);

    public static final StreamCodec<ByteBuf, ClientboundSetEnvironmentAttributePacket> STREAM_CODEC = StreamCodec.composite(
            MAP_STREAM_CODEC.apply(ByteBufCodecs::optional), ClientboundSetEnvironmentAttributePacket::value,
            ByteBufCodecs.VAR_INT, ClientboundSetEnvironmentAttributePacket::fadeLength,
            ClientboundSetEnvironmentAttributePacket::new
    );

    public static final Type<ClientboundSetEnvironmentAttributePacket> TYPE = new Type<>(LTExtras.id("set_environment_attribute"));

    public static ClientboundSetEnvironmentAttributePacket clear(int fadeLength) {
        return new ClientboundSetEnvironmentAttributePacket(Optional.empty(), fadeLength);
    }

    public static void handle(ClientboundSetEnvironmentAttributePacket packet, IPayloadContext ctx) {
        packet.value.ifPresentOrElse(map -> EnvironmentAttributeEffectHandler.apply(map, packet.fadeLength), () -> EnvironmentAttributeEffectHandler.clear(packet.fadeLength));
    }

    @Override
    public Type<ClientboundSetEnvironmentAttributePacket> type() {
        return TYPE;
    }
}
