package com.lovetropics.extras.model_modifer;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.model_modifer.types.CompositeType;
import com.lovetropics.extras.model_modifer.types.OperationType;
import com.lovetropics.extras.model_modifer.types.OffsetType;
import com.lovetropics.extras.model_modifer.types.ScaleType;
import com.lovetropics.extras.model_modifer.types.UnitType;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraModelModifierTypes {

    public static final DeferredRegister<ModelModifierType<?>> REGISTER = DeferredRegister.create(ExtraRegistries.MODIFIER_TYPE_KEY, LTExtras.MODID);

    public static final Codec<ModelModifier<?>> CODEC = ExtraRegistries.MODIFIER_TYPES.byNameCodec().dispatch("type", ModelModifier::type, ModelModifierType::codec);

    public static final Codec<Holder<ModelModifier<?>>> REGISTRY_CODEC = RegistryFileCodec.create(ExtraRegistries.MODEL_MODIFIER, CODEC, true);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModelModifier<?>> DIRECT_STREAM_CODEC =
            ByteBufCodecs.registry(ExtraRegistries.MODIFIER_TYPE_KEY).dispatch(ModelModifier::type, ModelModifierType::streamCodec);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ModelModifier<?>>> STREAM_CODEC = ByteBufCodecs.holder(ExtraRegistries.MODEL_MODIFIER, DIRECT_STREAM_CODEC);

    public static final DeferredHolder<ModelModifierType<?>, ScaleType> SCALE = REGISTER.register("scale", ScaleType::new);
    public static final DeferredHolder<ModelModifierType<?>, OffsetType> OFFSET = REGISTER.register("offset", OffsetType::new);
    public static final DeferredHolder<ModelModifierType<?>, CompositeType> COMPOSITE = REGISTER.register("composite", CompositeType::new);
    public static final DeferredHolder<ModelModifierType<?>, OperationType> CONSTANT = REGISTER.register("constant", OperationType::new);

    public static final DeferredHolder<ModelModifierType<?>, UnitType> ENDER_ARMS = unitType("ender_arms");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> FABULOUS = unitType("fabulous");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> FLAIL = unitType("flail");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> HOP_WALK = unitType("hop_walk");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> HOVERING = unitType("hovering");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> SHRUGGY_ARMS = unitType("shruggy_arms");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> SHUFFLE =  unitType("shuffle");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> UPSIDEDOWN = unitType("upside_down");
    public static final DeferredHolder<ModelModifierType<?>, UnitType> NO_SHADOW = unitType("no_shadow");

    private static DeferredHolder<ModelModifierType<?>, UnitType> unitType(String name) {
        return REGISTER.register(name, UnitType::new);
    }
}
