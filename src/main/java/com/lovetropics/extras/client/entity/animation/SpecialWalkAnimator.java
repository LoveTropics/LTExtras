package com.lovetropics.extras.client.entity.animation;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.item.WalkAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class SpecialWalkAnimator {
    public static final float HEELS_OFFSET = -4.0f;

    public static void apply(LivingEntityRenderState renderState, EntityModel<?> model) {
        if (!(renderState instanceof HumanoidRenderState humanoidRenderState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        WalkAnimation walkAnimation = humanoidRenderState.feetEquipment.getOrDefault(ExtraDataComponents.WALK_ANIMATION, WalkAnimation.DEFAULT);
        if (walkAnimation == WalkAnimation.FABULOUS) {
            applyFabulous(humanoidRenderState, humanoidModel);
        } else if (walkAnimation == WalkAnimation.FLAIL) {
            applyFlail(humanoidRenderState, humanoidModel);
        } else if (walkAnimation == WalkAnimation.HOVERING) {
            applyHovering(humanoidRenderState, humanoidModel);
        }

        if (humanoidRenderState.feetEquipment.is(ExtraItems.HIGH_HEELS)) {
            humanoidModel.body.y += HEELS_OFFSET;
            humanoidModel.leftLeg.y += HEELS_OFFSET;
            humanoidModel.rightLeg.y += HEELS_OFFSET;
            humanoidModel.head.y += HEELS_OFFSET;
            humanoidModel.leftArm.y += HEELS_OFFSET;
            humanoidModel.rightArm.y += HEELS_OFFSET;
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

    private static void applyFlail(HumanoidRenderState renderState, HumanoidModel<?> model) {
        float walkPos = renderState.walkAnimationPos;
        float scale = renderState.walkAnimationSpeed / renderState.speedValue;

        model.rightArm.xRot = Mth.cos(walkPos * 0.6f + Mth.PI) * 2.0f * scale;
        model.leftArm.xRot = Mth.cos(walkPos * 0.6f) * 2.0f * scale;
        model.rightArm.zRot = (Mth.cos(walkPos * 0.2312f) + 1.0f) * scale;
        model.leftArm.zRot = (Mth.cos(walkPos * 0.2812f) - 1.0f) * scale;
    }

    private static void applyHovering(HumanoidRenderState renderState, HumanoidModel<?> model) {
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
}
