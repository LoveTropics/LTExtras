package com.lovetropics.extras.model_modifer;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.model_modifer.types.CompositeType;
import com.lovetropics.extras.model_modifer.types.EnderArmsType;
import com.lovetropics.extras.model_modifer.types.FabulousType;
import com.lovetropics.extras.model_modifer.types.FlailType;
import com.lovetropics.extras.model_modifer.types.HopWalkType;
import com.lovetropics.extras.model_modifer.types.HoveringType;
import com.lovetropics.extras.model_modifer.types.OffsetType;
import com.lovetropics.extras.model_modifer.types.RaisedHighHeelsType;
import com.lovetropics.extras.model_modifer.types.ScaleType;
import com.lovetropics.extras.model_modifer.types.ShruggyArmsType;
import com.lovetropics.extras.model_modifer.types.ShuffleType;
import com.lovetropics.extras.model_modifer.types.StiffLegsType;
import com.lovetropics.extras.model_modifer.types.UpsidedownType;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Instrument;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraModelModifierTypes {

    public static final DeferredRegister<ModelModifierType<?>> REGISTER = DeferredRegister.create(ExtraRegistries.MODIFIER_TYPE_KEY, LTExtras.MODID);

    public static final Codec<ModelModifier<?>> CODEC = ExtraRegistries.MODIFIER_TYPES.byNameCodec().dispatch("type", ModelModifier::type, ModelModifierType::codec);
    public static final Codec<Holder<ModelModifier<?>>> REGISTRY_CODEC = RegistryFileCodec.create(ExtraRegistries.MODEL_MODIFIER, CODEC, true);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModelModifier<?>> DIRECT_STREAM_CODEC =
            ByteBufCodecs.registry(ExtraRegistries.MODIFIER_TYPE_KEY)
                    .dispatch(ModelModifier::type, ModelModifierType::streamCodec);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ModelModifier<?>>> STREAM_CODEC = ByteBufCodecs.holder(ExtraRegistries.MODEL_MODIFIER, DIRECT_STREAM_CODEC);

    public static final DeferredHolder<ModelModifierType<?>, ScaleType> SCALE = REGISTER.register("scale", ScaleType::new);
    public static final DeferredHolder<ModelModifierType<?>, EnderArmsType> ENDER_ARMS = REGISTER.register("ender_arms", EnderArmsType::new);
    public static final DeferredHolder<ModelModifierType<?>, FabulousType> FABULOUS = REGISTER.register("fabulous", FabulousType::new);
    public static final DeferredHolder<ModelModifierType<?>, FlailType> FLAIL = REGISTER.register("flail", FlailType::new);
    public static final DeferredHolder<ModelModifierType<?>, HopWalkType> HOP_WALK = REGISTER.register("hop_walk", HopWalkType::new);
    public static final DeferredHolder<ModelModifierType<?>, HoveringType> HOVERING = REGISTER.register("hovering", HoveringType::new);
    public static final DeferredHolder<ModelModifierType<?>, RaisedHighHeelsType> RAISED_HIGH_HEELS = REGISTER.register("raised_high_heels", RaisedHighHeelsType::new);
    public static final DeferredHolder<ModelModifierType<?>, ShruggyArmsType> SHRUGGY_ARMS = REGISTER.register("shruggy_arms", ShruggyArmsType::new);
    public static final DeferredHolder<ModelModifierType<?>, ShuffleType> SHUFFLE =  REGISTER.register("shuffle", ShuffleType::new);
    public static final DeferredHolder<ModelModifierType<?>, UpsidedownType> UPSIDEDOWN = REGISTER.register("upside_down", UpsidedownType::new);
    public static final DeferredHolder<ModelModifierType<?>, OffsetType> OFFSET = REGISTER.register("offset", OffsetType::new);
    public static final DeferredHolder<ModelModifierType<?>, CompositeType> COMPOSITE = REGISTER.register("composite", CompositeType::new);
    public static final DeferredHolder<ModelModifierType<?>, StiffLegsType> STIFF_LEGS = REGISTER.register("stiff_legs", StiffLegsType::new);
}
