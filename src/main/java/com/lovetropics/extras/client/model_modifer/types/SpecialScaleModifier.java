package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelModifier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.LivingEntity;

public record SpecialScaleModifier(float yScale, float yOffset) implements ModelModifier {


    @Override
    public void modify(HumanoidRenderState renderState, HumanoidModel<?> model) {
        float yScale = 1.2f;
        model.body.yScale *= yScale;
        model.leftLeg.yScale *= yScale;
        model.rightLeg.yScale *= yScale;
        model.head.yScale *= yScale;
        model.leftArm.yScale *= yScale;
        model.rightArm.yScale *= yScale;

        model.body.y *= yScale;
        model.leftLeg.y *= yScale;
        model.rightLeg.y *= yScale;
        model.head.y *= yScale;
        model.leftArm.y *= yScale;
        model.rightArm.y *= yScale;

        float y = 5f;
        model.body.y -= y;
        model.leftLeg.y -= y;
        model.rightLeg.y -= y;
        model.head.y -= y;
        model.leftArm.y -= y;
        model.rightArm.y -= y;
    }

    @Override
    public void preApply(LivingEntity livingEntity, HumanoidRenderState state) {

    }
}
