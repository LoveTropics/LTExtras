package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelModifier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class HoveringWalkModifier implements ModelModifier {

    @Override
    public void modify(HumanoidRenderState renderState, HumanoidModel<?> model) {
        float walkPos = renderState.walkAnimationPos;
        float yOffset = Mth.cos(walkPos * 0.51f) - 5.0f;

        model.body.y += yOffset;
        model.leftLeg.y += yOffset;
        model.leftLeg.xRot *= 0.0f;
        model.leftLeg.yRot *= 0.0f;
        model.rightLeg.y += yOffset;
        model.rightLeg.xRot *= 0.0f;
        model.rightLeg.yRot *= 0.0f;
        model.head.y += yOffset;
        model.leftArm.y += yOffset;
        model.leftArm.xRot *= 0.0f;
        model.leftArm.yRot *= 0.0f;
        model.rightArm.y += yOffset;
        model.rightArm.xRot *= 0.0f;
        model.rightArm.yRot *= 0.0f;
    }

    @Override
    public void preApply(LivingEntity livingEntity, HumanoidRenderState state) {

    }
}
