package com.lovetropics.extras.client.entity.renderer.layers;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.item.CustomBootsItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.ItemStack;

public class CustomSingleBootLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {
    public CustomSingleBootLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, S state, float yRot, float xRot) {
        if (!state.feetEquipment.isEmpty() && state.feetEquipment.getItem() instanceof CustomBootsItem) {
            poseStack.pushPose();
            M m = this.getParentModel();
            m.root().translateAndRotate(poseStack);
            m.leftLeg.translateAndRotate(poseStack);
            translateToFeet(poseStack, state.feetEquipment);
            // Todo 26.1 Port
//            collector.submitItem(state.feetEquipment, ItemDisplayContext.FIXED, packedLight, 0, poseStack, bufferSource, null, 0);
//            Minecraft.getInstance().getItemRenderer().renderStatic(renderState.feetEquipment, ItemDisplayContext.FIXED, packedLight, 0, poseStack, bufferSource, null, 0);
            poseStack.popPose();
        }
    }

    public void translateToFeet(PoseStack poseStack, ItemStack stack) {
        var y = 0.05 + stack.getOrDefault(ExtraDataComponents.ADJUST_HEIGHT, 0.0f);
        poseStack.translate(-0.125F, y, -0.4F);
        poseStack.scale(3.4f, 3.4f, 3.4f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
    }
}

