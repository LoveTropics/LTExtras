package com.lovetropics.extras.data.poi;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.ClientboundPoiPacket;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.lovetropics.lib.permission.PermissionsApi;
import com.lovetropics.lib.permission.role.RoleOverrideType;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class MapPoiManager extends SavedData {

    //This is used to calculate where to draw POIs on the map item itself. Update when map.png is updated.
    //What I did: Set BlueMap to Flat perspective, disable markers&areas. Move mouse to top left corner of desired map, note coordinates.
    //Make a _square_ screenshot (I used Greenshot), note coordinates of where the screenshot ends. Resize to 256x256 and done.
    public static final BoundingBox MAP_BB = new BoundingBox(2013, 0, 1883, 2910, 0, 2799);
    private static final Codec<Map<String, Poi>> CODEC = Codec.unboundedMap(Codec.STRING, Poi.CODEC);
    private static final String STORAGE_ID = LTExtras.MODID + "_map_poi";
    private final Map<String, Poi> pois;

    public MapPoiManager(final Map<String, Poi> pois) {
        this.pois = pois;
    }

    public MapPoiManager() {
        this.pois = new HashMap<>();
    }

    public static MapPoiManager get(final MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(MapPoiManager::load, MapPoiManager::new, STORAGE_ID);
    }

    @Override
    public CompoundTag save(final CompoundTag tag) {
        tag.put("map_pois", Util.getOrThrow(CODEC.encodeStart(NbtOps.INSTANCE, pois), IllegalStateException::new));
        return tag;
    }

    private static MapPoiManager load(final CompoundTag tag) {
        return CODEC.parse(NbtOps.INSTANCE, tag.get("map_pois")).result()
                .map(result -> new MapPoiManager(new HashMap<>(result)))
                .orElseGet(MapPoiManager::new);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof final ServerPlayer serverPlayer) {
            MapPoiManager.get(serverPlayer.getServer()).getEnabledPois()
                    .forEach(e -> LTExtrasNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientboundPoiPacket(e, false)));
        }
    }

    public Collection<Poi> getAllPois() {
        return pois.values();
    }

    public Set<Poi> getEnabledPois() {
        return pois.values().stream()
                .filter(Poi::enabled)
                .collect(Collectors.toSet());
    }

    private void removeFace(String name, UUID face) {
        final Poi poi = pois.get(name);
        if (poi != null) {
            poi.removeFace(face);
            LTExtrasNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundPoiPacket(poi, false));
        }
        setDirty();
    }

    public void addFace(String name, UUID face) {
        final Poi poi = pois.get(name);
        if (poi != null) {
            getAllPois().stream()
                    .filter(p -> p != poi)
                    .filter(p -> p.faces().contains(face))
                    .forEach(p -> removeFace(p.name(), face));
            poi.addFace(face);
            setDirty();
            LTExtrasNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundPoiPacket(poi, false));
        }
    }

    public void clearFaces() {
        pois.values()
                .stream()
                .filter(Poi::hasFaces)
                .map(Poi::clearFaces)
                .forEach(p -> LTExtrasNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundPoiPacket(p, false)));
        setDirty();
    }

    public Set<Poi> getDisabledPois() {
        return pois.values().stream()
                .filter(Predicate.not(Poi::enabled))
                .collect(Collectors.toSet());
    }

    @Nullable
    public Poi getPoi(final String name) {
        return pois.values().stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void add(final Poi poi) {
        setDirty();
        pois.put(poi.name(), poi);
        updateClients(poi);
    }

    public boolean enable(String name) {
        return setPoiState(name, true);
    }

    public boolean disable(String name) {
        return setPoiState(name, false);
    }

    private boolean setPoiState(final String name, final boolean newState) {
        setDirty();
        Poi poi = pois.get(name);
        if (poi != null) {
            poi.setEnabled(newState);
            setDirty();
            updateClients(poi);
            return true;
        }
        return false;
    }

    private void updateClients(final Poi poi) {
        LTExtrasNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundPoiPacket(poi, false));
    }

    public void remove(String name) {
        final Poi poi = pois.remove(name);
        setDirty();
        LTExtrasNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundPoiPacket(poi, true));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.getServer().getTickCount() % 20 != 0) {
            return;
        }
        final MapPoiManager manager = MapPoiManager.get(event.getServer());
        final Set<ServerPlayer> hosts = getHosts(event.getServer());

        if (hosts.isEmpty()) {
            manager.clearFaces();
            return;
        }

        addNewFacesToPois(manager, hosts);
        removeNoLongerHostingFaces(manager, hosts);
        removeFacesInWrongDimensions(manager, hosts);
    }

    private static void removeFacesInWrongDimensions(MapPoiManager manager, Set<ServerPlayer> hosts) {
        for (Poi poi : manager.getAllPois()) {
            for (ServerPlayer host : hosts) {
                if (poi.faces().contains(host.getUUID()) && poi.globalPos().dimension() != host.level().dimension()) {
                    manager.removeFace(poi.name(), host.getUUID());
                }
            }
        }
    }

    private static Set<ServerPlayer> getHosts(final MinecraftServer server) {
        return server.getPlayerList().getPlayers()
                .stream()
                .filter(FacePredicate::shouldDrawFace)
                .collect(Collectors.toSet());
    }

    private static void addNewFacesToPois(final MapPoiManager manager, final Set<ServerPlayer> hosts) {
        for (ServerPlayer host : hosts) {
            final BlockPos hostPosition = host.blockPosition();
            manager.getEnabledPois()
                    .stream()
                    .filter(p -> p.globalPos().dimension() == host.level().dimension())
                    .min(Comparator.comparingDouble(p -> p.globalPos().pos().distSqr(hostPosition)))
                    .filter(p -> !p.faces().contains(host.getUUID()))
                    .ifPresent(p -> manager.addFace(p.name(), host.getUUID()));
        }
    }

    private static void removeNoLongerHostingFaces(final MapPoiManager manager, final Set<ServerPlayer> currentHosts) {
        for (Poi poi : manager.getAllPois()) {
            for (UUID face : poi.faces()) {
                if (currentHosts.stream().noneMatch(p -> p.getUUID().equals(face))) {
                    manager.removeFace(poi.name(), face);
                }
            }
        }
    }

    private static class FacePredicate {
        private static final RoleOverrideType<Boolean> HOST_ROLE = (RoleOverrideType<Boolean>) RoleOverrideType.byId("host");
        private static final Predicate<ServerPlayer> SPECIAL_RULE = p -> PermissionsApi.lookup().byPlayer(p).overrides().test(HOST_ROLE);

        public static boolean shouldDrawFace(ServerPlayer player) {
            return SPECIAL_RULE.test(player);
        }
    }
}
