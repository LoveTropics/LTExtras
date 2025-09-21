package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.ClientPackControl;
import com.lovetropics.extras.data.packcontrol.PackControl;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundUpdatePackControl(
        PackControl.State state
) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, ClientboundUpdatePackControl> STREAM_CODEC = PackControl.State.STREAM_CODEC.map(ClientboundUpdatePackControl::new, ClientboundUpdatePackControl::state);

    public static final Type<ClientboundUpdatePackControl> TYPE = new Type<>(LTExtras.location("update_resource_packs"));

    public static void handle(ClientboundUpdatePackControl packet, IPayloadContext context) {
        ClientPackControl.updatePacks(packet.state);
    }

    @Override
    public Type<ClientboundUpdatePackControl> type() {
        return TYPE;
    }
}
