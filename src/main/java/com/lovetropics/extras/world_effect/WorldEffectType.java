package com.lovetropics.extras.world_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.StringRepresentable;

public enum WorldEffectType implements StringRepresentable {
    COMPOSITE("composite", CompositeWorldEffect.MAP_CODEC),
    SKY_COLOR("sky_color", SkyColorEffect.CODEC),
    PARTICLES("particles", ParticlesEffect.CODEC),
    ;

    public static final Codec<WorldEffectType> CODEC = StringRepresentable.fromEnum(WorldEffectType::values);

    private final String name;
    private final MapCodec<? extends WorldEffect> codec;

    WorldEffectType(final String name, final MapCodec<? extends WorldEffect> codec) {
        this.name = name;
        this.codec = codec;
    }

    public MapCodec<? extends WorldEffect> codec() {
        return codec;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
