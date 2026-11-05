package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.PhilippineEagleModel;
import com.lovetropics.extras.entity.PhilippineEagleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class PhilippineEagleRenderer extends MobRenderer<PhilippineEagleEntity, LivingEntityRenderState, PhilippineEagleModel> {
    private static final Identifier TEXTURE = LTExtras.id("textures/entity/philippine_eagle.png");

    public PhilippineEagleRenderer(EntityRendererProvider.Context context) {
        super(context, new PhilippineEagleModel(context.bakeLayer(PhilippineEagleModel.LAYER)), 0.2f);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public void extractRenderState(PhilippineEagleEntity entity, LivingEntityRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }
}
