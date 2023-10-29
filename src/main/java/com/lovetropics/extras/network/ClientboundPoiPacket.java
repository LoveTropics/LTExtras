package com.lovetropics.extras.network;

import com.lovetropics.extras.client.ClientMapPoiManager;
import com.lovetropics.extras.data.poi.Poi;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundPoiPacket(Poi poi, boolean delete) {
    public ClientboundPoiPacket(final FriendlyByteBuf input) {
        this(new Poi(input.readUtf(50),
                input.readComponent(),
                input.readResourceLocation(),
                input.readGlobalPos(),
                input.readBoolean(),
                input.readList(FriendlyByteBuf::readUUID)),
                input.readBoolean());
    }

    public void write(final FriendlyByteBuf output) {
        output.writeUtf(poi.name(), 50);
        output.writeComponent(poi.description());
        output.writeResourceLocation(poi.resourceLocation());
        output.writeGlobalPos(poi.globalPos());
        output.writeBoolean(poi.enabled());
        output.writeCollection(poi.faces(), FriendlyByteBuf::writeUUID);
        output.writeBoolean(delete);
    }

    public static void handle(final ClientboundPoiPacket packet, final Supplier<NetworkEvent.Context> ctx) {
        ClientMapPoiManager.updatePoi(packet.poi(), packet.delete());
    }
}
