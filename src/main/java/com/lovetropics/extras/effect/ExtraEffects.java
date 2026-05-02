package com.lovetropics.extras.effect;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.registries.Registries;
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

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class ExtraEffects {
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, LTExtras.MODID);

    public static final DeferredHolder<MobEffect, FishEyeEffect> FISH_EYE = REGISTER.register("fish_eye", () -> new FishEyeEffect(MobEffectCategory.BENEFICIAL, 0x75d7ff));

    public static final DeferredHolder<MobEffect, ForkliftBoostEffect> FORKLIFT_BOOST = REGISTER.register("forklift_boost", () -> new ForkliftBoostEffect(MobEffectCategory.BENEFICIAL, 0x75f7ff));

    public static final DeferredHolder<MobEffect, PropaguledEffect> PROPAGULED = REGISTER.register("propaguled", () -> new PropaguledEffect(MobEffectCategory.HARMFUL, 0x00ddcc).addAttributeModifier(Attributes.MOVEMENT_SPEED, LTExtras.id("effects.propaguled"), -0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    // Todo Code Updates
//    public static final Map<ModelModifierTypeLegacy, DeferredHolder<MobEffect, ModelEffect>> MODEL_EFFECTS = new HashMap<>();
//
//    static {
//        for (ModelModifierTypeLegacy value : ModelModifierTypeLegacy.values()) {
//            if (value.getEffectName() == null) {
//                continue;
//            }
//            ModelEffect modelEffect = new ModelEffect(MobEffectCategory.NEUTRAL, 0x000000, value);
//            if (value == ModelModifierTypeLegacy.SHRUGGY_ARMS) {
//                modelEffect.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, LTExtras.location("small_arms_block_range"), -3, AttributeModifier.Operation.ADD_VALUE);
//                modelEffect.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, LTExtras.location("small_arms_entity_range"), -3, AttributeModifier.Operation.ADD_VALUE);
//            } else if (value == ModelModifierTypeLegacy.ENDER_ARMS) {
//                modelEffect.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, LTExtras.location("long_arms_block_range"), 3, AttributeModifier.Operation.ADD_VALUE);
//                modelEffect.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, LTExtras.location("long_arms_entity_range"), 3, AttributeModifier.Operation.ADD_VALUE);
//            }
//            MODEL_EFFECTS.put(value, REGISTER.register("mm_" + value.getSerializedName(), () -> modelEffect));
//        }
//
//    }

    static {
        for (ModelModifierType value : ModelModifierType.values()) {
            if (value.getEffectName() == null) {
                continue;
            }
            ModelEffect modelEffect = new ModelEffect(MobEffectCategory.NEUTRAL, 0x000000, value);
            if (value == ModelModifierType.SHRUGGY_ARMS) {
                modelEffect.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, LTExtras.id("small_arms_block_range"), -3, AttributeModifier.Operation.ADD_VALUE);
                modelEffect.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, LTExtras.id("small_arms_entity_range"), -3, AttributeModifier.Operation.ADD_VALUE);
            } else if (value == ModelModifierType.ENDER_ARMS) {
                modelEffect.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, LTExtras.id("long_arms_block_range"), 3, AttributeModifier.Operation.ADD_VALUE);
                modelEffect.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, LTExtras.id("long_arms_entity_range"), 3, AttributeModifier.Operation.ADD_VALUE);
            }
            MODEL_EFFECTS.put(value, REGISTER.register("mm_" + value.getSerializedName(), () -> modelEffect));
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
        if (mobEffect instanceof ModelEffect modelEffect) {
//            ModelModifierStore.removeModifier(entity, modelEffect.getType());
        }
    }
}
