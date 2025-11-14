package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelModifier;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public record ScaleModifier(float x, float y, float z) implements ModelModifier {
    @Override
    public void applyToModel(LivingEntityRenderState state, EntityModel<?> model) {
    }

    @Override
    public void applyToTransforms(PoseStack poseStack, LivingEntityRenderState state) {
        poseStack.scale(x, y, z);
    }
}
