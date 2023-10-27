package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraItems;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ExtraItemProperties {
    public static void registerCollectibleCompassAngle() {
        ItemProperties.register(ExtraItems.COLLECTIBLE_COMPASS.get(), new ResourceLocation("angle"), new CompassItemPropertyFunction(
                (level, stack, entity) -> CollectibleCompassItem.getTarget(stack))
        );
    }
}
