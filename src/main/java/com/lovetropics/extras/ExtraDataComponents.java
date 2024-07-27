package com.lovetropics.extras;

import com.lovetropics.extras.collectible.CollectibleMarker;
import com.lovetropics.extras.item.CollectibleCompassItem;
import com.lovetropics.extras.item.ImageData;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

public class ExtraDataComponents {
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(LTExtras.MODID);

    // Components for specific items
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CollectibleMarker>> COLLECTIBLE = REGISTER.registerComponentType(
            "collectible",
            builder -> builder.persistent(CollectibleMarker.CODEC).networkSynchronized(CollectibleMarker.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CollectibleCompassItem.Target>> COLLECTIBLE_TARGET = REGISTER.registerComponentType(
            "collectible_target",
            builder -> builder.persistent(CollectibleCompassItem.Target.CODEC).networkSynchronized(CollectibleCompassItem.Target.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COIN_COUNT = REGISTER.registerComponentType(
            "coin_count",
            builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> TARGETED_ENTITY = REGISTER.registerComponentType(
            "targeted_entity",
            builder -> builder.persistent(UUIDUtil.CODEC)
    );

    // General extensions
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNDROPPABLE = REGISTER.registerComponentType(
            "undroppable",
            builder -> builder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COOLDOWN_OVERRIDE = REGISTER.registerComponentType(
            "cooldown_override",
            builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ImageData>> IMAGE = REGISTER.registerComponentType(
            "image",
            builder -> builder.persistent(ImageData.CODEC)
    );
}
