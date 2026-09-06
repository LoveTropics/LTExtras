package com.lovetropics.extras.environmentattribute;

import com.lovetropics.extras.network.message.ClientboundEnvironmentAttributesPacket;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@EventBusSubscriber
public class ServerDynamicEasManager {
    private static final Map<ResourceKey<Level>, LevelDynamicEasManager> BY_DIMENSION = new Reference2ObjectOpenHashMap<>();

    public static <Value, Argument> void modifyAttribute(ServerLevel level, Identifier layerId, EnvironmentAttribute<Value> attribute, AttributeModifier<Value, Argument> modifier, Argument argument, int transitionTicks) {
        LevelDynamicEasManager manager = getOrCreateForLevel(level);
        manager.modifyAttribute(layerId, attribute, modifier, argument, transitionTicks);

        if (attribute.isSyncable()) {
            PacketDistributor.sendToPlayersInDimension(level, new ClientboundEnvironmentAttributesPacket(
                    layerId,
                    List.of(new ClientboundEnvironmentAttributesPacket.Modifier<>(attribute, modifier, argument)),
                    List.of(),
                    transitionTicks
            ));
        }
    }

    public static boolean clearAttribute(ServerLevel level, Identifier layerId, EnvironmentAttribute<?> attribute, int transitionTicks) {
        LevelDynamicEasManager manager = BY_DIMENSION.get(level.dimension());
        if (manager != null && manager.clearAttribute(layerId, attribute, transitionTicks)) {
            if (attribute.isSyncable()) {
                PacketDistributor.sendToPlayersInDimension(level, new ClientboundEnvironmentAttributesPacket(
                        layerId,
                        List.of(),
                        List.of(attribute),
                        transitionTicks
                ));
            }
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        BY_DIMENSION.entrySet().removeIf(entry -> {
            ServerLevel level = server.getLevel(entry.getKey());
            LevelDynamicEasManager manager = entry.getValue();
            if (level == null || manager.tick()) {
                manager.close();
                return true;
            }
            return false;
        });
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            LevelDynamicEasManager manager = BY_DIMENSION.get(player.level().dimension());
            if (manager != null) {
                manager.synchronizeTo(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            MinecraftServer server = player.level().getServer();
            ServerLevel fromLevel = server.getLevel(event.getFrom());
            if (fromLevel != null) {
                LevelDynamicEasManager fromManager = BY_DIMENSION.get(fromLevel.dimension());
                if (fromManager != null) {
                    fromManager.synchronizeRemovalTo(player);
                }
            }
            ServerLevel toLevel = server.getLevel(event.getTo());
            if (toLevel != null) {
                LevelDynamicEasManager fromManager = BY_DIMENSION.get(toLevel.dimension());
                if (fromManager != null) {
                    fromManager.synchronizeTo(player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            LevelDynamicEasManager manager = BY_DIMENSION.remove(level.dimension());
            if (manager != null) {
                manager.close();
            }
        }
    }

    public static Stream<Identifier> streamLayerIds() {
        return BY_DIMENSION.values().stream()
                .flatMap(LevelDynamicEasManager::streamLayerIds)
                .distinct();
    }

    private static LevelDynamicEasManager getOrCreateForLevel(ServerLevel level) {
        return BY_DIMENSION.computeIfAbsent(level.dimension(), _ -> new LevelDynamicEasManager(level));
    }
}
