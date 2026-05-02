package com.lovetropics.extras.client.keybinds;

import com.lovetropics.extras.LTExtras;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

@EventBusSubscriber(Dist.CLIENT)
public class ForkliftKeybinds {
    public static final KeyMapping RAISE_FORKLIFT = create("forklift_raise",  InputConstants.KEY_R, KeyModifier.NONE);
    public static final KeyMapping LOWER_FORKLIFT = create("forklift_lower",  InputConstants.KEY_F, KeyModifier.NONE);
    public static final KeyMapping DRIFT = create("forklift_drift",  InputConstants.KEY_SPACE, KeyModifier.NONE);
    public static final KeyMapping EJECT_FORK_RIDERS = create("eject_fork_riders", InputConstants.KEY_G, KeyModifier.NONE);

    public static final KeyMapping.Category LOBBY_CATEGORY = new KeyMapping.Category(LTExtras.location("lobby"));

    public static void init() {
    }


    private static KeyMapping create(String id, int key, KeyModifier modifier) {
        final String modid = LTExtras.MODID;
        String description = "key." + modid + "." + id;
        return new KeyMapping(description, KeyConflictContext.IN_GAME, modifier, InputConstants.Type.KEYSYM, key, LOBBY_CATEGORY);
    }


    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(RAISE_FORKLIFT);
        event.register(LOWER_FORKLIFT);
        event.register(DRIFT);
        event.register(EJECT_FORK_RIDERS);
        event.registerCategory(LOBBY_CATEGORY);
    }
}
