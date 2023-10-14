package com.lovetropics.extras.network;

import com.lovetropics.extras.schedule.PlayerTimeZone;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.function.Supplier;

public record SetTimeZonePacket(ZoneId id) {
    private static final int MAX_LENGTH = 64;

    public static SetTimeZonePacket read(final FriendlyByteBuf input) {
        try {
            return new SetTimeZonePacket(ZoneId.of(input.readUtf(MAX_LENGTH)));
        } catch (final DateTimeException e) {
            return new SetTimeZonePacket(ZoneOffset.UTC);
        }
    }

    public void write(final FriendlyByteBuf output) {
        output.writeUtf(id.getId());
    }

    public void handle(final Supplier<NetworkEvent.Context> ctx) {
        final ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            PlayerTimeZone.set(player, id);
        }
    }
}
