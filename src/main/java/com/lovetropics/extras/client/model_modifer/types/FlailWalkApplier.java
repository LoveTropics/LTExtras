package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.UnitType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public record FlailWalkApplier() implements ModelApplier<UnitType> {
    @Override
    public void applyToModel(UnitType data, LivingEntityRenderState state, EntityModel<?> model) {
        if (!(state instanceof HumanoidRenderState humanoidState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        float walkPos = state.walkAnimationPos;
        float scale = state.walkAnimationSpeed / humanoidState.speedValue;

        humanoidModel.rightArm.xRot = Mth.cos(walkPos * 0.6f + Mth.PI) * 2.0f * scale;
        humanoidModel.leftArm.xRot = Mth.cos(walkPos * 0.6f) * 2.0f * scale;
        humanoidModel.rightArm.zRot = (Mth.cos(walkPos * 0.2312f) + 1.0f) * scale;
        humanoidModel.leftArm.zRot = (Mth.cos(walkPos * 0.2812f) - 1.0f) * scale;
    }
}
