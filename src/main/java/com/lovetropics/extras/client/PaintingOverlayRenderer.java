package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.item.PaintingOverlay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class PaintingOverlayRenderer {

    public static void renderOverlay(PaintingRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack headgear = player.getItemBySlot(EquipmentSlot.HEAD);
        @Nullable List<PaintingOverlay> paintingOverlays = headgear.get(ExtraDataComponents.PAINTING_OVERLAY);
        if(paintingOverlays != null) {
            for (PaintingOverlay paintingOverlay : paintingOverlays) {
                if (renderState.variant != null && paintingOverlay != null && paintingOverlay.painting().value() == renderState.variant) {
                    poseStack.pushPose();
                    poseStack.mulPose(Axis.YP.rotationDegrees(180 - renderState.direction.get2DDataValue() * 90));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180));

                    final float scaleAdjust = renderState.variant.width() / 50f + Mth.sin(player.tickCount % 32 * Mth.PI / 32) / 20f;

                    if (paintingOverlay.text().isPresent()) {
                        final Component textComponent = paintingOverlay.text().get();
                        FormattedCharSequence visualOrderText = textComponent.getVisualOrderText();
                        final int textLength = textComponent.getString().length();

                        // Bring text in front of painting
                        poseStack.translate(0, 0, -0.1f);
                        // Scale text way down
                        final float scale = 0.01f + scaleAdjust;
                        poseStack.scale(scale, scale, scale);

                        // Todo 26.1 Port - Check This Works
                        collector.submitText(
                                poseStack,
                                -textLength * 2,
                                0,
                                visualOrderText,
                                false,
                                Font.DisplayMode.POLYGON_OFFSET,
                                renderState.lightCoords,
                                DyeColor.WHITE.getTextColor(),
                                0,
                                0
                        );
                    } else if (paintingOverlay.itemStack().isPresent()) {
                        final float scale = 1.f + scaleAdjust;
                        poseStack.scale(scale, scale, scale);

                        // Todo 26.1 Port
//                        Minecraft.getInstance().getItemRenderer().renderStatic(paintingOverlay.itemStack().get(), ItemDisplayContext.FIXED, 15728850, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, player.level(), 0);
                    }

                    poseStack.popPose();
                }
            }
        }
    }
}
