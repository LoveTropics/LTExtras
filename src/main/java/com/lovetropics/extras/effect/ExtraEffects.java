package com.lovetropics.extras.effect;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraEffects {
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, LTExtras.MODID);

    public static final DeferredHolder<MobEffect, FishEyeEffect> FISH_EYE = REGISTER.register("fish_eye", () -> new FishEyeEffect(MobEffectCategory.BENEFICIAL, 0x75d7ff));

    public static final DeferredHolder<MobEffect, ForkliftBoostEffect> FORKLIFT_BOOST = REGISTER.register("forklift_boost", () -> new ForkliftBoostEffect(MobEffectCategory.BENEFICIAL, 0x75f7ff));

    public static final DeferredHolder<MobEffect, PropaguledEffect> PROPAGULED = REGISTER.register("propaguled", () -> new PropaguledEffect(MobEffectCategory.HARMFUL, 0x00ddcc).addAttributeModifier(Attributes.MOVEMENT_SPEED, LTExtras.location("effects.propaguled"), -0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
}
