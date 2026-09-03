package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.UnitType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class EnderArmsApplier implements ModelApplier.Unit {

    @Override
    public void applyToModel(UnitType data, LivingEntityRenderState state, EntityModel<?> model) {
        if (!(state instanceof HumanoidRenderState humanoidState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }
        humanoidModel.leftArm.yScale = humanoidModel.body.yScale + humanoidModel.leftLeg.yScale;
        humanoidModel.rightArm.yScale = humanoidModel.body.yScale + humanoidModel.rightLeg.yScale;
    }
}
