package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.zipline.ZiplineRideEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class ZiplineRideRenderer extends EntityRenderer<ZiplineRideEntity, EntityRenderState> {
    public ZiplineRideRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public boolean shouldRender(ZiplineRideEntity entity, Frustum camera, double x, double y, double z) {
        return false;
    }
}
