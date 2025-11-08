package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.client.block.DisplayBlockRender;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class ClientRegisterEvents {



    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ExtraBlocks.DISPLAY_BLOCK_ENTITY.get(), DisplayBlockRender::new);
    }


}
