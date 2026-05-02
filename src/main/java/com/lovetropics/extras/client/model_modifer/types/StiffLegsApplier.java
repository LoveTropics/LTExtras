package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.UnitType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class StiffLegsApplier implements ModelApplier<UnitType> {
    @Override
    public void applyToModel(UnitType data, LivingEntityRenderState state, EntityModel<?> model) {
        if (!(state instanceof HumanoidRenderState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }
        humanoidModel.leftLeg.xRot = 0.0f;
        humanoidModel.rightLeg.xRot = 0.0f;
    }
}
