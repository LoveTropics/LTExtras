package com.lovetropics.extras.model_modifer;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;

public class ExtraModelModifiers {

    public static final Codec<ModelModifier<?>> CODEC = ExtraRegistries.MODIFIER_TYPES.byNameCodec()
            .dispatch("type", ModelModifier::type, ModelModifierType::codec);
    public static final Codec<Holder<ModelModifier<?>>> REGISTRY_CODEC = RegistryFileCodec.create(ExtraRegistries.MODEL_MODIFIER, CODEC, true);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModelModifier<?>> DIRECT_STREAM_CODEC = ByteBufCodecs.registry(ExtraRegistries.MODIFIER_TYPE_KEY)
            .dispatch(ModelModifier::type, ModelModifierType::streamCodec);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ModelModifier<?>>> STREAM_CODEC = ByteBufCodecs.holder(ExtraRegistries.MODEL_MODIFIER, DIRECT_STREAM_CODEC);

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
    public static final ResourceKey<ModelModifier<?>> DANACE = key("dance");

    private static ResourceKey<ModelModifier<?>> key(String name) {
        return ResourceKey.create(ExtraRegistries.MODEL_MODIFIER, LTExtras.id(name));
    }
}
