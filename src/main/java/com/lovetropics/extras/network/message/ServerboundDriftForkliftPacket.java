package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.entity.ForkliftEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundDriftForkliftPacket(boolean drift, int entityId) implements CustomPacketPayload {
    public static final Type<ServerboundDriftForkliftPacket> TYPE = new Type<>(LTExtras.location("drift_forklift"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundDriftForkliftPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundDriftForkliftPacket::drift,
            ByteBufCodecs.VAR_INT, ServerboundDriftForkliftPacket::entityId,
            ServerboundDriftForkliftPacket::new
    );

    public static void handle(ServerboundDriftForkliftPacket packet, IPayloadContext ctx) {
        final Level level = ctx.player().level();

        if (level.getEntity(packet.entityId) instanceof ForkliftEntity forklift) {
            forklift.setDrifting(packet.drift);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
