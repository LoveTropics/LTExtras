package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.client.block.DisplayBlockRender;
import com.lovetropics.extras.client.block.WordBoxBlockEntityRenderer;
import com.lovetropics.extras.client.entity.state.HoniedShulkerRenderState;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
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

}
