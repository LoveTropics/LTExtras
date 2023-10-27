package com.lovetropics.extras.data.poi;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.ClientboundPoiPacket;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
}
