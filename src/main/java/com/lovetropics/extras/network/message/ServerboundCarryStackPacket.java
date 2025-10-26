package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundCarryStackPacket(ItemStack carryStack) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCarryStackPacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, ServerboundCarryStackPacket::carryStack,
            ServerboundCarryStackPacket::new);

    public static final Type<ServerboundCarryStackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "carry_stack"));

    public static void handle(ServerboundCarryStackPacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            serverPlayer.containerMenu.setCarried(packet.carryStack());
        }
    }

    @Override
    public Type<ServerboundCarryStackPacket> type() {
        return TYPE;
    }
}
