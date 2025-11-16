package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.client.block.DisplayBlockRender;
import com.lovetropics.extras.client.block.WordBoxBlockEntityRenderer;
import com.lovetropics.extras.client.entity.renderer.layers.CustomSingleBootLayer;
import com.lovetropics.extras.client.entity.state.HoniedShulkerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientRegisterEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ExtraBlocks.DISPLAY_BLOCK_ENTITY.get(), DisplayBlockRender::new);
        event.registerBlockEntityRenderer(ExtraBlocks.WORD_BOX_BLOCK_ENTITY.get(), WordBoxBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(FallingBlockRenderer.class, WordBoxBlockEntityRenderer::updateFallingBlockRenderState);
        event.registerEntityModifier(ShulkerRenderer.class, HoniedShulkerRenderState::updateHoniedRenderState);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : event.getEntityTypes()) {
            EntityRenderer<?, ?> renderer = event.getRenderer(entityType);
            if (renderer instanceof HumanoidMobRenderer humanoidMobRenderer) {
                humanoidMobRenderer.addLayer(new CustomSingleBootLayer((RenderLayerParent) renderer));
            }
        }
        for(var skin : event.getSkins()) {
            var renderer = event.getSkin(skin);
            if(renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new CustomSingleBootLayer((RenderLayerParent) renderer));
            }
        }
    }
}
