package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelModifier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class StiffLegsModifier implements ModelModifier {
    @Override
    public void applyToModel(LivingEntityRenderState state, EntityModel<?> model) {
        if (!(state instanceof HumanoidRenderState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }
        humanoidModel.leftLeg.xRot = 0.0f;
        humanoidModel.rightLeg.xRot = 0.0f;
    }
}
