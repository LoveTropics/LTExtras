package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.OffsetType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public record OffsetApplier() implements ModelApplier<OffsetType> {

    @Override
    public void applyToTransforms(OffsetType data, PoseStack poseStack, LivingEntityRenderState state) {
        poseStack.translate(data.x() / 16.0f, data.y() / 16.0f, data.z() / 16.0f);
    }
}
