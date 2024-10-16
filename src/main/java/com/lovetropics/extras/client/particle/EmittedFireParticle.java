package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EmittedFireParticle extends EmittedRaisingParticle {
	EmittedFireParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
		super(world, x, y, z, sprites);
	}

	@Override
	public void move(double pX, double pY, double pZ) {
		setBoundingBox(getBoundingBox().move(pX, pY, pZ));
		setLocationFromBoundingbox();
	}

	@Override
	public float getQuadSize(float pScaleFactor) {
		float f = ((float) age + pScaleFactor) / (float) lifetime;
		return quadSize * (1.0F - f * f * 0.5F);
	}

	@Override
	public int getLightColor(float pPartialTick) {
		float f = ((float) age + pPartialTick) / (float) lifetime;
		f = Mth.clamp(f, 0.0F, 1.0F);
		int i = super.getLightColor(pPartialTick);
		int j = i & 255;
		int k = i >> 16 & 255;
		j += (int)(f * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		Minecraft.getInstance().particleEngine.register(ExtraParticles.EMITTED_FIRE_PARTICLE.get(), EmittedFireParticle.Factory::new);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Factory(SpriteSet pSprites) {
			sprites = pSprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new EmittedFireParticle(pLevel, pX, pY, pZ, sprites);
		}
	}
}
