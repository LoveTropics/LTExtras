package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.client.model_modifer.ModelModifierClient;
import com.lovetropics.extras.model_modifer.types.CompositeType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

public class CompositeTypeApplier implements ModelApplier<CompositeType.Modifier> {

    @Override
    public void applyToModel(CompositeType.Modifier data, LivingEntityRenderState state, EntityModel<?> model) {
        data.modifiers().forEach(modifier -> ModelModifierClient.Applier.applyToModel(modifier, state, model));
    }

    @Override
    public void applyToTransforms(CompositeType.Modifier data, PoseStack poseStack, LivingEntityRenderState state) {
        data.modifiers().forEach(modifier -> ModelModifierClient.Applier.applyToTransforms(modifier, poseStack, state));
    }

    @Override
    public void extractRenderState(CompositeType.Modifier data, LivingEntity livingEntity, LivingEntityRenderState state) {
        data.modifiers().forEach(modifier -> ModelModifierClient.Applier.extract(modifier, livingEntity, state));
    }
}
