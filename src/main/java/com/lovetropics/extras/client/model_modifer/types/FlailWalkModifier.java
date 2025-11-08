package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.client.model_modifer.ModelModifier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class FlailWalkModifier implements ModelModifier {

    @Override
    public void modify(HumanoidRenderState renderState, HumanoidModel<?> model) {
        float walkPos = renderState.walkAnimationPos;
        float scale = renderState.walkAnimationSpeed / renderState.speedValue;

        model.rightArm.xRot = Mth.cos(walkPos * 0.6f + Mth.PI) * 2.0f * scale;
        model.leftArm.xRot = Mth.cos(walkPos * 0.6f) * 2.0f * scale;
        model.rightArm.zRot = (Mth.cos(walkPos * 0.2312f) + 1.0f) * scale;
        model.leftArm.zRot = (Mth.cos(walkPos * 0.2812f) - 1.0f) * scale;
    }

    @Override
    public void preApply(LivingEntity livingEntity, HumanoidRenderState state) {

    }
}
