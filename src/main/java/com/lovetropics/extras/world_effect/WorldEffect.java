package com.lovetropics.extras.world_effect;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;

public interface WorldEffect {
    Codec<WorldEffect> CODEC = Codec.lazyInitialized(() -> {
        Codec<WorldEffect> typedCodec = WorldEffectType.CODEC.dispatch(WorldEffect::type, WorldEffectType::codec);
        return Codec.withAlternative(typedCodec, CompositeWorldEffect.CODEC);
    });

    void apply(ServerPlayer player, boolean immediate);

    void clear(ServerPlayer player, boolean immediate);

    WorldEffectType type();
}
