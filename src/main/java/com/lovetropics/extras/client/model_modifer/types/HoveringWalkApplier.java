package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.UnitType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public record HoveringWalkApplier() implements ModelApplier<UnitType> {
    @Override
    public void applyToModel(UnitType data, LivingEntityRenderState state, EntityModel<?> model) {
        if (!(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }
        humanoidModel.leftLeg.xRot = 0.0f;
        humanoidModel.leftLeg.yRot = 0.0f;
        humanoidModel.rightLeg.xRot = 0.0f;
        humanoidModel.rightLeg.yRot = 0.0f;
        humanoidModel.leftArm.xRot = 0.0f;
        humanoidModel.leftArm.yRot = 0.0f;
        humanoidModel.rightArm.xRot = 0.0f;
        humanoidModel.rightArm.yRot = 0.0f;
    }

    @Override
    public void applyToTransforms(UnitType data, PoseStack poseStack, LivingEntityRenderState state) {
        poseStack.translate(0.0f, getYOffset(state) / 16.0f, 0.0f);
    }

    private float getYOffset(LivingEntityRenderState state) {
        float walkPos = state.walkAnimationPos;
        return Mth.cos(walkPos * 0.51f) - 5.0f;
    }
}
