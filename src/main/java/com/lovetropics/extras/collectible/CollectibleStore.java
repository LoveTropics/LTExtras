package com.lovetropics.extras.collectible;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.network.message.ClientboundCollectiblesListPacket;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
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
    public static final MapCodec<CollectibleStore> MAP_CODEC = CollectibleData.MAP_CODEC.xmap(
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

    private final List<Holder<Collectible>> collectibles = new ArrayList<>();
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
        if (player instanceof ServerPlayer serverPlayer) {
            data.player = serverPlayer;
        }
        return data;
    }

    public CollectibleData asData() {
        return new CollectibleData(collectibles, hasUnseen);
    }

    public boolean give(Holder<Collectible> collectible) {
        if (!collectibles.contains(collectible)) {
            maybeEquip(collectible);
            collectibles.add(collectible);
            hasUnseen = true;
            sendToClient(false);
            return true;
        }
        return false;
    }

    public boolean clear(Predicate<Holder<Collectible>> predicate) {
        if (collectibles.removeIf(predicate)) {
            sendToClient(false);
            return true;
        }
        return false;
    }

    public boolean contains(Holder<Collectible> collectible) {
        return collectibles.contains(collectible);
    }

    public int count(Predicate<Holder<Collectible>> predicate) {
        int count = 0;
        for (Holder<Collectible> collectible : collectibles) {
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

    private void maybeEquip(Holder<Collectible> collectible) {
        if (player != null) {
            Collectible value = collectible.value();
            Equippable equippable = value.get(DataComponents.EQUIPPABLE);
            if (value.autoEquip() && equippable != null) {
                ItemStack currentItem = player.getItemBySlot(equippable.slot());
                if (!currentItem.has(ExtraDataComponents.COLLECTIBLE)) {
                    if (!player.addItem(currentItem)) {
                        ItemEntity drop = player.drop(currentItem, false);
                        if (drop != null) {
                            drop.setNoPickUpDelay();
                            drop.setTarget(player.getUUID());
                        }
                    }
                }
                EquipmentSlot slot = equippable.slot();
                player.setItemSlot(slot, Collectible.createItemStack(collectible, player.getUUID()));
            }
        }
    }
}
