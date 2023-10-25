package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.ImageData;
import com.lovetropics.extras.item.ImageItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ImageRenderer {
    @SubscribeEvent
    public static void onRenderItemInFrame(final RenderItemInFrameEvent event) {
        final ItemStack stack = event.getItemStack();
        if (!stack.is(ExtraItems.IMAGE.get())) {
            return;
        }
        final ImageData image = ImageData.get(stack).orElse(null);
        if (image != null) {
            renderImage(image, event.getPoseStack().last(), event.getMultiBufferSource(), event.getPackedLight());
            event.setCanceled(true);
        }
    }

    private static void renderImage(final ImageData image, final PoseStack.Pose pose, final MultiBufferSource bufferSource, final int packedLight) {
        final float x0 = -image.width() / 2.0f;
        final float y0 = -image.height() / 2.0f;
        final float x1 = image.width() / 2.0f;
        final float y1 = image.height() / 2.0f;

        final VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCullZOffset(image.texture()));
        addVertex(consumer, pose, x0, y0, 0.0f, 1.0f, packedLight);
        addVertex(consumer, pose, x0, y1, 0.0f, 0.0f, packedLight);
        addVertex(consumer, pose, x1, y1, 1.0f, 0.0f, packedLight);
        addVertex(consumer, pose, x1, y0, 1.0f, 1.0f, packedLight);
    }

    private static void addVertex(final VertexConsumer consumer, final PoseStack.Pose pose, final float x, final float y, final float u, final float v, final int packedLight) {
        final Matrix4f matrix = pose.pose();
        final Matrix3f normal = pose.normal();
        consumer.vertex(matrix, x, y, 0.0f)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0f, 0.0f, -1.0f)
                .endVertex();
    }
}
