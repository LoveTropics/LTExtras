package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundPickCollectibleItemPacket(Collectible collectible) implements CustomPacketPayload {
    private static final int NOT_FOUND = -1;

    public static final Type<ServerboundPickCollectibleItemPacket> TYPE = new Type<>(LTExtras.location("pick_collectible"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPickCollectibleItemPacket> STREAM_CODEC = StreamCodec.composite(
            Collectible.STREAM_CODEC, ServerboundPickCollectibleItemPacket::collectible,
            ServerboundPickCollectibleItemPacket::new
    );

    public static void handle(ServerboundPickCollectibleItemPacket packet, IPayloadContext ctx) {
        Collectible collectible = packet.collectible;
        final ServerPlayer player = (ServerPlayer) ctx.player();

        if (player.containerMenu != player.inventoryMenu) {
            return;
        }

        final CollectibleStore collectibles = CollectibleStore.get(player);
        if (collectibles.isLocked() || !collectibles.contains(collectible)) {
            player.inventoryMenu.broadcastChanges();
            return;
        }

        final Inventory inventory = player.getInventory();
        final int index = findCollectibleInInventory(inventory, collectible);
        if (index == NOT_FOUND) {
            player.inventoryMenu.setCarried(collectible.createItemStack(player.getUUID()));
        } else {
            player.inventoryMenu.setCarried(inventory.removeItemNoUpdate(index));
        }
    }

    private static int findCollectibleInInventory(final Inventory inventory, final Collectible collectible) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (collectible.matches(inventory.getItem(i))) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    @Override
    public Type<ServerboundPickCollectibleItemPacket> type() {
        return TYPE;
    }
}
