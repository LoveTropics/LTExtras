package com.lovetropics.extras.network;

import com.lovetropics.extras.client.ClientCollectiblesList;
import com.lovetropics.extras.collectible.Collectible;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record CollectiblesListPacket(List<Collectible> collectibles) {
    public CollectiblesListPacket(final FriendlyByteBuf input) {
        this(input.readList(Collectible::new));
    }

    public void write(final FriendlyByteBuf output) {
        output.writeCollection(collectibles, (out, c) -> c.write(out));
    }

    public void handle(final Supplier<NetworkEvent.Context> ctx) {
        ClientCollectiblesList.get().update(collectibles);
    }
}
