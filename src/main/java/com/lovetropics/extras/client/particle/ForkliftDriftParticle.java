package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ForkliftDriftParticle {

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ExtraParticles.FORK_LIFT_DRIFT_PARTICLE.get(), PlayerCloudParticle.Provider::new);
    }

}
