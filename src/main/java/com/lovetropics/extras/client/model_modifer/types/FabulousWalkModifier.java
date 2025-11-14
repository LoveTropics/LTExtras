package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.entity.animation.AnimationUtils;
import com.lovetropics.extras.client.model_modifer.ModelModifier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public record FabulousWalkModifier() implements ModelModifier {
    @Override
    public void applyToModel(LivingEntityRenderState state, EntityModel<?> model) {
        if (!(state instanceof HumanoidRenderState humanoidState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        float walkPos = humanoidState.walkAnimationPos;
        float scale = humanoidState.walkAnimationSpeed / humanoidState.speedValue;

        if (!humanoidState.isCrouching) {
            humanoidModel.body.xRot -= 15.0f * Mth.DEG_TO_RAD;
            humanoidModel.body.y += 1.0f;
            humanoidModel.head.y += 1.0f;
            humanoidModel.leftLeg.z -= 3.0f;
            humanoidModel.rightLeg.z -= 3.0f;
        }

        float swing = AnimationUtils.squareSin(walkPos * 0.6f, 3.0f);
        humanoidModel.rightArm.xRot = -swing * scale;
        humanoidModel.leftArm.xRot = swing * scale;
        humanoidModel.rightLeg.xRot = swing * 1.4f * scale;
        humanoidModel.leftLeg.xRot = -swing * 1.4f * scale;

        humanoidModel.body.zRot += swing * 10.0f * Mth.DEG_TO_RAD * scale;
        humanoidModel.rightLeg.x -= swing * 2.0f * scale;
        humanoidModel.leftLeg.x -= swing * 2.0f * scale;
    }
}
