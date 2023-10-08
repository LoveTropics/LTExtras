package com.lovetropics.extras.data.spawnitems;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber
public class SpawnItemsReloadListener extends SimpleJsonResourceReloadListener {
    public static final BiMap<ResourceLocation, SpawnItems> REGISTRY = Maps.synchronizedBiMap(HashBiMap.create());

    public SpawnItemsReloadListener(Gson pGson, String pDirectory) {
        super(pGson, pDirectory);
    }

    @SubscribeEvent
    static void onAddReloadListeners(final AddReloadListenerEvent event) {
        event.addListener(new SpawnItemsReloadListener(new GsonBuilder().setLenient().create(), "spawn_items"));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        pProfiler.push("lt:spawn_items");
        REGISTRY.clear();
        pObject.forEach((rl, json) -> REGISTRY.put(rl, Util.getOrThrow(SpawnItems.CODEC.decode(JsonOps.INSTANCE, json), JsonParseException::new).getFirst()));
        pProfiler.pop();
    }
}
