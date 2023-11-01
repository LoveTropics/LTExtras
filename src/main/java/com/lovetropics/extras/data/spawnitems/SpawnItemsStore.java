package com.lovetropics.extras.data.spawnitems;

import com.lovetropics.extras.LTExtras;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
@AutoRegisterCapability
public final class SpawnItemsStore implements ICapabilitySerializable<Tag> {
    public static final ResourceLocation ID = new ResourceLocation(LTExtras.MODID, "spawn_items");

    private static final Codec<Map<ResourceLocation, List<SpawnItems.Stack>>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, SpawnItems.Stack.CODEC.listOf()
            .xmap(ArrayList::new, Function.identity())); // Make list mutable
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<ResourceLocation, List<SpawnItems.Stack>> receivedItems = new HashMap<>();
    private final LazyOptional<SpawnItemsStore> instance = LazyOptional.of(() -> this);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return LTExtras.SPAWN_ITEMS_STORE.orEmpty(cap, instance);
    }

    @Override
    public Tag serializeNBT() {
        return Util.getOrThrow(CODEC.encodeStart(NbtOps.INSTANCE, receivedItems), IllegalStateException::new);
    }

    @Override
    public void deserializeNBT(final Tag nbt) {
        CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(Util.prefix("Spawn Items: ", LOGGER::error)).ifPresent(map -> {
            receivedItems.clear();
            receivedItems.putAll(map);
        });
    }

    @SubscribeEvent
    static void onAttachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer) {
            event.addCapability(ID, new SpawnItemsStore());
        }
    }

    @SubscribeEvent
    static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof final ServerPlayer player) {
            sendItems(player);
        }
    }

    private static void sendItems(ServerPlayer player) {
        final var cap = getNullable(player);
        if (cap == null) {
            return;
        }
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
            if (items.shouldApplyToPlayer(player)) {
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
        oldPlayer.reviveCaps();
        try {
            final SpawnItemsStore oldStore = getNullable(oldPlayer);
            final SpawnItemsStore newStore = getNullable(event.getEntity());
            if (oldStore != null && newStore != null) {
                newStore.receivedItems.putAll(oldStore.receivedItems);
            }
        } finally {
            oldPlayer.invalidateCaps();
        }
    }

    @Nullable
    public static SpawnItemsStore getNullable(final Player player) {
        return player.getCapability(LTExtras.SPAWN_ITEMS_STORE).orElse(null);
    }
}
