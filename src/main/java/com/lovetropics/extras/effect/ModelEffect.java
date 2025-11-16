package com.lovetropics.extras.effect;

import com.lovetropics.extras.model_modifer.ModelModifierStore;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ModelEffect extends MobEffect {

    private final ModelModifierType type;

    protected ModelEffect(MobEffectCategory category, int color, ModelModifierType modelModifierType) {
        super(category, color);
        this.type = modelModifierType;
    }

    public ModelModifierType getType() {
        return type;
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        ModelModifierStore.addModifier(entity, type);
    }
}
