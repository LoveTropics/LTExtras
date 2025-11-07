package com.lovetropics.extras.client.entity.animation;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.client.ClientRegisterEvents;
import com.lovetropics.extras.client.model_modifer.ModelModifier;
import com.lovetropics.extras.client.model_modifer.ModelModifierClient;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import java.util.List;

public class SpecialWalkAnimator {
    public static final float HEELS_OFFSET = -4.0f;

    public static void apply(LivingEntityRenderState renderState, EntityModel<?> model) {
        if (!(renderState instanceof HumanoidRenderState humanoidRenderState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        List<ModelModifier> renderData = renderState.getRenderData(ModelModifierClient.MODIFIERS);
        if (renderData == null) {
            return;
        }
        for (ModelModifier modifier : renderData) {
            modifier.modify(humanoidRenderState, humanoidModel);
        }

        if (humanoidRenderState.feetEquipment.is(ExtraItems.HIGH_HEELS)) {
            humanoidModel.body.y += HEELS_OFFSET;
            humanoidModel.leftLeg.y += HEELS_OFFSET;
            humanoidModel.rightLeg.y += HEELS_OFFSET;
            humanoidModel.head.y += HEELS_OFFSET;
            humanoidModel.leftArm.y += HEELS_OFFSET;
            humanoidModel.rightArm.y += HEELS_OFFSET;
        }
    }
}
