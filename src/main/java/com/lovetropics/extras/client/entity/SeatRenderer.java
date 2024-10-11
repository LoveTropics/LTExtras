package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.block.entity.SeatEntity;
import com.lovetropics.extras.client.entity.model.RaveKoaModel;
import com.lovetropics.extras.client.entity.model.SeatModel;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntity;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDJ;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDance1;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDance2;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SeatRenderer extends EntityRenderer<SeatEntity> {
    private static final ResourceLocation TEXTURE_MAN_HUNTER = LTExtras.location("textures/entity/koa_man_hunter.png");

   public SeatRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
   }

    @Override
    public ResourceLocation getTextureLocation(final SeatEntity seatEntity) {
        return TEXTURE_MAN_HUNTER;
    }

    @Override
    public boolean shouldRender(final SeatEntity livingEntity, final Frustum camera, final double camX, final double camY, final double camZ) {
        return false;
    }

}
