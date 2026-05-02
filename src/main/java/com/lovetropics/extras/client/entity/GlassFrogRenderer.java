package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.GlassFrogModel;
import com.lovetropics.extras.client.entity.state.GlassFrogRenderState;
import com.lovetropics.extras.entity.glass_frog.GlassFrog;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class GlassFrogRenderer extends MobRenderer<GlassFrog, GlassFrogRenderState, GlassFrogModel> {
    private static final Identifier TEXTURE = LTExtras.id("textures/entity/glass_frog.png");

    public GlassFrogRenderer(EntityRendererProvider.Context context) {
        super(context, new GlassFrogModel(context.bakeLayer(GlassFrogModel.LAYER)), 0.2F);
    }

    @Override
    public GlassFrogRenderState createRenderState() {
        return new GlassFrogRenderState();
    }

    @Override
    public Identifier getTextureLocation(GlassFrogRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public void extractRenderState(GlassFrog frog, GlassFrogRenderState state, float partialTick) {
        super.extractRenderState(frog, state, partialTick);
        state.isSwimming = frog.isInWater();
    }

    @Override
    protected @Nullable RenderType getRenderType(GlassFrogRenderState renderState, boolean isVisible, boolean renderTranslucent, boolean appearsGlowing) {
        return super.getRenderType(renderState, isVisible, true, appearsGlowing);
    }
}
