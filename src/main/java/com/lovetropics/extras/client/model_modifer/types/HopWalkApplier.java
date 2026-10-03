package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.entity.animation.AnimationUtils;
import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.HopWalkType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public record HopWalkApplier() implements ModelApplier<HopWalkType> {
    @Override
    public void applyToModel(HopWalkType type, LivingEntityRenderState state, EntityModel<?> model) {
        if (!(state instanceof HumanoidRenderState humanoidState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        float walkPos = humanoidState.walkAnimationPos;
        float scale = humanoidState.walkAnimationSpeed / humanoidState.speedValue;

        float hopAmount = (AnimationUtils.squareSin(walkPos, 1.75f) - 1) * type.hopAmount() * scale;
        humanoidModel.root().y += hopAmount;
    }
}
