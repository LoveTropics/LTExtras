package com.lovetropics.extras.collectible;

import com.lovetropics.extras.mixin.MinecraftServerAccessor;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class CollectibleLister {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static CompletableFuture<List<UUID>> listPlayersWithItem(final MinecraftServer server, final Predicate<ItemStack> item) {
        return list(server).thenApplyAsync(entries -> {
            final List<UUID> profileIds = new ArrayList<>();
            for (final CollectibleLister.Entry entry : entries) {
                for (final Collectible collectible : entry.data().collectibles()) {
                    final ItemStack stack = collectible.createItemStack(entry.profileId());
                    if (item.test(stack)) {
                        profileIds.add(entry.profileId());
                    }
                }
            }
            return profileIds;
        }, Util.backgroundExecutor());
    }

    public static CompletableFuture<List<Entry>> list(final MinecraftServer server) {
        final Set<UUID> seenProfileIds = new ObjectOpenHashSet<>();
        final List<Entry> entries = new ArrayList<>();
        for (final ServerPlayer player : server.getPlayerList().getPlayers()) {
            final CollectibleStore store = CollectibleStore.getNullable(player);
            if (store != null) {
                entries.add(new Entry(player.getUUID(), store.asData()));
                seenProfileIds.add(player.getUUID());
            }
        }

        final PlayerDataStorage playerDataStorage = ((MinecraftServerAccessor) server).getPlayerDataStorage();
        return CompletableFuture.supplyAsync(playerDataStorage::getSeenPlayers, Util.ioPool())
                .thenCompose(seenPlayers -> {
                    final List<CompletableFuture<Entry>> futures = new ArrayList<>();
                    for (final String player : seenPlayers) {
                        try {
                            final UUID profileId = UUID.fromString(player);
                            if (!seenProfileIds.contains(profileId)) {
                                futures.add(CompletableFuture.supplyAsync(() -> loadPlayerData(player, playerDataStorage, profileId), Util.ioPool()));
                            }
                        } catch (final IllegalArgumentException ignored) {
                        }
                    }
                    return Util.sequence(futures);
                })
                .thenApply(loadedEntries -> {
                    for (final Entry entry : loadedEntries) {
                        if (entry != null) {
                            entries.add(entry);
                        }
                    }
                    return entries;
                });
    }

    @Nullable
    private static Entry loadPlayerData(final String player, final PlayerDataStorage playerDataStorage, final UUID profileId) {
        final CompoundTag tag;
        try {
            final Path path = playerDataStorage.getPlayerDataFolder().toPath().resolve(player + ".dat");
            tag = NbtIo.readCompressed(path.toFile());
        } catch (final IOException e) {
            LOGGER.error("Failed to load player data for {}", profileId, e);
            return null;
        }
        final CompoundTag forgeCaps = tag.getCompound("ForgeCaps");
        final Tag collectiblesTag = forgeCaps.get(CollectibleStore.ID.toString());
        if (collectiblesTag == null) {
            return null;
        }
        return CollectibleData.CODEC.parse(NbtOps.INSTANCE, collectiblesTag)
                .resultOrPartial(Util.prefix("Failed to parse player data for " + profileId, LOGGER::warn))
                .map(data -> new Entry(profileId, data))
                .orElse(null);
    }

    public record Entry(UUID profileId, CollectibleData data) {
    }
}
