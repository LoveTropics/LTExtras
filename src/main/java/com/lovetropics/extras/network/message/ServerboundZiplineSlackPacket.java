package com.lovetropics.extras.network.message;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.zipline.ZiplineIndex;
import com.lovetropics.extras.zipline.ZiplinePoleBlockEntity;
import com.lovetropics.extras.zipline.ZiplineSegment;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent when a player uses the wrench on a rope, to loosen or tighten it.
 */
public record ServerboundZiplineSlackPacket(BlockPos from, BlockPos to, boolean loosen) implements CustomPacketPayload {
    public static final Type<ServerboundZiplineSlackPacket> TYPE = new Type<>(LTExtras.id("zipline_slack"));
    public static final StreamCodec<ByteBuf, ServerboundZiplineSlackPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ServerboundZiplineSlackPacket::from,
            BlockPos.STREAM_CODEC, ServerboundZiplineSlackPacket::to,
            ByteBufCodecs.BOOL, ServerboundZiplineSlackPacket::loosen,
            ServerboundZiplineSlackPacket::new
    );

    private static final double REACH_BUFFER = 2.0;

    public static void handle(ServerboundZiplineSlackPacket packet, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        if (!player.mayBuild() || !player.isHolding(ExtraItems.ZIPLINE_WRENCH.get())) {
            return;
        }
        ZiplineSegment segment = ZiplineIndex.get(player.level()).getSegment(packet.from, packet.to);
        double reach = player.blockInteractionRange() + REACH_BUFFER;
        if (segment == null || segment.distanceToSqr(player.getEyePosition()) > reach * reach) {
            return;
        }
        int slack = Mth.clamp(segment.slack() + (packet.loosen ? 1 : -1), 0, ZiplineSegment.MAX_SLACK);
        ZiplinePoleBlockEntity.setSlack(player.level(), packet.from, packet.to, slack);
        player.sendOverlayMessage(ExtraLangKeys.ZIPLINE_SLACK.format(slack, ZiplineSegment.MAX_SLACK));
    }

    @Override
    public Type<ServerboundZiplineSlackPacket> type() {
        return TYPE;
    }
}
