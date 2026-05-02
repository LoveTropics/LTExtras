package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.client.entity.state.CollectibleEntityRenderState;
import com.lovetropics.extras.entity.CollectibleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Quaternionf;

public class CollectibleEntityRenderer extends EntityRenderer<CollectibleEntity, CollectibleEntityRenderState> {
    private static final ItemDisplayContext DISPLAY_CONTEXT = ItemDisplayContext.GROUND;

    private final ItemModelResolver itemModeResolver;

    public CollectibleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        itemModeResolver = context.getItemModelResolver();
        shadowRadius = 0.3f;
        shadowStrength = 0.75f;
    }

    @Override
    public CollectibleEntityRenderState createRenderState() {
        return new CollectibleEntityRenderState();
    }

    @Override
    public void extractRenderState(CollectibleEntity entity, CollectibleEntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        ItemStack displayedItem = entity.getDisplayedItem();
        itemModeResolver.updateForNonLiving(state.displayedItemState, displayedItem, DISPLAY_CONTEXT, entity);
    }

    @Override
    public void submit(CollectibleEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.displayedItemState.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        // TODO: Re-evaluate scaling and positioning now that we have access to bounding box
        AABB boundingBox = state.displayedItemState.getModelBoundingBox();
        float offset = (1.0f / 16.0f) - (float) boundingBox.minY;
        float bob = (Mth.sin(state.ageInTicks / 10.0f) + 1.0f) * 0.05f;
        poseStack.translate(0.0F, bob + offset, 0.0F);
        poseStack.mulPose(Mth.rotationAroundAxis(Mth.Y_AXIS, camera.orientation, new Quaternionf()));
        poseStack.scale(2.0f, 2.0f, 2.0f);

        state.displayedItemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();

        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    protected boolean shouldShowName(CollectibleEntity entity, double distanceToCameraSq) {
        return entity.hasCustomName() || entity.shouldShowName() && isEntityPicked(entity);
    }

    private static boolean isEntityPicked(Entity entity) {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof EntityHitResult entityHitResult) {
            return entityHitResult.getEntity() == entity;
        }
        return false;
    }
}
