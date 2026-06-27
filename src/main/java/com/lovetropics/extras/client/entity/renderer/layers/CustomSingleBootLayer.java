package com.lovetropics.extras.client.entity.renderer.layers;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.CustomBootsItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CustomSingleBootLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {
    private static final ContextKey<ItemStackRenderState> BOOT_ITEM = new ContextKey<>(LTExtras.id("boot_item"));

    public CustomSingleBootLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    public static void updateBootRenderState(LivingEntity entity, LivingEntityRenderState renderState) {
        ItemStack feetItem = entity.getItemBySlot(EquipmentSlot.FEET);
        if (feetItem.getItem() instanceof CustomBootsItem) {
            ItemStackRenderState itemRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForLiving(itemRenderState, feetItem, ItemDisplayContext.FIXED, entity);
            renderState.setRenderData(BOOT_ITEM, itemRenderState);
        }
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, S state, float yRot, float xRot) {
        ItemStackRenderState bootItem = state.getRenderData(BOOT_ITEM);
        if (bootItem != null && !bootItem.isEmpty()) {
            poseStack.pushPose();
            M m = this.getParentModel();
            m.root().translateAndRotate(poseStack);
            m.leftLeg.translateAndRotate(poseStack);
            translateToFeet(poseStack, state.feetEquipment);
            bootItem.submit(poseStack, collector, lightCoords, OverlayTexture.NO_OVERLAY, EntityRenderState.NO_OUTLINE);
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

