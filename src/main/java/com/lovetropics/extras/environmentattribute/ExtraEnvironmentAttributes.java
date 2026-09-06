package com.lovetropics.extras.environmentattribute;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraEnvironmentAttributes {
    public static final DeferredRegister<EnvironmentAttribute<?>> REGISTER = DeferredRegister.create(Registries.ENVIRONMENT_ATTRIBUTE, LTExtras.MODID);

    public static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<ExtraParticles>> EXTRA_PARTICLES = REGISTER.register(
            "extra_particles",
            () -> EnvironmentAttribute.builder(ExtraAttributeTypes.EXTRA_PARTICLES)
                    .defaultValue(ExtraParticles.EMPTY)
                    .syncable()
                    .build()
    );
}
