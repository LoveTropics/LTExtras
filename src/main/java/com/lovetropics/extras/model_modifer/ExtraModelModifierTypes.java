package com.lovetropics.extras.model_modifer;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.model_modifer.types.AnimationType;
import com.lovetropics.extras.model_modifer.types.CompositeType;
import com.lovetropics.extras.model_modifer.types.HopWalkType;
import com.lovetropics.extras.model_modifer.types.OperationType;
import com.lovetropics.extras.model_modifer.types.OffsetType;
import com.lovetropics.extras.model_modifer.types.ScaleType;
import com.lovetropics.extras.model_modifer.types.UnitType;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraModelModifierTypes {

    public static final DeferredRegister<ModelModifierType<?>> REGISTER = DeferredRegister.create(ExtraRegistries.MODIFIER_TYPE_KEY, LTExtras.MODID);

    public static final DeferredHolder<ModelModifierType<?>, ModelModifierType<ScaleType>> SCALE = register("scale", ScaleType.CODEC, ScaleType.STREAM_CODEC);
    public static final DeferredHolder<ModelModifierType<?>, ModelModifierType<OffsetType>> OFFSET = register("offset", OffsetType.CODEC, OffsetType.STREAM_CODEC);
    public static final DeferredHolder<ModelModifierType<?>, ModelModifierType<CompositeType>> COMPOSITE = register("composite", CompositeType.CODEC, CompositeType.STREAM_CODEC);
    public static final DeferredHolder<ModelModifierType<?>, ModelModifierType<OperationType>> OPERATION = register("operation", OperationType.CODEC, OperationType.STREAM_CODEC);
    public static final DeferredHolder<ModelModifierType<?>, ModelModifierType<HopWalkType>> HOP_WALK = register("hop_walk", HopWalkType.CODEC, HopWalkType.STREAM_CODEC);
    public static final DeferredHolder<ModelModifierType<?>, ModelModifierType<AnimationType>> ANIMATION = register("animation", AnimationType.CODEC, AnimationType.STREAM_CODEC);

    public static final DeferredHolder<ModelModifierType<?>, UnitType> ENDER_ARMS = unit("ender_arms");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> FABULOUS = unit("fabulous");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> FLAIL = unit("flail");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> HOVERING = unit("hovering");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> SHRUGGY_ARMS = unit("shruggy_arms");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> SHUFFLE = unit("shuffle");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> UPSIDEDOWN = unit("upsidedown");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> NO_SHADOW = unit("no_shadow");

    private static <T extends ModelModifier<?>> DeferredHolder<ModelModifierType<?>, ModelModifierType<T>> register(String name, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return REGISTER.register(name, () -> ModelModifierType.simple(codec, streamCodec));
    }

    private static DeferredHolder<ModelModifierType<?>, UnitType> unit(String name) {
        return REGISTER.register(name, UnitType::new);
    }
}
