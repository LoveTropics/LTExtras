package com.lovetropics.extras.client.model_modifer;

import com.lovetropics.extras.model_modifer.ModelModifier;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

public interface ModelApplier<T extends ModelModifier<?>> {

    default void applyToModel(T data, LivingEntityRenderState state, EntityModel<?> model) {}

    default void applyToTransforms(T data, PoseStack poseStack, LivingEntityRenderState state) {}

    default void extractRenderState(T data, LivingEntity livingEntity, LivingEntityRenderState state) {}
}
