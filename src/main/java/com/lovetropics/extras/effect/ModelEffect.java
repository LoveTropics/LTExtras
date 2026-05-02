package com.lovetropics.extras.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ModelEffect extends MobEffect {

//    private final ModelModifierTypeLegacy type;

    protected ModelEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
//        ModelModifierStore.addModifier(entity, type);
    }
}
