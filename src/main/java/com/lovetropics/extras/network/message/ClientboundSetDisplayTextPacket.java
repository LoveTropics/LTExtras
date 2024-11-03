package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.mixin.placeholder.TextDisplayAccessor;
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

public record ClientboundSetDisplayTextPacket(int entityId, Component text) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetDisplayTextPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundSetDisplayTextPacket::entityId,
            ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundSetDisplayTextPacket::text,
            ClientboundSetDisplayTextPacket::new
    );

    public static final Type<ClientboundSetDisplayTextPacket> TYPE = new Type<>(LTExtras.location("hologram_text"));

    public static void handle(ClientboundSetDisplayTextPacket packet, IPayloadContext ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(packet.entityId);
        if (entity instanceof TextDisplayAccessor textDisplay) {
            textDisplay.ltextras$setText(packet.text);
        }
    }

    @Override
    public Type<ClientboundSetDisplayTextPacket> type() {
        return TYPE;
    }
}
