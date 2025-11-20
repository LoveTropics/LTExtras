package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ExtraParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, LTExtras.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WATER_BARRIER = REGISTER.register("water_barrier", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CHECKPOINT = REGISTER.register("checkpoint", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMITTED_PARTICLE = REGISTER.register("emitted_particle", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMITTED_FIRE_PARTICLE = REGISTER.register("emitted_fire_particle", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMITTED_HEARTS_PARTICLE = REGISTER.register("emitted_hearts_particle", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TEAM_CENTS_PARTICLE = REGISTER.register("team_cents", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TEAM_NO_CENTS_PARTICLE = REGISTER.register("team_no_cents", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHORT_HONEY_PARTICLE = REGISTER.register("short_honey", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FORK_LIFT_DRIFT_PARTICLE = REGISTER.register("fork_lift_drift", () -> new SimpleParticleType(false));
}
