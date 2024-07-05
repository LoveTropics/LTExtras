package com.lovetropics.extras.collectible;

import com.lovetropics.extras.mixin.MinecraftServerAccessor;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CollectibleLister {
    private static final String PLAYER_DATA_SUFFIX = ".dat";
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
            final CollectibleStore store = CollectibleStore.get(player);
            entries.add(new Entry(player.getUUID(), store.asData()));
            seenProfileIds.add(player.getUUID());
        }

        final PlayerDataStorage playerDataStorage = ((MinecraftServerAccessor) server).getPlayerDataStorage();

        return CompletableFuture.supplyAsync(() -> listSeenPlayers(playerDataStorage), Util.ioPool())
                .thenCompose(seenPlayers -> {
                    final List<CompletableFuture<Entry>> futures = new ArrayList<>();
                    for (final UUID profileId : seenPlayers) {
                        if (!seenProfileIds.contains(profileId)) {
                            futures.add(CompletableFuture.supplyAsync(() -> loadPlayerData(profileId, playerDataStorage), Util.ioPool()));
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

    private static List<UUID> listSeenPlayers(final PlayerDataStorage storage) {
        try (final Stream<Path> files = Files.list(storage.getPlayerDir().toPath())) {
            return files.<UUID>mapMulti((path, consumer) -> {
                final String fileName = path.getFileName().toString();
                if (fileName.endsWith(PLAYER_DATA_SUFFIX)) {
                    final String uuidString = fileName.substring(0, fileName.length() - PLAYER_DATA_SUFFIX.length());
                    try {
                        final UUID uuid = UUID.fromString(uuidString);
                        consumer.accept(uuid);
                    } catch (final IllegalArgumentException ignored) {
                    }
                }
            }).toList();
        } catch (final IOException e) {
            LOGGER.error("Failed to list players with data", e);
            return List.of();
        }
    }

    @Nullable
    private static Entry loadPlayerData(final UUID profileId, final PlayerDataStorage playerDataStorage) {
        final CompoundTag tag;
        try {
            final Path path = playerDataStorage.getPlayerDir().toPath().resolve(profileId + PLAYER_DATA_SUFFIX);
            tag = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
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
