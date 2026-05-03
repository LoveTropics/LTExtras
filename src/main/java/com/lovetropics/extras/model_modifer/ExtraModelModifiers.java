package com.lovetropics.extras.model_modifer;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.registry.ExtraRegistries;
import net.minecraft.resources.ResourceKey;

public class ExtraModelModifiers {

    public static final ResourceKey<ModelModifier<?>> FABULOUS = key("fabulous");
    public static final ResourceKey<ModelModifier<?>> FLAIL = key("flail");
    public static final ResourceKey<ModelModifier<?>> HOVERING = key("hovering");
    public static final ResourceKey<ModelModifier<?>> SHUFFLE = key("shuffle");
    public static final ResourceKey<ModelModifier<?>> UPSIDEDOWN = key("upsidedown");
    public static final ResourceKey<ModelModifier<?>> SHRUNK = key("shrunk");
    public static final ResourceKey<ModelModifier<?>> ENLARGED = key("enlarged");
    public static final ResourceKey<ModelModifier<?>> SHRUGGY_ARMS = key("shruggy_arms");
    public static final ResourceKey<ModelModifier<?>> ENDER_ARMS = key("ender_arms");
    public static final ResourceKey<ModelModifier<?>> STIFF_LEGS = key("stiff_legs");
    public static final ResourceKey<ModelModifier<?>> HOP_WALK = key("hop_walk");
    public static final ResourceKey<ModelModifier<?>> SMALL_ARMS = key("small_arms");
    public static final ResourceKey<ModelModifier<?>> T_POSE = key("t_pose");
    public static final ResourceKey<ModelModifier<?>> PANCAKE = key("pancake");

    private static ResourceKey<ModelModifier<?>> key(String name) {
        return ResourceKey.create(ExtraRegistries.MODEL_MODIFIER, LTExtras.id(name));
    }
}
