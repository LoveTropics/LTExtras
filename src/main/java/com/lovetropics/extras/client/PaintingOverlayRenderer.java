package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.PaintingOverlay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class PaintingOverlayRenderer {
    private static final ContextKey<List<OverlayRenderState>> PAINTING_OVERLAY = new ContextKey<>(LTExtras.id("painting_overlay"));

    @SubscribeEvent
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        ItemModelResolver itemModelResolver = Minecraft.getInstance().getItemModelResolver();
        event.registerEntityModifier(PaintingRenderer.class, (painting, paintingRenderState) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            ItemStack headgear = player.getItemBySlot(EquipmentSlot.HEAD);
            List<PaintingOverlay> paintingOverlays = headgear.getOrDefault(ExtraDataComponents.PAINTING_OVERLAY, List.of());
            if (paintingOverlays.isEmpty()) {
                return;
            }
            List<OverlayRenderState> overlayRenderStates = new ArrayList<>(paintingOverlays.size());
            for (PaintingOverlay paintingOverlay : paintingOverlays) {
                if (paintingRenderState.variant != null && paintingOverlay.painting().value() == paintingRenderState.variant) {
                    ItemStackRenderState item;
                    if (paintingOverlay.item().isPresent()) {
                        item = new ItemStackRenderState();
                        itemModelResolver.updateForNonLiving(item, paintingOverlay.item().get().create(), ItemDisplayContext.FIXED, painting);
                    } else {
                        item = null;
                    }
                    overlayRenderStates.add(new OverlayRenderState(
                            player.tickCount,
                            paintingOverlay.text().orElse(null),
                            item
                    ));
                }
            }
            paintingRenderState.setRenderData(PAINTING_OVERLAY, overlayRenderStates);
        });
    }

    public static void renderOverlay(PaintingRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector) {
        List<OverlayRenderState> overlays = renderState.getRenderData(PAINTING_OVERLAY);
        if (overlays == null) {
            return;
        }
        for (OverlayRenderState overlay : overlays) {
            render(renderState, poseStack, collector, overlay);
        }
    }

    private static void render(PaintingRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, OverlayRenderState overlay) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - renderState.direction.get2DDataValue() * 90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        final float scaleAdjust = renderState.variant.width() / 50f + Mth.sin(overlay.tickCount % 32 * Mth.PI / 32) / 20f;

        if (overlay.text != null) {
            final int textLength = overlay.text.getString().length();

            // Bring text in front of painting
            poseStack.translate(0, 0, -0.1f);
            // Scale text way down
            final float scale = 0.01f + scaleAdjust;
            poseStack.scale(scale, scale, scale);

            collector.submitText(
                    poseStack,
                    -textLength * 2,
                    -3,
                    overlay.text.getVisualOrderText(),
                    false,
                    Font.DisplayMode.NORMAL,
                    LightCoordsUtil.lightCoordsWithEmission(renderState.lightCoords, 2),
                    CommonColors.WHITE,
                    0,
                    0
            );
        } else if (overlay.item != null) {
            final float scale = 1.f + scaleAdjust;
            poseStack.scale(scale, scale, scale);
            overlay.item.submit(poseStack, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, EntityRenderState.NO_OUTLINE);
        }

        poseStack.popPose();
    }

    private record OverlayRenderState(
            int tickCount,
            @Nullable Component text,
            @Nullable ItemStackRenderState item
    ) {
    }
}
