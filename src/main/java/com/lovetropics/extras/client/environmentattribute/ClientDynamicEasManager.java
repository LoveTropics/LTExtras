package com.lovetropics.extras.client.environmentattribute;

import com.lovetropics.extras.environmentattribute.LevelDynamicEasManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(Dist.CLIENT)
public class ClientDynamicEasManager {
    private static @Nullable LevelDynamicEasManager manager;

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level) {
            if (manager != null) {
                manager.close();
            }
            manager = new LevelDynamicEasManager(level);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if (manager == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            if (!minecraft.isPaused()) {
                manager.tick();
            }
        } else {
            manager.close();
            manager = null;
        }
    }

    public static <Value, Argument> void modifyAttribute(Identifier layerId, EnvironmentAttribute<Value> attribute, AttributeModifier<Value, Argument> modifier, Argument argument, int transitionTicks) {
        if (manager != null) {
            manager.modifyAttribute(layerId, attribute, modifier, argument, transitionTicks);
        }
    }

    public static void clearAttribute(Identifier layerId, EnvironmentAttribute<?> attribute, int transitionTicks) {
        if (manager != null) {
            manager.clearAttribute(layerId, attribute, transitionTicks);
        }
    }
}
