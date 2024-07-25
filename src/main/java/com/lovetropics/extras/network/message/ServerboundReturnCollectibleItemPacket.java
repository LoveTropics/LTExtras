package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundReturnCollectibleItemPacket(Collectible collectible) implements CustomPacketPayload {
    public static final Type<ServerboundReturnCollectibleItemPacket> TYPE = new Type<>(LTExtras.location("return_collectible"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundReturnCollectibleItemPacket> STREAM_CODEC = StreamCodec.composite(
            Collectible.STREAM_CODEC, ServerboundReturnCollectibleItemPacket::collectible,
            ServerboundReturnCollectibleItemPacket::new
    );

    public static void handle(ServerboundReturnCollectibleItemPacket packet, IPayloadContext ctx) {
        Collectible collectible = packet.collectible;
        ServerPlayer player = (ServerPlayer) ctx.player();
        if (player.containerMenu != player.inventoryMenu) {
            return;
        }

        CollectibleStore collectibles = CollectibleStore.get(player);

        if (collectibles.contains(collectible) && collectible.matches(player.inventoryMenu.getCarried())) {
            player.inventoryMenu.setCarried(ItemStack.EMPTY);
        } else {
            player.inventoryMenu.broadcastChanges();
        }
    }

    @Override
    public Type<ServerboundReturnCollectibleItemPacket> type() {
        return TYPE;
    }
}
