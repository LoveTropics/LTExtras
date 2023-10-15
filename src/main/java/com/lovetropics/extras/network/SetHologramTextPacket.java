package com.lovetropics.extras.network;

import com.lovetropics.extras.entity.HologramEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetHologramTextPacket(int entityId, Component text) {
    public SetHologramTextPacket(final FriendlyByteBuf input) {
        this(input.readVarInt(), input.readComponent());
    }

    public void write(final FriendlyByteBuf output) {
        output.writeVarInt(entityId);
        output.writeComponent(text);
    }

    public static void handle(final SetHologramTextPacket packet, final Supplier<NetworkEvent.Context> ctx) {
        final Minecraft minecraft = Minecraft.getInstance();
        final ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        final Entity entity = level.getEntity(packet.entityId);
        if (entity instanceof final HologramEntity hologram) {
            hologram.setDisplayText(packet.text);
        }
    }
}
