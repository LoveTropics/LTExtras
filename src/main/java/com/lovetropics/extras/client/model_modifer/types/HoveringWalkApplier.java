package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.UnitType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public record HoveringWalkApplier() implements ModelApplier.Unit {

    @Override
    public void applyToTransforms(UnitType data, PoseStack poseStack, LivingEntityRenderState state) {
        poseStack.translate(0.0f, -getYOffset(state) / 16.0f, 0.0f);
    }

    private float getYOffset(LivingEntityRenderState state) {
        float walkPos = state.walkAnimationPos;
        return Mth.cos(walkPos * 0.51f) - 5.0f;
    }
}
