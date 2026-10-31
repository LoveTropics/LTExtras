package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.entity.ForkliftEntity;
import com.lovetropics.extras.entity.SeeSawEntity;
import com.lovetropics.extras.entity.SwingEntity;
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

public record ServerboundSwingPacket(boolean playerInput, int entityId) implements CustomPacketPayload {
    public static final Type<ServerboundSwingPacket> TYPE = new Type<>(LTExtras.id("swing"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSwingPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundSwingPacket::playerInput,
            ByteBufCodecs.VAR_INT, ServerboundSwingPacket::entityId,
            ServerboundSwingPacket::new
    );

    public static void handle(ServerboundSwingPacket packet, IPayloadContext ctx) {
        final Level level = ctx.player().level();
        if (level.getEntity(packet.entityId) instanceof SwingEntity swing) {
            swing.setPlayerInput(packet.playerInput);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
