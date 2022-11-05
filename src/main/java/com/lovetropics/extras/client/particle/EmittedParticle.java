package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EmittedParticle extends TextureSheetParticle {
	EmittedParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
		super(world, x, y, z);
		this.lifetime = 80;
		this.gravity = 0.5f;
		this.yd = 0.75f;
		this.xd = (Math.random() - Math.random()) * 0.05;
		this.zd = (Math.random() - Math.random()) * 0.05;
		this.setSize(0.5f, 0.5f);

		float f = (float)(Math.random() * (double)0.3F + (double)0.6F);
		this.rCol = f;
		this.gCol = f;
		this.bCol = f;

		pickSprite(sprites);
	}

	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(ExtraParticles.EMITTED_PARTICLE.get(), EmittedParticle.Factory::new);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Factory(SpriteSet pSprites) {
			this.sprites = pSprites;
		}

		public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new EmittedParticle(pLevel, pX, pY, pZ, this.sprites);
		}
	}
}
