package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.ClientCollectiblesList;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ExtraItemProperties {
    public static final ResourceLocation UNSEEN = ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "unseen");

    public static void register() {
        ItemProperties.register(ExtraItems.COLLECTIBLE_COMPASS.get(), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "angle"), new CompassItemPropertyFunction((level, stack, entity) -> {
            final CollectibleCompassItem.Target target = stack.get(ExtraDataComponents.COLLECTIBLE_TARGET);
            return target != null ? target.pos() : null;
        }));

        ItemProperties.register(ExtraItems.COLLECTIBLE_BASKET.get(), UNSEEN, (ClampedItemPropertyFunction) (stack, level, entity, seed) -> {
            final ClientCollectiblesList collectibles = ClientCollectiblesList.getOrNull();
            if (collectibles != null && collectibles.hasUnseen()) {
                return 1.0f;
            }
            return 0.0f;
        });
    }
}
