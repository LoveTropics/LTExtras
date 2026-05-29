package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(Dist.CLIENT)
public final class ExtraParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, LTExtras.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMITTED_PARTICLE = REGISTER.register("emitted_particle", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMITTED_FIRE_PARTICLE = REGISTER.register("emitted_fire_particle", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMITTED_HEARTS_PARTICLE = REGISTER.register("emitted_hearts_particle", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TEAM_CENTS_PARTICLE = REGISTER.register("team_cents", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TEAM_NO_CENTS_PARTICLE = REGISTER.register("team_no_cents", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHORT_HONEY_PARTICLE = REGISTER.register("short_honey", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FORK_LIFT_DRIFT_PARTICLE = REGISTER.register("fork_lift_drift", () -> new SimpleParticleType(false));

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(EMITTED_HEARTS_PARTICLE.get(), EmittedHeartsParticle.Factory::new);
        event.registerSpriteSet(EMITTED_FIRE_PARTICLE.get(), EmittedFireParticle.Factory::new);
        event.registerSpriteSet(EMITTED_PARTICLE.get(), EmittedParticle.Factory::new);
        event.registerSpriteSet(FORK_LIFT_DRIFT_PARTICLE.get(), PlayerCloudParticle.Provider::new);
        event.registerSpriteSet(SHORT_HONEY_PARTICLE.get(), HoneyShortFallParticle.Factory::new);
        event.registerSpriteSet(TEAM_CENTS_PARTICLE.get(), TeamParticle.TeamFactory::new);
        event.registerSpriteSet(TEAM_NO_CENTS_PARTICLE.get(), TeamParticle.TeamFactory::new);
    }


}
