package com.lovetropics.extras.client.item;

import com.lovetropics.extras.LTExtras;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ExtraSpecialRenderers {
    @SubscribeEvent
    public static void register(RegisterSpecialModelRendererEvent event) {
        event.register(LTExtras.id("word_box"), WordBoxSpecialRenderer.Unbaked.MAP_CODEC);
    }
}
