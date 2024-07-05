package com.lovetropics.extras.data.spawnitems;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@EventBusSubscriber
public final class SpawnItemsStore implements IAttachmentSerializer<Tag, SpawnItemsStore> {
    private static final Codec<Map<ResourceLocation, List<SpawnItems.Stack>>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, SpawnItems.Stack.CODEC.listOf()
            .xmap(ArrayList::new, Function.identity())); // Make list mutable
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<ResourceLocation, List<SpawnItems.Stack>> receivedItems = new HashMap<>();

    @SubscribeEvent
    static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof final ServerPlayer player) {
            sendItems(player);
        }
    }

    private static void sendItems(ServerPlayer player) {
        final var cap = get(player);
        final var diff = getDiff(player, cap.receivedItems);

        for (final var entry : diff.entrySet()) {
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
    static void onReloadResources(final OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) { // We've ran /reload
            LOGGER.debug("Sending spawn items to all players after reload...");
            event.getPlayerList().getPlayers().forEach(SpawnItemsStore::sendItems);
        }
    }

    private static Map<ResourceLocation, List<SpawnItems.Stack>> getDiff(ServerPlayer player, Map<ResourceLocation, List<SpawnItems.Stack>> old) {
        final Map<ResourceLocation, List<SpawnItems.Stack>> diff = new HashMap<>();
        SpawnItemsReloadListener.REGISTRY.forEach((location, items) -> {
            final var oldReceived = old.getOrDefault(location, List.of());
            if (items.canApplyToPlayer(player)) {
                diff.put(location, items.items().stream()
                        .filter(Predicate.not(oldReceived::contains))
                        .toList());
            }
        });
        return diff;
    }

    @SubscribeEvent
    static void onPlayerClone(final PlayerEvent.Clone event) {
        final Player oldPlayer = event.getOriginal();
        if (event.isWasDeath() && !oldPlayer.level().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get()) {
            return;
        }

        final SpawnItemsStore oldStore = get(oldPlayer);
        final SpawnItemsStore newStore = get(event.getEntity());
        newStore.receivedItems.putAll(oldStore.receivedItems);
    }

    public static SpawnItemsStore get(final Player player) {
        return player.getData(ExtraAttachments.SPAWN_ITEMS_STORE);
    }

    @Override
    public SpawnItemsStore read(IAttachmentHolder holder, Tag tag, HolderLookup.Provider registries) {
        CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag).resultOrPartial(Util.prefix("Spawn Items: ", LOGGER::error)).ifPresent(map -> {
            receivedItems.clear();
            receivedItems.putAll(map);
        });

        return this;
    }

    @Override
    @Nullable
    public Tag write(SpawnItemsStore attachment, HolderLookup.Provider registries) {
        return CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), receivedItems).getOrThrow();
    }
}
