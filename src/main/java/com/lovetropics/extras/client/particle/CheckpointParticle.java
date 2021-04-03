package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CheckpointParticle extends SpriteTexturedParticle {
	CheckpointParticle(ClientWorld world, double x, double y, double z, IItemProvider item) {
		super(world, x, y, z);
		this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(item));
		this.particleGravity = 0.0F;
		this.maxAge = 80;
		this.canCollide = false;
	}

	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particles.registerFactory(ExtraParticles.CHECKPOINT.get(), new Factory());
	}

	public IParticleRenderType getRenderType() {
		return IParticleRenderType.TERRAIN_SHEET;
	}

	@Override
	public float getScale(float scaleFactor) {
		return 0.5F;
	}

	public static class Factory implements IParticleFactory<BasicParticleType> {
		@Override
		public Particle makeParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new CheckpointParticle(world, x, y, z, Blocks.STRUCTURE_VOID.asItem());
		}
	}
}
