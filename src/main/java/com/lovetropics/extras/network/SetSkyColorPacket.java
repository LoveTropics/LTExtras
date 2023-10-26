package com.lovetropics.extras.network;

import com.lovetropics.extras.client.world_effect.SkyColorEffectHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetSkyColorPacket(int color, int fadeLength) {
    private static final int CLEAR = Integer.MIN_VALUE;

    public SetSkyColorPacket(final FriendlyByteBuf input) {
        this(input.readInt(), input.readVarInt());
    }

    public static SetSkyColorPacket clear(final int fadeLength) {
        return new SetSkyColorPacket(CLEAR, fadeLength);
    }

    public void write(final FriendlyByteBuf output) {
        output.writeInt(color);
        output.writeVarInt(fadeLength);
    }

    public static void handle(final SetSkyColorPacket packet, final Supplier<NetworkEvent.Context> ctx) {
        if (packet.color == CLEAR) {
            SkyColorEffectHandler.clear(packet.fadeLength);
        } else {
            SkyColorEffectHandler.apply(packet.color, packet.fadeLength);
        }
    }
}
