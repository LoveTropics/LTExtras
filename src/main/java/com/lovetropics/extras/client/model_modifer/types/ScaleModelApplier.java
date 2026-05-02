package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.ScaleType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public record ScaleModelApplier() implements ModelApplier<ScaleType.Data> {

    @Override
    public void applyToTransforms(ScaleType.Data data, PoseStack poseStack, LivingEntityRenderState state) {
        poseStack.scale(data.scaleX(), data.scaleY(), data.scaleZ());
    }
}
