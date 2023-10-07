package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.entity.CollectibleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CollectibleEntityRenderer extends EntityRenderer<CollectibleEntity> {
    private static final ItemDisplayContext DISPLAY_CONTEXT = ItemDisplayContext.GROUND;
    private static final Vector3f Y_AXIS = new Vector3f(0.0f, 1.0f, 0.0f);

    private final ItemRenderer itemRenderer;

    public CollectibleEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        itemRenderer = context.getItemRenderer();
        shadowRadius = 0.3f;
        shadowStrength = 0.75f;
    }

    @Override
    public void render(final CollectibleEntity entity, final float yaw, final float partialTicks, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        final ItemStack displayedItem = entity.getDisplayedItem();
        if (displayedItem.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        final BakedModel model = itemRenderer.getModel(displayedItem, entity.level(), null, entity.getId());
        final float age = entity.tickCount + partialTicks;

        final float groundScale = model.getTransforms().getTransform(DISPLAY_CONTEXT).scale.y();
        final float bob = (Mth.sin(age / 10.0f) + 1.0f) * 0.05f;
        poseStack.translate(0.0f, bob + 0.4f * groundScale, 0.0f);
        poseStack.mulPose(rotationAbout(entityRenderDispatcher.cameraOrientation(), Y_AXIS, new Quaternionf()));
        poseStack.mulPose(Axis.YP.rotation(Mth.PI));

        final float scale = model.isGui3d() ? 2.25f : 2.0f;
        poseStack.scale(scale, scale, scale);

        itemRenderer.render(displayedItem, DISPLAY_CONTEXT, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, model);

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static Quaternionf rotationAbout(final Quaternionf rotation, final Vector3f axis, final Quaternionf result) {
        final float projectScale = axis.dot(rotation.x(), rotation.y(), rotation.z());
        return result.set(axis.x() * projectScale, axis.y() * projectScale, axis.z() * projectScale, rotation.w());
    }

    @Override
    public ResourceLocation getTextureLocation(final CollectibleEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    protected boolean shouldShowName(final CollectibleEntity entity) {
        return entity.hasCustomName() || entity.shouldShowName() && isEntityPicked(entity);
    }

    private static boolean isEntityPicked(final Entity entity) {
        final HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof final EntityHitResult entityHitResult) {
            return entityHitResult.getEntity() == entity;
        }
        return false;
    }
}
