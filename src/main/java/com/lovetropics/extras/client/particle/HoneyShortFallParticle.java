package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
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
public class HoneyShortFallParticle {
    public static TextureSheetParticle createHoneyFallParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        DripParticle dripparticle = new ShortHoneyParticle(level, x, y, z);
        dripparticle.gravity = 0.01F;
        dripparticle.lifetime = 8;
        dripparticle.setColor(0.582F, 0.448F, 0.082F);
        return dripparticle;
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ExtraParticles.SHORT_HONEY_PARTICLE.get(), HoneyShortFallParticle::createHoneyFallParticle);
    }

    public static class ShortHoneyParticle extends DripParticle {
        public ShortHoneyParticle(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z, Fluids.WATER);
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                float f = Mth.randomBetween(this.random, 0.3F, 1.0F);
                if (this.random.nextFloat() < 0.05f) {
                    this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, f, 1.0F, false);
                }
            }
        }
    }
}
