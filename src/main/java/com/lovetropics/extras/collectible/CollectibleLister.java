package com.lovetropics.extras.collectible;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.mixin.MinecraftServerAccessor;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.neoforged.neoforge.attachment.AttachmentHolder;
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

    public static CompletableFuture<List<UUID>> listPlayersWithItem(MinecraftServer server, Predicate<ItemStack> item) {
        return list(server).thenApplyAsync(entries -> {
            List<UUID> profileIds = new ArrayList<>();
            for (CollectibleLister.Entry entry : entries) {
                for (Collectible collectible : entry.data().collectibles()) {
                    ItemStack stack = collectible.createItemStack(entry.profileId());
                    if (item.test(stack)) {
                        profileIds.add(entry.profileId());
                    }
                }
            }
            return profileIds;
        }, Util.backgroundExecutor());
    }

    public static CompletableFuture<List<Entry>> list(MinecraftServer server) {
        Set<UUID> seenProfileIds = new ObjectOpenHashSet<>();
        List<Entry> entries = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            CollectibleStore store = CollectibleStore.get(player);
            entries.add(new Entry(player.getUUID(), store.asData()));
            seenProfileIds.add(player.getUUID());
        }

        PlayerDataStorage playerDataStorage = ((MinecraftServerAccessor) server).getPlayerDataStorage();
        RegistryOps<Tag> ops = server.registryAccess().createSerializationContext(NbtOps.INSTANCE);

        return CompletableFuture.supplyAsync(() -> listSeenPlayers(playerDataStorage), Util.ioPool())
                .thenCompose(seenPlayers -> {
                    List<CompletableFuture<Entry>> futures = new ArrayList<>();
                    for (UUID profileId : seenPlayers) {
                        if (!seenProfileIds.contains(profileId)) {
                            futures.add(CompletableFuture.supplyAsync(() -> loadPlayerData(profileId, playerDataStorage, ops), Util.ioPool()));
                        }
                    }
                    return Util.sequence(futures);
                })
                .thenApply(loadedEntries -> {
                    for (Entry entry : loadedEntries) {
                        if (entry != null) {
                            entries.add(entry);
                        }
                    }
                    return entries;
                });
    }

    private static List<UUID> listSeenPlayers(PlayerDataStorage storage) {
        try (Stream<Path> files = Files.list(storage.getPlayerDir().toPath())) {
            return files.<UUID>mapMulti((path, consumer) -> {
                String fileName = path.getFileName().toString();
                if (fileName.endsWith(PLAYER_DATA_SUFFIX)) {
                    String uuidString = fileName.substring(0, fileName.length() - PLAYER_DATA_SUFFIX.length());
                    try {
                        UUID uuid = UUID.fromString(uuidString);
                        consumer.accept(uuid);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }).toList();
        } catch (IOException e) {
            LOGGER.error("Failed to list players with data", e);
            return List.of();
        }
    }

    @Nullable
    private static Entry loadPlayerData(UUID profileId, PlayerDataStorage playerDataStorage, RegistryOps<Tag> ops) {
        CompoundTag tag;
        try {
            Path path = playerDataStorage.getPlayerDir().toPath().resolve(profileId + PLAYER_DATA_SUFFIX);
            tag = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            LOGGER.error("Failed to load player data for {}", profileId, e);
            return null;
        }
        CompoundTag attachmentsTag = tag.getCompound(AttachmentHolder.ATTACHMENTS_NBT_KEY);
        Tag collectiblesTag = attachmentsTag.get(ExtraAttachments.COLLECTIBLE_STORE.getKey().location().toString());
        if (collectiblesTag == null) {
            return null;
        }
        return CollectibleData.CODEC.parse(ops, collectiblesTag)
                .resultOrPartial(Util.prefix("Failed to parse player data for " + profileId, LOGGER::warn))
                .map(data -> new Entry(profileId, data))
                .orElse(null);
    }

    public record Entry(UUID profileId, CollectibleData data) {
    }
}
