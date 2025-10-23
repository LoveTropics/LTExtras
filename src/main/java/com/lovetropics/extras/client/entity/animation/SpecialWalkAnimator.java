package com.lovetropics.extras.client.entity.animation;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.item.WalkAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class SpecialWalkAnimator {
    public static void apply(LivingEntityRenderState renderState, EntityModel<?> model) {
        if (renderState instanceof HumanoidRenderState humanoidRenderState && model instanceof HumanoidModel<?> humanoidModel) {
            WalkAnimation walkAnimation = humanoidRenderState.feetEquipment.getOrDefault(ExtraDataComponents.WALK_ANIMATION, WalkAnimation.DEFAULT);
            if (walkAnimation == WalkAnimation.FABULOUS) {
                applyFabulous(humanoidRenderState, humanoidModel);
            }
        }
    }

    private static void applyFabulous(HumanoidRenderState renderState, HumanoidModel<?> model) {
        float walkPos = renderState.walkAnimationPos;
        float scale = renderState.walkAnimationSpeed / renderState.speedValue;

        if (!renderState.isCrouching) {
            model.body.xRot -= 15.0f * Mth.DEG_TO_RAD;
            model.body.y += 1.0f;
            model.head.y += 1.0f;
            model.leftLeg.z -= 3.0f;
            model.rightLeg.z -= 3.0f;
        }

        float swing = AnimationUtils.squareSin(walkPos * 0.6f, 3.0f);
        model.rightArm.xRot = -swing * scale;
        model.leftArm.xRot = swing * scale;
        model.rightLeg.xRot = swing * 1.4f * scale;
        model.leftLeg.xRot = -swing * 1.4f * scale;

        model.body.zRot += swing * 10.0f * Mth.DEG_TO_RAD * scale;
        model.rightLeg.x -= swing * 2.0f * scale;
        model.leftLeg.x -= swing * 2.0f * scale;
    }
}
