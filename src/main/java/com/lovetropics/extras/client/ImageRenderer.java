package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.ImageData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderItemInFrameEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ImageRenderer {
    @SubscribeEvent
    public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(ExtraItems.IMAGE.get())) {
            return;
        }
        ImageData image = stack.get(ExtraDataComponents.IMAGE);
        if (image != null) {
            renderImage(image, event.getPoseStack().last(), event.getMultiBufferSource(), event.getPackedLight());
            event.setCanceled(true);
        }
    }

    private static void renderImage(ImageData image, PoseStack.Pose pose, MultiBufferSource bufferSource, int packedLight) {
        float x0 = -image.width() / 2.0f + image.offsetX();
        float y0 = -image.height() / 2.0f + image.offsetY();
        float x1 = image.width() / 2.0f + image.offsetX();
        float y1 = image.height() / 2.0f + image.offsetY();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCullZOffset(image.texture()));
        addVertex(consumer, pose, x0, y0, 1.0f, 1.0f, packedLight);
        addVertex(consumer, pose, x1, y0, 0.0f, 1.0f, packedLight);
        addVertex(consumer, pose, x1, y1, 0.0f, 0.0f, packedLight);
        addVertex(consumer, pose, x0, y1, 1.0f, 0.0f, packedLight);
    }

    private static void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, int packedLight) {
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        consumer.addVertex(matrix, x, y, 0.0f)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0f, 0.0f, -1.0f);
    }
}
