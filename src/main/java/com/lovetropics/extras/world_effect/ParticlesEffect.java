package com.lovetropics.extras.world_effect;

import com.lovetropics.extras.network.message.ClientboundWorldParticleEffectsPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record ParticlesEffect(List<Particle> particles) implements WorldEffect {
    public static final MapCodec<ParticlesEffect> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Particle.CODEC.listOf().fieldOf("particles").forGetter(ParticlesEffect::particles)
    ).apply(i, ParticlesEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticlesEffect> STREAM_CODEC = StreamCodec.composite(
            Particle.STREAM_CODEC.apply(ByteBufCodecs.list()), ParticlesEffect::particles,
            ParticlesEffect::new
    );

    @Override
    public void apply(ServerPlayer player, boolean immediate) {
        PacketDistributor.sendToPlayer(player, new ClientboundWorldParticleEffectsPacket(Optional.of(this)));
    }

    @Override
    public void clear(ServerPlayer player, boolean immediate) {
        PacketDistributor.sendToPlayer(player, new ClientboundWorldParticleEffectsPacket(Optional.empty()));
    }

    @Override
    public WorldEffectType type() {
        return WorldEffectType.PARTICLES;
    }

    public record Particle(ParticleOptions particle, int count, int range, Vec3i offset) {
        public static final Codec<Particle> CODEC = RecordCodecBuilder.create(i -> i.group(
                ParticleTypes.CODEC.fieldOf("options").forGetter(Particle::particle),
                Codec.INT.fieldOf("count").forGetter(Particle::count),
                Codec.INT.fieldOf("range").forGetter(Particle::range),
                Vec3i.CODEC.optionalFieldOf("offset", BlockPos.ZERO).forGetter(Particle::offset)
        ).apply(i, Particle::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Particle> STREAM_CODEC = StreamCodec.composite(
                ParticleTypes.STREAM_CODEC, Particle::particle,
                ByteBufCodecs.VAR_INT, Particle::count,
                ByteBufCodecs.VAR_INT, Particle::range,
                BlockPos.STREAM_CODEC.map(Function.identity(), BlockPos::new), Particle::offset,
                Particle::new
        );
    }
}
