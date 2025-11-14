package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.entity.animation.AnimationUtils;
import com.lovetropics.extras.client.model_modifer.ModelModifier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public record ShuffleWalkModifier() implements ModelModifier {
    @Override
    public void applyToModel(LivingEntityRenderState state, EntityModel<?> model) {
        if (!(state instanceof HumanoidRenderState humanoidState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        float walkPos = state.walkAnimationPos;
        float scale = state.walkAnimationSpeed / humanoidState.speedValue;

        float walkModifier = 1.6f;
        float legSwing = AnimationUtils.squareSin(walkPos * walkModifier, 0.5f);
        humanoidModel.rightLeg.xRot = legSwing * 0.5f * scale;
        humanoidModel.leftLeg.xRot = -legSwing * 0.5f * scale;
        float armSwing = AnimationUtils.squareSin(walkPos * walkModifier + Mth.PI, 0.5f);
        humanoidModel.rightArm.xRot = armSwing * 0.5f * scale;
        humanoidModel.leftArm.xRot = -armSwing * 0.5f * scale;
    }
}
