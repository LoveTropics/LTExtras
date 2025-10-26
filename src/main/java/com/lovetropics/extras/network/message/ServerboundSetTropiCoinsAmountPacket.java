package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.TropiCoinsStore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSetTropiCoinsAmountPacket(int amount) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetTropiCoinsAmountPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundSetTropiCoinsAmountPacket::amount,
            ServerboundSetTropiCoinsAmountPacket::new);

    public static final Type<ServerboundSetTropiCoinsAmountPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "set_tropicoins_amount"));

    public static void handle(ServerboundSetTropiCoinsAmountPacket packet, IPayloadContext context) {
        TropiCoinsStore.setAmountServerbound(context.player(), packet.amount());
    }

    @Override
    public Type<ServerboundSetTropiCoinsAmountPacket> type() {
        return TYPE;
    }
}
