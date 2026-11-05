package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.PhilippineWartyPigModel;
import com.lovetropics.extras.entity.PhilippineWartyPigEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class PhilippineWartyPigRenderer extends MobRenderer<PhilippineWartyPigEntity, LivingEntityRenderState, PhilippineWartyPigModel> {
    private static final Identifier TEXTURE = LTExtras.id("textures/entity/philippine_warty_pig.png");

    public PhilippineWartyPigRenderer(EntityRendererProvider.Context context) {
        super(context, new PhilippineWartyPigModel(context.bakeLayer(PhilippineWartyPigModel.LAYER)), 0.2f);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public void extractRenderState(PhilippineWartyPigEntity entity, LivingEntityRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }
}
