package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.zipline.ZiplineEntity;
import com.lovetropics.extras.zipline.ZiplinePath;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ZiplineRenderer extends EntityRenderer<ZiplineEntity, EntityRenderState> {
    public ZiplineRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    protected AABB getBoundingBoxForCulling(ZiplineEntity entity) {
        return new AABB(entity.path().start(), entity.path().end()).inflate(0.1);
    }

    @Override
    public void extractRenderState(ZiplineEntity entity, EntityRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        ZiplinePath path = entity.path();
        if (!path.isValid()) {
            return;
        }
        EntityRenderState.LeashState leash = new EntityRenderState.LeashState();
        leash.start = path.start();
        leash.end = path.end();
        leash.offset = path.start().subtract(entity.position());
        leash.slack = false;
        leash.startBlockLight = entity.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(leash.start));
        leash.endBlockLight = entity.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(leash.end));
        leash.startSkyLight = entity.level().getBrightness(LightLayer.SKY, BlockPos.containing(leash.start));
        leash.endSkyLight = entity.level().getBrightness(LightLayer.SKY, BlockPos.containing(leash.end));
        state.leashStates = List.of(leash);
    }
}
