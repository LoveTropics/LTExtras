package com.lovetropics.extras.network.message;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.TropiCoinsStore;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.mixin.AbstractContainerMenuAccess;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundExchangeTropiCoinsPacket(boolean store, int count) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, ServerboundExchangeTropiCoinsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundExchangeTropiCoinsPacket::store,
            ByteBufCodecs.VAR_INT, ServerboundExchangeTropiCoinsPacket::count,
            ServerboundExchangeTropiCoinsPacket::new
    );

    public static final Type<ServerboundExchangeTropiCoinsPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(LTExtras.MODID, "exchange_tropicoins"));

    public static void handle(ServerboundExchangeTropiCoinsPacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            boolean success;
            if (packet.store) {
                success = handleStore(player, packet.count);
            } else {
                success = handleRetrieve(player, packet.count);
            }
            if (success) {
                // The client and server got the same result, don't sync anything
                setRemoteCarried(player.containerMenu, player.containerMenu.getCarried());
            } else {
                // The client is likely just desynced, try to get back to a common state
                player.syncData(ExtraAttachments.TROPICOINS_STORE);
                player.containerMenu.sendAllDataToRemote();
            }
        }
    }

    private static boolean handleStore(ServerPlayer player, int requestedCount) {
        ItemStack carried = player.containerMenu.getCarried();
        if (!carried.is(ExtraItems.TROPICOIN)) {
            return false;
        }
        int actualCount = Math.min(requestedCount, carried.getCount());
        TropiCoinsStore store = player.getData(ExtraAttachments.TROPICOINS_STORE);
        carried.shrink(actualCount);
        store.setAmount(store.getAmount() + actualCount);
        return actualCount == requestedCount;
    }

    private static boolean handleRetrieve(ServerPlayer player, int requestedCount) {
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.isEmpty()) {
            TropiCoinsStore store = player.getData(ExtraAttachments.TROPICOINS_STORE);
            int actualCount = requestedCount;
            actualCount = Math.min(actualCount, store.getAmount());
            actualCount = Math.min(actualCount, ExtraItems.TROPICOIN.get().getDefaultMaxStackSize());
            store.setAmount(store.getAmount() - actualCount);
            player.containerMenu.setCarried(new ItemStack(ExtraItems.TROPICOIN.get(), actualCount));
            return actualCount == requestedCount;
        } else if (carried.is(ExtraItems.TROPICOIN)) {
            TropiCoinsStore store = player.getData(ExtraAttachments.TROPICOINS_STORE);
            int actualCount = requestedCount;
            actualCount = Math.min(actualCount, store.getAmount());
            actualCount = Math.min(actualCount, carried.getMaxStackSize() - carried.getCount());
            store.setAmount(store.getAmount() - actualCount);
            carried.grow(actualCount);
            return actualCount == requestedCount;
        }
        return false;
    }

    private static void setRemoteCarried(AbstractContainerMenu menu, ItemStack carried) {
        ((AbstractContainerMenuAccess) menu).getRemoteCarried().force(carried);
    }

    @Override
    public Type<ServerboundExchangeTropiCoinsPacket> type() {
        return TYPE;
    }
}
