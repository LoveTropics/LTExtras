package com.lovetropics.extras.network;

import com.lovetropics.extras.client.ClientCollectiblesList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenCollectibleBasketPacket() {
    public OpenCollectibleBasketPacket(final FriendlyByteBuf input) {
        this();
    }

    public void write(final FriendlyByteBuf output) {
    }

    public static void handle(final OpenCollectibleBasketPacket packet, final Supplier<NetworkEvent.Context> ctx) {
        ClientCollectiblesList.openScreen();
    }
}
