package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.ScaleType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public record ScaleApplier() implements ModelApplier<ScaleType> {

    @Override
    public void applyToTransforms(ScaleType data, PoseStack poseStack, LivingEntityRenderState state) {
        poseStack.scale(data.x(), data.y(), data.z());
    }
}
