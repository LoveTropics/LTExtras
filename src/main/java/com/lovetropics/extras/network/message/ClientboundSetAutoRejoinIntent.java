package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.ClientAutoRejoinHandler;
import com.lovetropics.extras.rejoiner.AutoRejoinIntent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSetAutoRejoinIntent(AutoRejoinIntent intent) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, ClientboundSetAutoRejoinIntent> STREAM_CODEC = AutoRejoinIntent.STREAM_CODEC.map(ClientboundSetAutoRejoinIntent::new, ClientboundSetAutoRejoinIntent::intent);

    public static final Type<ClientboundSetAutoRejoinIntent> TYPE = new Type<>(LTExtras.id("set_auto_rejoin_intent"));

    public static void handle(ClientboundSetAutoRejoinIntent packet, IPayloadContext ctx) {
        ClientAutoRejoinHandler.handleIntent(packet.intent);
    }

    @Override
    public Type<ClientboundSetAutoRejoinIntent> type() {
        return TYPE;
    }
}
