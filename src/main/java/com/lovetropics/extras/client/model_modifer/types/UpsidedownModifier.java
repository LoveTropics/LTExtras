package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelModifier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.LivingEntity;

public class UpsidedownModifier implements ModelModifier {

    @Override
    public void modify(HumanoidRenderState renderState, HumanoidModel<?> model) {

    }

    @Override
    public void preApply(LivingEntity livingEntity, HumanoidRenderState state) {
        state.isUpsideDown = true;
    }
}
