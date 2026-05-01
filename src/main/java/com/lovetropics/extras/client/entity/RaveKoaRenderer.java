package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.RaveKoaModel;
import com.lovetropics.extras.client.entity.state.RaveKoaRenderState;
import com.lovetropics.extras.entity.ExtraEntities;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RaveKoaRenderer extends MobRenderer<RaveKoaEntity, RaveKoaRenderState, RaveKoaModel> {
    private static final Identifier TEXTURE_MAN_HUNTER = LTExtras.location("textures/entity/koa_man_hunter.png");
    private static final Identifier TEXTURE_MAN_FISHER = LTExtras.location("textures/entity/koa_man_fisher.png");
    private static final Identifier TEXTURE_WOMAN_FISHER = LTExtras.location("textures/entity/koa_woman_fisher.png");

    public RaveKoaRenderer(EntityRendererProvider.Context context) {
        super(context, new RaveKoaModel(context.bakeLayer(RaveKoaModel.LAYER_LOCATION)), 0.9f);
    }

    @Override
    public RaveKoaRenderState createRenderState() {
        return new RaveKoaRenderState();
    }

    @Override
    public void extractRenderState(RaveKoaEntity entity, RaveKoaRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.raveAnimationStateDJ.copyFrom(entity.raveAnimationStateDJ);
        state.raveAnimationStateDance1.copyFrom(entity.raveAnimationStateDance1);
        state.raveAnimationStateDance2.copyFrom(entity.raveAnimationStateDance2);
    }

    @Override
    public Identifier getTextureLocation(RaveKoaRenderState state) {
        if (state.entityType == ExtraEntities.RAVEKOADJ.get()) {
            return TEXTURE_MAN_HUNTER;
        } else if (state.entityType == ExtraEntities.RAVEKOADANCE1.get()) {
            return TEXTURE_WOMAN_FISHER;
        } else if (state.entityType == ExtraEntities.RAVEKOADANCE2.get()) {
            return TEXTURE_MAN_FISHER;
        }
        return TEXTURE_MAN_HUNTER;
    }
}
