package com.lovetropics.extras.extension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.AreaEffectCloud;

import java.util.Optional;

public record LingeringPotionExtension(
        Optional<Integer> reapplicationDelay,
        Optional<Float> radius,
        Optional<Float> radiusPerTick,
        Optional<Float> radiusOnUse,
        Optional<Integer> duration,
        Optional<Integer> durationOnUse,
        Optional<Integer> waitTime,
        Optional<ParticleOptions> customParticle
) {

    public static final Codec<LingeringPotionExtension> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("reapplication_delay").forGetter(LingeringPotionExtension::reapplicationDelay),
            Codec.FLOAT.optionalFieldOf("radius").forGetter(LingeringPotionExtension::radius),
            Codec.FLOAT.optionalFieldOf("radius_per_tick").forGetter(LingeringPotionExtension::radiusPerTick),
            Codec.FLOAT.optionalFieldOf("radius_on_use").forGetter(LingeringPotionExtension::radiusOnUse),
            Codec.INT.optionalFieldOf("duration").forGetter(LingeringPotionExtension::duration),
            Codec.INT.optionalFieldOf("duration_on_use").forGetter(LingeringPotionExtension::durationOnUse),
            Codec.INT.optionalFieldOf("wait_time").forGetter(LingeringPotionExtension::waitTime),
            ParticleTypes.CODEC.optionalFieldOf("custom_particle").forGetter(LingeringPotionExtension::customParticle)
    ).apply(i, LingeringPotionExtension::new));


}
