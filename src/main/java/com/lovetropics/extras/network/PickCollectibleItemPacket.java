package com.lovetropics.extras.network;

import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PickCollectibleItemPacket(Collectible collectible) {
    private static final int NOT_FOUND = -1;

    public PickCollectibleItemPacket(final FriendlyByteBuf input) {
        this(new Collectible(input));
    }

    public void write(final FriendlyByteBuf output) {
        collectible.write(output);
    }

    public void handle(final Supplier<NetworkEvent.Context> ctx) {
        final ServerPlayer player = ctx.get().getSender();
        if (player == null || player.containerMenu != player.inventoryMenu) {
            return;
        }

        final CollectibleStore collectibles = CollectibleStore.getNullable(player);
        if (collectibles == null || !collectibles.contains(collectible)) {
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
}
