package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.UnitType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

public record UpsidedownApplier() implements ModelApplier<UnitType> {
    @Override
    public void extractRenderState(UnitType data, LivingEntity livingEntity, LivingEntityRenderState state) {
        state.isUpsideDown = true;
    }
}
