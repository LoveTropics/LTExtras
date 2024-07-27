package com.lovetropics.extras.data.spawnitems;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@EventBusSubscriber(modid = LTExtras.MODID)
public final class SpawnItemsStore {
    public static final Codec<SpawnItemsStore> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, SpawnItems.Stack.CODEC.listOf()).xmap(
            stacksById -> {
                SpawnItemsStore store = new SpawnItemsStore();
                stacksById.forEach((id, stacks) -> {
                    store.receivedItems.put(id, new ArrayList<>(stacks));
                });
                return store;
            },
            store -> store.receivedItems
    );

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<ResourceLocation, List<SpawnItems.Stack>> receivedItems = new HashMap<>();

    @SubscribeEvent
    static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendItems(player);
        }
    }

    private static void sendItems(ServerPlayer player) {
        var cap = get(player);
        var diff = getDiff(player, cap.receivedItems);

        for (var entry : diff.entrySet()) {
            entry.getValue().forEach(stack -> {
                if (!player.addItem(stack.build())) {
                    player.level().addFreshEntity(player.drop(stack.build(), true, true));
                }
            });

            cap.receivedItems.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue());
        }

        LOGGER.debug("Sent spawn items from {} sets to player {}: {}", diff.size(), player, diff.keySet());
    }

    @SubscribeEvent
    static void onReloadResources(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) { // We've ran /reload
            LOGGER.debug("Sending spawn items to all players after reload...");
            event.getPlayerList().getPlayers().forEach(SpawnItemsStore::sendItems);
        }
    }

    private static Map<ResourceLocation, List<SpawnItems.Stack>> getDiff(ServerPlayer player, Map<ResourceLocation, List<SpawnItems.Stack>> old) {
        Map<ResourceLocation, List<SpawnItems.Stack>> diff = new HashMap<>();
        SpawnItemsReloadListener.REGISTRY.forEach((location, items) -> {
            var oldReceived = old.getOrDefault(location, List.of());
            if (items.canApplyToPlayer(player)) {
                diff.put(location, items.items().stream()
                        .filter(Predicate.not(oldReceived::contains))
                        .toList());
            }
        });
        return diff;
    }

    @SubscribeEvent
    static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        if (event.isWasDeath() && !oldPlayer.level().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get()) {
            return;
        }

        SpawnItemsStore oldStore = get(oldPlayer);
        SpawnItemsStore newStore = get(event.getEntity());
        newStore.receivedItems.putAll(oldStore.receivedItems);
    }

    public static SpawnItemsStore get(Player player) {
        return player.getData(ExtraAttachments.SPAWN_ITEMS_STORE);
    }
}
