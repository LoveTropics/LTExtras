package com.lovetropics.extras.environmentattribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record ExtraParticles(List<Particle> particles) {
    public static final ExtraParticles EMPTY = new ExtraParticles(List.of());

    public static final Codec<ExtraParticles> CODEC = Particle.CODEC.listOf().xmap(ExtraParticles::new, ExtraParticles::particles);

    public record Particle(ParticleOptions particle, int count, int range, Vec3i offset) {
        public static final Codec<Particle> CODEC = RecordCodecBuilder.create(i -> i.group(
                ParticleTypes.CODEC.fieldOf("options").forGetter(Particle::particle),
                ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(Particle::count),
                ExtraCodecs.POSITIVE_INT.fieldOf("range").forGetter(Particle::range),
                Vec3i.CODEC.optionalFieldOf("offset", BlockPos.ZERO).forGetter(Particle::offset)
        ).apply(i, Particle::new));
    }
}
