package com.lovetropics.extras.effect;

import com.lovetropics.extras.sounds.ExtraSounds;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ForkliftBoostEffect extends MobEffect {
    public ForkliftBoostEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.withSoundOnAdded(ExtraSounds.FORKLIFT_BOOST_START.value());
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
    }

}
