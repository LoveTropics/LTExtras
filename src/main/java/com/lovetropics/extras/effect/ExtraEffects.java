package com.lovetropics.extras.effect;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.model_modifer.ExtraModelModifiers;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierStore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

@EventBusSubscriber
public class ExtraEffects {
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, LTExtras.MODID);

    public static final DeferredHolder<MobEffect, FishEyeEffect> FISH_EYE = REGISTER.register("fish_eye", () -> new FishEyeEffect(MobEffectCategory.BENEFICIAL, 0x75d7ff));

    public static final DeferredHolder<MobEffect, ForkliftBoostEffect> FORKLIFT_BOOST = REGISTER.register("forklift_boost", () -> new ForkliftBoostEffect(MobEffectCategory.BENEFICIAL, 0x75f7ff));

    public static final DeferredHolder<MobEffect, PropaguledEffect> PROPAGULED = REGISTER.register("propaguled", () -> new PropaguledEffect(MobEffectCategory.HARMFUL, 0x00ddcc).addAttributeModifier(Attributes.MOVEMENT_SPEED, LTExtras.id("effects.propaguled"), -0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredHolder<MobEffect, ModelModifierEffect> FABULOUS = modelEffect(ExtraModelModifiers.FABULOUS);
    public static final DeferredHolder<MobEffect, ModelModifierEffect> FLAIL = modelEffect(ExtraModelModifiers.FLAIL);
    public static final DeferredHolder<MobEffect, ModelModifierEffect> HOVERING = modelEffect(ExtraModelModifiers.HOVERING);
    public static final DeferredHolder<MobEffect, ModelModifierEffect> SHUFFLE = modelEffect(ExtraModelModifiers.SHUFFLE);
    public static final DeferredHolder<MobEffect, ModelModifierEffect> UPSIDEDOWN = modelEffect(ExtraModelModifiers.UPSIDEDOWN);
    public static final DeferredHolder<MobEffect, ModelModifierEffect> SHRUNK = modelEffect(ExtraModelModifiers.ENLARGED);
    public static final DeferredHolder<MobEffect, ModelModifierEffect> SHRUGGY_ARMS = modelEffect(ExtraModelModifiers.SHRUGGY_ARMS, effect -> {
        effect.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, LTExtras.id("small_arms_block_range"), -3, AttributeModifier.Operation.ADD_VALUE);
        effect.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, LTExtras.id("small_arms_entity_range"), -3, AttributeModifier.Operation.ADD_VALUE);
        return effect;
    });
    public static final DeferredHolder<MobEffect, ModelModifierEffect> ENDER_ARMS = modelEffect(ExtraModelModifiers.ENDER_ARMS, effect -> {
        effect.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, LTExtras.id("long_arms_block_range"), 3, AttributeModifier.Operation.ADD_VALUE);
        effect.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, LTExtras.id("long_arms_entity_range"), 3, AttributeModifier.Operation.ADD_VALUE);
        return effect;
    });
    public static final DeferredHolder<MobEffect, ModelModifierEffect> STIFF_LEGS = modelEffect(ExtraModelModifiers.STIFF_LEGS);
    public static final DeferredHolder<MobEffect, ModelModifierEffect> HOP_WALK = modelEffect(ExtraModelModifiers.HOP_WALK);


    private static DeferredHolder<MobEffect, ModelModifierEffect> modelEffect(ResourceKey<ModelModifier<?>> modifier, UnaryOperator<ModelModifierEffect> builder) {
        return REGISTER.register("mm_" + modifier.identifier().getPath(), () -> new ModelModifierEffect(MobEffectCategory.NEUTRAL, 0x000000, modifier));
    }

    private static DeferredHolder<MobEffect, ModelModifierEffect> modelEffect(ResourceKey<ModelModifier<?>> modifier) {
        return modelEffect(modifier, effect -> effect);
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        removeModelModifiers(event.getEffect().value(), event.getEntity());
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Expired event) {
        removeModelModifiers(event.getEffectInstance().getEffect().value(), event.getEntity());
    }

    private static void removeModelModifiers(MobEffect mobEffect, LivingEntity entity) {
        if (mobEffect instanceof ModelModifierEffect modelEffect) {
            ModelModifierStore.removeModifier(entity, modelEffect.getEffect(entity));
        }
    }
}
