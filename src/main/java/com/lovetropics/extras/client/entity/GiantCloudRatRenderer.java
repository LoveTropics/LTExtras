package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.GiantCloudRatModel;
import com.lovetropics.extras.entity.GiantCloudRatEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class GiantCloudRatRenderer extends MobRenderer<GiantCloudRatEntity, LivingEntityRenderState, GiantCloudRatModel> {
    private static final Identifier TEXTURE = LTExtras.id("textures/entity/giant_cloud_rat.png");

    public GiantCloudRatRenderer(EntityRendererProvider.Context context) {
        super(context, new GiantCloudRatModel(context.bakeLayer(GiantCloudRatModel.LAYER)), 0.2f);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public void extractRenderState(GiantCloudRatEntity entity, LivingEntityRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }
}
