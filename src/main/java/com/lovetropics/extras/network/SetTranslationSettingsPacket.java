package com.lovetropics.extras.network;

import com.lovetropics.extras.translation.TranslationOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetTranslationSettingsPacket(boolean translateIncoming, boolean translateOutgoing) {
    public SetTranslationSettingsPacket(final FriendlyByteBuf input) {
        this(input.readBoolean(), input.readBoolean());
    }

    public void write(final FriendlyByteBuf output) {
        output.writeBoolean(translateIncoming);
        output.writeBoolean(translateOutgoing);
    }

    public void handle(final Supplier<NetworkEvent.Context> ctx) {
        final ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            TranslationOptions.set(player, translateIncoming, translateOutgoing);
        }
    }
}
