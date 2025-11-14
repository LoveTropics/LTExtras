package com.lovetropics.extras.client.model_modifer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface ModelModifier {
    void applyToModel(LivingEntityRenderState state, EntityModel<?> model);

    default void applyToTransforms(PoseStack poseStack, LivingEntityRenderState state) {
    }

    default void extractRenderState(LivingEntity livingEntity, LivingEntityRenderState state) {
    }

    ModelModifier NO_OP = (renderState, model) -> {
    };
}
