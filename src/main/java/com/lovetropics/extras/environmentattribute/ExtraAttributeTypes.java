package com.lovetropics.extras.environmentattribute;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.AttributeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

public class ExtraAttributeTypes {
    public static final DeferredRegister<AttributeType<?>> REGISTER = DeferredRegister.create(Registries.ATTRIBUTE_TYPE, LTExtras.MODID);

    public static final AttributeType<ExtraParticles> EXTRA_PARTICLES = AttributeType.ofNotInterpolated(ExtraParticles.CODEC, Map.of());

    public static void register(IEventBus modBus) {
        REGISTER.register(modBus);
        REGISTER.register("extra_particles", () -> EXTRA_PARTICLES);
    }
}
