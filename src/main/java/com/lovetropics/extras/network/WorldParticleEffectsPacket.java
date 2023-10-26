package com.lovetropics.extras.network;

import com.lovetropics.extras.client.world_effect.WorldParticleEffectHandler;
import com.lovetropics.extras.world_effect.ParticlesEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public record WorldParticleEffectsPacket(@Nullable ParticlesEffect effect) {
    public WorldParticleEffectsPacket(final FriendlyByteBuf input) {
        this(input.<ParticlesEffect>readNullable(ParticlesEffect::new));
    }

    public void write(final FriendlyByteBuf output) {
        output.writeNullable(effect, (out, effect) -> effect.write(out));
    }

    public static void handle(final WorldParticleEffectsPacket packet, final Supplier<NetworkEvent.Context> ctx) {
        WorldParticleEffectHandler.set(packet.effect);
    }
}
