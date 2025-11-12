package com.lovetropics.extras.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class ExtraArmPoses {
    public static final EnumProxy<HumanoidModel.ArmPose> BOX_PROXY = new EnumProxy<>(HumanoidModel.ArmPose.class,
            true, (IArmPoseTransformer) ExtraArmPoses::applyBoxTransform
    );

    private static void applyBoxTransform(HumanoidModel<?> model, HumanoidRenderState humanoidRenderState, HumanoidArm humanoidArm) {
        model.rightArm.xRot = -45.0f * Mth.DEG_TO_RAD;
        model.leftArm.xRot = -45.0f * Mth.DEG_TO_RAD;
    }
}
