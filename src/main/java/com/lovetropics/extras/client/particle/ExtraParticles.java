package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ExtraParticles {
	public static final DeferredRegister<ParticleType<?>> REGISTER = new DeferredRegister<>(ForgeRegistries.PARTICLE_TYPES, LTExtras.MODID);

	public static final RegistryObject<BasicParticleType> WATER_BARRIER = REGISTER.register("water_barrier", () -> new BasicParticleType(false));
	public static final RegistryObject<BasicParticleType> CHECKPOINT = REGISTER.register("checkpoint", () -> new BasicParticleType(false));

}
