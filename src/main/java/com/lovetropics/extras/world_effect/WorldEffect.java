package com.lovetropics.extras.world_effect;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

public interface WorldEffect {
    Codec<WorldEffect> CODEC = ExtraCodecs.lazyInitializedCodec(() -> {
        final Codec<WorldEffect> typedCodec = WorldEffectType.CODEC.dispatch(WorldEffect::type, type -> type.codec().codec());
        return Codec.either(typedCodec, CompositeWorldEffect.CODEC).xmap(
                either -> either.map(e -> e, e -> e),
                Either::left
        );
    });

    void apply(ServerPlayer player, boolean immediate);

    void clear(ServerPlayer player, final boolean immediate);

    WorldEffectType type();
}
