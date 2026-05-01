package com.lovetropics.extras.data.poi;

import com.google.common.collect.Sets;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.network.message.ClientboundPoiFacesPacket;
import com.lovetropics.extras.network.message.ClientboundRemovePoiPacket;
import com.lovetropics.extras.network.message.ClientboundUpdatePoiPacket;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.lib.permission.PermissionsApi;
import com.lovetropics.lib.permission.role.RoleOverrideType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PermissionsChangedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

@EventBusSubscriber(modid = LTExtras.MODID)
public class MapManager extends SavedData {
    public static final int MAP_SIZE = 256;

    public static final Codec<MapManager> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceKey.codec(ExtraRegistries.POI).listOf().fieldOf("disabled").forGetter(m -> List.copyOf(m.disabledPois))
    ).apply(i, disabledPois -> {
        MapManager mapManager = new MapManager();
        mapManager.disabledPois.addAll(disabledPois);
        return mapManager;
    }));

    private static final SavedDataType<MapManager> TYPE = new SavedDataType<>(
            LTExtras.location("map_poi"),
            MapManager::new,
            CODEC
    );

    private final Set<ResourceKey<PoiConfig>> disabledPois = new ObjectOpenHashSet<>();
    private Map<ResourceKey<PoiConfig>, Set<UUID>> facesByPoi = Map.of();

    private final Map<UUID, PlayerSender> playerSenders = new HashMap<>();

    public static MapManager get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MapManager.get(player.level().getServer()).onPlayerLoggedIn(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerPermissionsChanged(PermissionsChangedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MapManager.get(player.level().getServer()).refreshAccessiblePois(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MapManager.get(player.level().getServer()).onPlayerLoggedOut(player);
        }
    }

    private void onPlayerLoggedIn(ServerPlayer player) {
        playerSenders.put(player.getUUID(), new PlayerSender());
        refreshAccessiblePois(player);
    }

    private void onPlayerLoggedOut(ServerPlayer player) {
        playerSenders.remove(player.getUUID());
    }

    public void reload(MinecraftServer server) {
        if (disabledPois.removeIf(key -> !MapConfigs.POIS.containsKey(key.identifier()))) {
            setDirty();
        }

        for (Map.Entry<UUID, PlayerSender> entry : playerSenders.entrySet()) {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player != null) {
                reloadForPlayer(entry.getValue(), player);
            }
        }
    }

    private void reloadForPlayer(PlayerSender sender, ServerPlayer player) {
        sender.dropAll(player, key -> !MapConfigs.POIS.containsKey(key.identifier()));
        // Resend everything, even if it didn't change
        for (Named<PoiConfig> holder : MapConfigs.POIS) {
            if (isAccessibleFor(holder, player)) {
                sender.addOrUpdate(player, holder);
            }
        }
    }

    @Nullable
    public Named<PoiConfig> getPoiAccessibleTo(ServerPlayer player, Identifier id) {
        Named<PoiConfig> holder = MapConfigs.POIS.get(id);
        return holder != null && isAccessibleFor(holder, player) ? holder : null;
    }

    private boolean isAccessibleFor(Named<PoiConfig> poi, ServerPlayer player) {
        return player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) || !disabledPois.contains(poi.key());
    }

    public Stream<ResourceKey<PoiConfig>> getAccessiblePois(ServerPlayer player) {
        return MapConfigs.POIS.stream().filter(holder -> isAccessibleFor(holder, player)).map(Named::key);
    }

    public Stream<ResourceKey<PoiConfig>> getEnabledPois() {
        return MapConfigs.POIS.stream().map(Named::key).filter(key -> !disabledPois.contains(key));
    }

    public Stream<ResourceKey<PoiConfig>> getDisabledPois() {
        return disabledPois.stream();
    }

    public boolean enable(MinecraftServer server, ResourceKey<PoiConfig> id) {
        Named<PoiConfig> holder = MapConfigs.POIS.get(id.identifier());
        if (holder == null) {
            return false;
        }
        boolean changed = disabledPois.remove(id);
        if (changed) {
            setDirty();
            refreshAccessiblePois(server);
            return true;
        }
        return false;
    }

    public boolean disable(MinecraftServer server, ResourceKey<PoiConfig> id) {
        Named<PoiConfig> holder = MapConfigs.POIS.get(id.identifier());
        if (holder == null) {
            return false;
        }
        boolean changed = disabledPois.add(id);
        if (changed) {
            setDirty();
            refreshAccessiblePois(server);
            return true;
        }
        return false;
    }

    private void refreshAccessiblePois(ServerPlayer player) {
        PlayerSender sender = playerSenders.get(player.getUUID());
        if (sender == null) {
            return;
        }
        for (Named<PoiConfig> holder : MapConfigs.POIS) {
            if (isAccessibleFor(holder, player)) {
                // Bit overkill, but resend it fully to update the lock icon for disabled but accessible POIs
                sender.addOrUpdate(player, holder);
            } else {
                sender.remove(player, holder.key());
            }
        }
    }

    private void refreshAccessiblePois(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            refreshAccessiblePois(player);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        MinecraftServer server = event.getServer();
        if (server.getTickCount() % SharedConstants.TICKS_PER_SECOND != 0) {
            return;
        }
        MapManager manager = MapManager.get(server);
        manager.updatePlayerFaces(server, assignPlayerFaces(server.getPlayerList()));
    }

    private void updatePlayerFaces(MinecraftServer server, Map<ResourceKey<PoiConfig>, Set<UUID>> newFacesByPoi) {
        for (ResourceKey<PoiConfig> id : Sets.union(facesByPoi.keySet(), newFacesByPoi.keySet())) {
            Set<UUID> oldFaces = facesByPoi.getOrDefault(id, Set.of());
            Set<UUID> newFaces = newFacesByPoi.getOrDefault(id, Set.of());
            if (newFaces.equals(oldFaces)) {
                continue;
            }
            for (Map.Entry<UUID, PlayerSender> entry : playerSenders.entrySet()) {
                ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                if (player != null) {
                    entry.getValue().updateFaces(player, id, newFaces);
                }
            }
        }
        facesByPoi = newFacesByPoi;
    }

    private static Map<ResourceKey<PoiConfig>, Set<UUID>> assignPlayerFaces(PlayerList playerList) {
        Map<ResourceKey<PoiConfig>, Set<UUID>> assignedFaces = new HashMap<>();
        for (ServerPlayer player : playerList.getPlayers()) {
            if (!FacePredicate.shouldDrawFace(player)) {
                continue;
            }
            MapConfigs.POIS.stream()
                    .filter(holder -> holder.value().map().value().dimension() == player.level().dimension())
                    .min(Comparator.comparingDouble(holder -> holder.value().pos().distSqr(player.blockPosition())))
                    .ifPresent(nearestPoi -> {
                        Set<UUID> facesForPoi = assignedFaces.computeIfAbsent(nearestPoi.key(), i -> new ObjectOpenHashSet<>());
                        facesForPoi.add(player.getUUID());
                    });
        }
        return assignedFaces;
    }

    private static class FacePredicate {
        private static final RoleOverrideType<Boolean> HOST_ROLE = (RoleOverrideType<Boolean>) RoleOverrideType.byId("host");

        public static boolean shouldDrawFace(ServerPlayer player) {
            return PermissionsApi.lookup().byPlayer(player).overrides().test(HOST_ROLE);
        }
    }

    private class PlayerSender {
        private static final int UNKNOWN_POI = -1;

        private final Object2IntMap<ResourceKey<PoiConfig>> knownPois = new Object2IntOpenHashMap<>();
        private int nextNetworkId;

        private PlayerSender() {
            knownPois.defaultReturnValue(UNKNOWN_POI);
        }

        public void addOrUpdate(ServerPlayer player, Named<PoiConfig> holder) {
            int networkId = knownPois.computeIfAbsent(holder.key(), i -> nextNetworkId++);
            PacketDistributor.sendToPlayer(player, createUpdatePacket(networkId, holder.key(), holder.value()));
        }

        public void remove(ServerPlayer player, ResourceKey<PoiConfig> id) {
            int networkId = knownPois.removeInt(id);
            if (networkId != UNKNOWN_POI) {
                PacketDistributor.sendToPlayer(player, new ClientboundRemovePoiPacket(networkId));
            }
        }

        public void dropAll(ServerPlayer player, Predicate<ResourceKey<PoiConfig>> predicate) {
            knownPois.object2IntEntrySet().removeIf(entry -> {
                if (predicate.test(entry.getKey())) {
                    PacketDistributor.sendToPlayer(player, new ClientboundRemovePoiPacket(entry.getIntValue()));
                    return true;
                }
                return false;
            });
        }

        private ClientboundUpdatePoiPacket createUpdatePacket(int networkId, ResourceKey<PoiConfig> id, PoiConfig config) {
            Component description = config.description();
            if (disabledPois.contains(id)) {
                description = lockedDescription(description);
            }
            MapConfig map = config.map().value();
            int markerX = map.markerX(config.pos());
            int markerY = map.markerY(config.pos());
            return new ClientboundUpdatePoiPacket(networkId, id, config.map(), description, config.icon(), markerX, markerY);
        }

        private static Component lockedDescription(Component description) {
            return Component.empty()
                    .append(Component.literal("🔒 ").withStyle(ChatFormatting.RED))
                    .append(description)
                    .withStyle(ChatFormatting.GRAY);
        }

        public void updateFaces(ServerPlayer player, ResourceKey<PoiConfig> id, Set<UUID> faces) {
            int networkId = knownPois.getInt(id);
            if (networkId != UNKNOWN_POI) {
                player.connection.send(new ClientboundPoiFacesPacket(networkId, List.copyOf(faces)));
            }
        }
    }
}
