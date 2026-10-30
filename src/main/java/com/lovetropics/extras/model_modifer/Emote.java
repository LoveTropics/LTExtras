package com.lovetropics.extras.model_modifer;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record Emote(
        List<Holder<ModelModifier>> modifiers
) {
    public static final Codec<Emote> CODEC = ExtraCodecs.compactListCodec(ExtraModelModifiers.REGISTRY_CODEC).xmap(Emote::new, Emote::modifiers);
    public static final StreamCodec<RegistryFriendlyByteBuf, Emote> STREAM_CODEC = StreamCodec.composite(
            ExtraModelModifiers.STREAM_CODEC.apply(ByteBufCodecs.list()), Emote::modifiers,
            Emote::new
    );
}
