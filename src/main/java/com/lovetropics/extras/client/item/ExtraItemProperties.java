package com.lovetropics.extras.client.item;

import com.lovetropics.extras.LTExtras;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ExtraItemProperties {
    @SubscribeEvent
    public static void registerRangeSelectProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(LTExtras.location("collectible_compass_angle"), CollectibleCompassAngle.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
        event.register(LTExtras.location("has_unseen_collectible"), HasUnseenCollectible.MAP_CODEC);
    }
}
