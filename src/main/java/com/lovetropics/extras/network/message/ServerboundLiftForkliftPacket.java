package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.block.entity.ForkliftEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundLiftForkliftPacket(int forkHeight, int entityId) implements CustomPacketPayload {
    public static final Type<ServerboundLiftForkliftPacket> TYPE = new Type<>(LTExtras.location("lift_forklift"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundLiftForkliftPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundLiftForkliftPacket::forkHeight,
            ByteBufCodecs.VAR_INT, ServerboundLiftForkliftPacket::entityId,
            ServerboundLiftForkliftPacket::new
    );

    public static void handle(ServerboundLiftForkliftPacket packet, IPayloadContext ctx) {
        final int forkHeight = packet.forkHeight;

        final Level level = ctx.player().level();

        if (level.getEntity(packet.entityId) instanceof ForkliftEntity forklift) {
            forklift.setForkHeight(forkHeight);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
