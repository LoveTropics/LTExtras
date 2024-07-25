package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.entity.HologramEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSetHologramTextPacket(int entityId, Component text) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetHologramTextPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundSetHologramTextPacket::entityId,
            ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundSetHologramTextPacket::text,
            ClientboundSetHologramTextPacket::new
    );

    public static final Type<ClientboundSetHologramTextPacket> TYPE = new Type<>(LTExtras.location("hologram_text"));

    public static void handle(ClientboundSetHologramTextPacket packet, IPayloadContext ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(packet.entityId);
        if (entity instanceof HologramEntity hologram) {
            hologram.setDisplayText(packet.text);
        }
    }

    @Override
    public Type<ClientboundSetHologramTextPacket> type() {
        return TYPE;
    }
}
