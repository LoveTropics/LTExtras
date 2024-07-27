package com.lovetropics.extras.collectible;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.network.message.ClientboundCollectiblesListPacket;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber(modid = LTExtras.MODID)
public class CollectibleStore {
    public static final Codec<CollectibleStore> CODEC = CollectibleData.CODEC.xmap(
            data -> {
                CollectibleStore store = new CollectibleStore();
                store.collectibles.clear();
                store.collectibles.addAll(data.collectibles());
                store.hasUnseen = data.hasUnseen();
                return store;
            },
            CollectibleStore::asData
    );

    @Nullable
    private ServerPlayer player;

    private final List<Collectible> collectibles = new ArrayList<>();
    private boolean hasUnseen;
    private boolean locked;

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        CollectibleStore collectibles = get(event.getEntity());
        collectibles.sendToClient(true);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        if (event.isWasDeath()) {
            CollectibleStore oldCollectibles = get(oldPlayer);
            CollectibleStore newCollectibles = get(event.getEntity());

            newCollectibles.collectibles.addAll(oldCollectibles.collectibles);
            newCollectibles.sendToClient(true);
        }
    }

    public static CollectibleStore get(Player player) {
        CollectibleStore data = player.getData(ExtraAttachments.COLLECTIBLE_STORE);
        data.player = (ServerPlayer) player;
        return data;
    }

    public CollectibleData asData() {
        return new CollectibleData(collectibles, hasUnseen);
    }

    public boolean give(Collectible collectible) {
        if (!collectibles.contains(collectible)) {
            collectibles.add(collectible);
            hasUnseen = true;
            sendToClient(false);
            return true;
        }
        return false;
    }

    public boolean clear(Predicate<Collectible> predicate) {
        if (collectibles.removeIf(predicate)) {
            sendToClient(false);
            return true;
        }
        return false;
    }

    public boolean contains(Collectible collectible) {
        return collectibles.contains(collectible);
    }

    public int count(Predicate<Collectible> predicate) {
        int count = 0;
        for (Collectible collectible : collectibles) {
            if (predicate.test(collectible)) {
                count++;
            }
        }
        return count;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public void markSeen() {
        hasUnseen = false;
    }

    private void sendToClient(boolean silent) {
        if (player != null) {
            PacketDistributor.sendToPlayer(player, new ClientboundCollectiblesListPacket(collectibles, silent, hasUnseen));
        }
    }
}
