package com.lovetropics.extras.client.model_modifer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.LivingEntity;

public interface ModelModifier {

    void modify(HumanoidRenderState renderState, HumanoidModel<?> model);

    void preApply(LivingEntity livingEntity, HumanoidRenderState state);
    
    ModelModifier NO_OP = new ModelModifier() {
        @Override
        public void modify(HumanoidRenderState renderState, HumanoidModel<?> model) {}

        @Override
        public void preApply(LivingEntity livingEntity, HumanoidRenderState state) {}
    };
}
