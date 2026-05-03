package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.ScaleType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public record ScaleApplier() implements ModelApplier<ScaleType.Modifier> {

    @Override
    public void applyToTransforms(ScaleType.Modifier data, PoseStack poseStack, LivingEntityRenderState state) {
        poseStack.scale(data.scaleX().orElse(1f), data.scaleY().orElse(1f), data.scaleZ().orElse(1f));
    }
}
