package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.entity.animation.AnimationUtils;
import com.lovetropics.extras.client.model_modifer.ModelModifier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class ShuffleWalkModifier implements ModelModifier {

    @Override
    public void modify(HumanoidRenderState renderState, HumanoidModel<?> model) {
        float walkPos = renderState.walkAnimationPos;
        float scale = renderState.walkAnimationSpeed / renderState.speedValue;

        float walkModifier = 1.6f;
        float legSwing = AnimationUtils.squareSin(walkPos * walkModifier, 0.5f);
        model.rightLeg.xRot = legSwing * 0.5f * scale;
        model.leftLeg.xRot = -legSwing * 0.5f * scale;
        float armSwing = AnimationUtils.squareSin(walkPos * walkModifier + Mth.PI, 0.5f);
        model.rightArm.xRot = armSwing * 0.5f * scale;
        model.leftArm.xRot = -armSwing * 0.5f * scale;
    }

    @Override
    public void preApply(LivingEntity livingEntity, HumanoidRenderState state) {

    }
}
