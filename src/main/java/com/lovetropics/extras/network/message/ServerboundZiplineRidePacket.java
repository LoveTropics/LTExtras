package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.zipline.ZiplineIndex;
import com.lovetropics.extras.zipline.ZiplineRide;
import com.lovetropics.extras.zipline.ZiplineRider;
import com.lovetropics.extras.zipline.ZiplineSegment;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

/**
 * Tells the server which rope the player is riding on, or that they left the ziplin.
 */
public record ServerboundZiplineRidePacket(Optional<Segment> segment) implements CustomPacketPayload {
    public static final Type<ServerboundZiplineRidePacket> TYPE = new Type<>(LTExtras.id("zipline_ride"));
    public static final StreamCodec<ByteBuf, ServerboundZiplineRidePacket> STREAM_CODEC = ByteBufCodecs.optional(Segment.STREAM_CODEC)
            .map(ServerboundZiplineRidePacket::new, ServerboundZiplineRidePacket::segment);

    public static void handle(ServerboundZiplineRidePacket packet, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        ZiplineSegment segment = packet.segment
                .map(s -> ZiplineIndex.get(player.level()).getSegment(s.from(), s.to()))
                .orElse(null);
        if (segment == null || !ZiplineRider.isNearRope(player, segment)) {
            ZiplineRider.stopRiding(player);
            return;
        }
        // Only the segment matters here, as the player's client simulates the ride itself
        ZiplineRider.startRiding(player, new ZiplineRide(segment, 0.0, 0.0, 1));
    }

    @Override
    public Type<ServerboundZiplineRidePacket> type() {
        return TYPE;
    }

    public record Segment(BlockPos from, BlockPos to) {
        public static final StreamCodec<ByteBuf, Segment> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Segment::from,
                BlockPos.STREAM_CODEC, Segment::to,
                Segment::new
        );
    }
}
