package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.block.entity.SeatEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SeatRenderer extends EntityRenderer<SeatEntity, EntityRenderState> {
    public SeatRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(SeatEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return false;
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
