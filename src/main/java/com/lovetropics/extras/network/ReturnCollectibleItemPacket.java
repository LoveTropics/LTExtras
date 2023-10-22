package com.lovetropics.extras.network;

import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ReturnCollectibleItemPacket(Collectible collectible) {
    public ReturnCollectibleItemPacket(final FriendlyByteBuf input) {
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
        if (collectibles == null) {
            return;
        }

        if (collectibles.contains(collectible) && collectible.matches(player.inventoryMenu.getCarried())) {
            player.inventoryMenu.setCarried(ItemStack.EMPTY);
        } else {
            player.inventoryMenu.broadcastChanges();
        }
    }
}
