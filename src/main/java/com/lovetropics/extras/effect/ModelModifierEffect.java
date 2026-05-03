package com.lovetropics.extras.effect;

import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierStore;
import com.lovetropics.extras.registry.ExtraRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ModelModifierEffect extends MobEffect {

    private final ResourceKey<ModelModifier<?>> modifier;

    protected ModelModifierEffect(MobEffectCategory category, int color, ResourceKey<ModelModifier<?>> modifier) {
        super(category, color);
        this.modifier = modifier;
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        ModelModifierStore.addModifier(entity, this.getEffect(entity));
    }

    public Holder.Reference<ModelModifier<?>> getEffect(LivingEntity livingEntity) {
        Registry<ModelModifier<?>> modelModifiers = livingEntity.level().getServer().registryAccess().lookupOrThrow(ExtraRegistries.MODEL_MODIFIER);
        return modelModifiers.getOrThrow(this.modifier);
    }
}
