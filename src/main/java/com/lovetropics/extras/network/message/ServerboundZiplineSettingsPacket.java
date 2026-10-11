package com.lovetropics.extras.network.message;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.zipline.ZiplineNetwork;
import com.lovetropics.extras.zipline.ZiplineSegment;
import com.lovetropics.extras.zipline.ZiplineSettings;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent when a player saves the settings of a rope from the settings screen opened with the wrench.
 */
public record ServerboundZiplineSettingsPacket(BlockPos from, BlockPos to, ZiplineSettings settings) implements CustomPacketPayload {
    public static final Type<ServerboundZiplineSettingsPacket> TYPE = new Type<>(LTExtras.id("zipline_settings"));
    public static final StreamCodec<ByteBuf, ServerboundZiplineSettingsPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ServerboundZiplineSettingsPacket::from,
            BlockPos.STREAM_CODEC, ServerboundZiplineSettingsPacket::to,
            ZiplineSettings.STREAM_CODEC, ServerboundZiplineSettingsPacket::settings,
            ServerboundZiplineSettingsPacket::new
    );

    private static final double REACH_BUFFER = 2.0;

    public static void handle(ServerboundZiplineSettingsPacket packet, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        if (!player.mayBuild() || !player.isHolding(ExtraItems.ZIPLINE_WRENCH.get())) {
            return;
        }
        ZiplineNetwork network = ZiplineNetwork.get(player.level());
        ZiplineSegment segment = network.getSegment(packet.from, packet.to);
        double reach = player.blockInteractionRange() + REACH_BUFFER;
        if (segment == null || segment.distanceToSqr(player.getEyePosition()) > reach * reach) {
            return;
        }
        network.setSettings(packet.from, packet.to, packet.settings.validated());
        player.sendOverlayMessage(ExtraLangKeys.ZIPLINE_SETTINGS_SAVED.get());
    }

    @Override
    public Type<ServerboundZiplineSettingsPacket> type() {
        return TYPE;
    }
}
