package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.entity.ForkliftEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundLiftForkliftPacket(boolean eject, int forkHeight, int entityId) implements CustomPacketPayload {
    public static final Type<ServerboundLiftForkliftPacket> TYPE = new Type<>(LTExtras.location("lift_forklift"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundLiftForkliftPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundLiftForkliftPacket::eject,
            ByteBufCodecs.VAR_INT, ServerboundLiftForkliftPacket::forkHeight,
            ByteBufCodecs.VAR_INT, ServerboundLiftForkliftPacket::entityId,
            ServerboundLiftForkliftPacket::new
    );

    public static void handle(ServerboundLiftForkliftPacket packet, IPayloadContext ctx) {
        final int forkHeight = packet.forkHeight;

        final Level level = ctx.player().level();

        if (level.getEntity(packet.entityId) instanceof ForkliftEntity forklift) {
            int heightBefore = forklift.getForkHeight();
            int heightChanged = Mth.abs(forkHeight - heightBefore);
            forklift.setForkHeight(forkHeight);

            if (packet.eject) {
                for (final Entity passenger : forklift.getPassengers()) {
                    // Don't eject the controlling passenger
                    if (forklift.getControllingPassenger() != null && passenger.is(forklift.getControllingPassenger())) {
                        continue;
                    }

                    passenger.stopRiding();
                    passenger.hasImpulse = true;

                    float force = (float) heightChanged / ForkliftEntity.FORK_HEIGHT;
                    passenger.setDeltaMovement(0, level.random.triangle(force, 0.1f + force), 0);
                }
                level.playSound(forklift, forklift.blockPosition(), SoundEvents.BREEZE_JUMP, SoundSource.NEUTRAL, 1, 1);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
