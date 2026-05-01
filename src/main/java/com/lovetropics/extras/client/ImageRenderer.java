package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.ImageData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.CommonColors;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderItemInFrameEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import org.jetbrains.annotations.UnknownNullability;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ImageRenderer {
    private static final ContextKey<ImageData> IMAGE_KEY = new ContextKey<>(LTExtras.location("image"));

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        Class<ItemFrameRenderer<?>> itemFrameRenderer = (Class<ItemFrameRenderer<?>>) (Class<?>) ItemFrameRenderer.class;
        event.registerEntityModifier(itemFrameRenderer, (entity, state) -> {
            ItemStack stack = entity.getItem();
            if (stack.is(ExtraItems.IMAGE.get())) {
                state.setRenderData(IMAGE_KEY, stack.get(ExtraDataComponents.IMAGE));
            }
        });
    }

    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ImageData image = event.getItemFrameRenderState().getRenderData(IMAGE_KEY);
        if (image != null) {
            renderImage(image, event.getPoseStack().last(), event.getSubmitNodeCollector(), event.getItemFrameRenderState().lightCoords);
            event.setCanceled(true);
        }
    }

    private static void renderImage(ImageData image, PoseStack.Pose pose, SubmitNodeCollector bufferSource, int packedLight) {
        float x0 = -image.width() / 2.0f + image.offsetX();
        float y0 = -image.height() / 2.0f + image.offsetY();
        float x1 = image.width() / 2.0f + image.offsetX();
        float y1 = image.height() / 2.0f + image.offsetY();

        // Todo 26.1 Port
//        VertexConsumer consumer = bufferSource.getBuffer(RenderTypes.entityCutoutZOffset(image.texture()));
//        addVertex(consumer, pose, x0, y0, 1.0f, 1.0f, packedLight);
//        addVertex(consumer, pose, x1, y0, 0.0f, 1.0f, packedLight);
//        addVertex(consumer, pose, x1, y1, 0.0f, 0.0f, packedLight);
//        addVertex(consumer, pose, x0, y1, 1.0f, 0.0f, packedLight);
    }

    private static void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, int packedLight) {
        consumer.addVertex(pose.pose(), x, y, 0.0f)
                .setColor(CommonColors.WHITE)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0f, 0.0f, -1.0f);
    }
}
