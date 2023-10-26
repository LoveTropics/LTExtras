package com.lovetropics.extras.world_effect;

import com.lovetropics.extras.network.LTExtrasNetwork;
import com.lovetropics.extras.network.WorldParticleEffectsPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public record ParticlesEffect(List<Particle> particles) implements WorldEffect {
    public static final MapCodec<ParticlesEffect> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Particle.CODEC.listOf().fieldOf("particles").forGetter(ParticlesEffect::particles)
    ).apply(i, ParticlesEffect::new));

    public ParticlesEffect(final FriendlyByteBuf input) {
        this(input.readList(Particle::new));
    }

    public void write(final FriendlyByteBuf output) {
        output.writeCollection(particles, (out, particle) -> particle.write(out));
    }

    @Override
    public void apply(final ServerPlayer player, final boolean immediate) {
        LTExtrasNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new WorldParticleEffectsPacket(this));
    }

    @Override
    public void clear(final ServerPlayer player, final boolean immediate) {
        LTExtrasNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new WorldParticleEffectsPacket((ParticlesEffect) null));
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

        public Particle(final FriendlyByteBuf input) {
            this(
                    readParticle(input, input.readById(BuiltInRegistries.PARTICLE_TYPE)),
                    input.readVarInt(),
                    input.readVarInt(),
                    input.readBlockPos()
            );
        }

        public void write(final FriendlyByteBuf output) {
            output.writeId(BuiltInRegistries.PARTICLE_TYPE, particle.getType());
            particle.writeToNetwork(output);
            output.writeVarInt(count);
            output.writeVarInt(range);
            output.writeBlockPos(new BlockPos(offset));
        }

        private static <T extends ParticleOptions> T readParticle(final FriendlyByteBuf input, final ParticleType<T> type) {
            return type.getDeserializer().fromNetwork(type, input);
        }
    }
}
