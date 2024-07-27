package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundPickCollectibleItemPacket(Holder<Collectible> collectible) implements CustomPacketPayload {
    private static final int NOT_FOUND = -1;

    public static final Type<ServerboundPickCollectibleItemPacket> TYPE = new Type<>(LTExtras.location("pick_collectible"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPickCollectibleItemPacket> STREAM_CODEC = StreamCodec.composite(
            Collectible.STREAM_CODEC, ServerboundPickCollectibleItemPacket::collectible,
            ServerboundPickCollectibleItemPacket::new
    );

    public static void handle(ServerboundPickCollectibleItemPacket packet, IPayloadContext ctx) {
        Holder<Collectible> collectible = packet.collectible;
        ServerPlayer player = (ServerPlayer) ctx.player();

        if (player.containerMenu != player.inventoryMenu) {
            return;
        }

        CollectibleStore collectibles = CollectibleStore.get(player);
        if (collectibles.isLocked() || !collectibles.contains(collectible)) {
            player.inventoryMenu.broadcastChanges();
            return;
        }

        Inventory inventory = player.getInventory();
        int index = findCollectibleInInventory(inventory, collectible);
        if (index == NOT_FOUND) {
            player.inventoryMenu.setCarried(Collectible.createItemStack(collectible, player.getUUID()));
        } else {
            player.inventoryMenu.setCarried(inventory.removeItemNoUpdate(index));
        }
    }

    private static int findCollectibleInInventory(Inventory inventory, Holder<Collectible> collectible) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (Collectible.matches(collectible, inventory.getItem(i))) {
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
