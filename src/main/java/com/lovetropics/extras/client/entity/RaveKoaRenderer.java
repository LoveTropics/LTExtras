package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.RaveKoaModel;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntity;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDJ;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDance1;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDance2;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaveKoaRenderer extends MobRenderer<RaveKoaEntity, RaveKoaModel<RaveKoaEntity>> {
   private static final ResourceLocation TEXTURE_MAN_HUNTER = new ResourceLocation(LTExtras.MODID, "textures/entity/koa_man_hunter.png");
   private static final ResourceLocation TEXTURE_MAN_FISHER = new ResourceLocation(LTExtras.MODID, "textures/entity/koa_man_fisher.png");
   private static final ResourceLocation TEXTURE_WOMAN_FISHER = new ResourceLocation(LTExtras.MODID, "textures/entity/koa_woman_fisher.png");

   public RaveKoaRenderer(EntityRendererProvider.Context p_234787_) {
      super(p_234787_, new RaveKoaModel<>(p_234787_.bakeLayer(RaveKoaModel.LAYER_LOCATION)), 0.9F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getTextureLocation(RaveKoaEntity entity) {
      if (entity instanceof RaveKoaEntityDJ) {
         return TEXTURE_MAN_HUNTER;
      } else if (entity instanceof RaveKoaEntityDance1) {
         return TEXTURE_WOMAN_FISHER;
      } else if (entity instanceof RaveKoaEntityDance2) {
         return TEXTURE_MAN_FISHER;
      } else {
         return TEXTURE_MAN_HUNTER;
      }
   }
}