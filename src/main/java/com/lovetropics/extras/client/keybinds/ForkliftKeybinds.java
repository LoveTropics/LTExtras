package com.lovetropics.extras.client.keybinds;

import com.lovetropics.extras.LTExtras;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class ForkliftKeybinds {
    public static final KeyMapping LIFT_FORK = create("lift_fork", InputConstants.KEY_SPACE, KeyModifier.NONE);
    public static final KeyMapping LOWER_FORK = create("lower_fork", InputConstants.KEY_LCONTROL, KeyModifier.NONE);

    public static void init() {
    }

    private static KeyMapping create(String id, int key, KeyModifier modifier) {
        final String modid = LTExtras.MODID;
        return new KeyMapping("key." + modid + "." + id, KeyConflictContext.IN_GAME, modifier, InputConstants.Type.KEYSYM.getOrCreate(key), "key.categories." + modid + ".lobby");
    }
}
