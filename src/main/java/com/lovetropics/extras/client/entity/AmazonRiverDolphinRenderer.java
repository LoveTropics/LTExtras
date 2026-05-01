package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.AmazonRiverDolphinModel;
import com.lovetropics.extras.client.entity.state.AmazonRiverDolphinRenderState;
import com.lovetropics.extras.entity.AmazonRiverDolphin;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.resources.Identifier;

public class AmazonRiverDolphinRenderer extends AgeableMobRenderer<AmazonRiverDolphin, AmazonRiverDolphinRenderState, AmazonRiverDolphinModel> {
    private static final Identifier TEXTURE = LTExtras.location("textures/entity/amazon_river_dolphin.png");

    public AmazonRiverDolphinRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new AmazonRiverDolphinModel(context.bakeLayer(AmazonRiverDolphinModel.LAYER)),
                new AmazonRiverDolphinModel(context.bakeLayer(AmazonRiverDolphinModel.BABY_LAYER)),
                0.7F
        );
    }

    @Override
    public AmazonRiverDolphinRenderState createRenderState() {
        return new AmazonRiverDolphinRenderState();
    }

    @Override
    public Identifier getTextureLocation(AmazonRiverDolphinRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public void extractRenderState(AmazonRiverDolphin dolphin, AmazonRiverDolphinRenderState state, float partialTick) {
        super.extractRenderState(dolphin, state, partialTick);
        HoldingEntityRenderState.extractHoldingEntityRenderState(dolphin, state, this.itemModelResolver);
        state.isMoving = dolphin.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7;
        state.isMouthOpen = dolphin.getMouthOpen();
    }
}
