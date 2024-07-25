package com.lovetropics.extras.world_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public record CompositeWorldEffect(List<WorldEffect> effects) implements WorldEffect {
    public static final Codec<CompositeWorldEffect> CODEC = WorldEffect.CODEC.listOf().xmap(CompositeWorldEffect::new, CompositeWorldEffect::effects);
    public static final MapCodec<CompositeWorldEffect> MAP_CODEC = CODEC.fieldOf("effects");

    @Override
    public void apply(ServerPlayer player, boolean immediate) {
        for (WorldEffect effect : effects) {
            effect.apply(player, immediate);
        }
    }

    @Override
    public void clear(ServerPlayer player, boolean immediate) {
        for (WorldEffect effect : effects) {
            effect.clear(player, immediate);
        }
    }

    @Override
    public WorldEffectType type() {
        return WorldEffectType.COMPOSITE;
    }
}
