package com.lovetropics.extras;

import com.lovetropics.extras.item.CollectibleBasketItem;
import com.lovetropics.extras.item.EntityWandItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

public class ExtraItems {
    private static final Registrate REGISTRATE = LTExtras.registrate();

    public static final ItemEntry<EntityWandItem> ENTITY_WAND = REGISTRATE.item("entity_wand", EntityWandItem::new)
            .initialProperties(() -> new Item.Properties().stacksTo(1))
            .defaultModel()
            .register();

    public static final ItemEntry<EntityWandItem> COSMETIC_POINT = REGISTRATE.item("cosmetic_point", EntityWandItem::new)
            .initialProperties(() -> new Item.Properties().stacksTo(1))
            .defaultModel()
            .register();

    public static final ItemEntry<CollectibleBasketItem> COLLECTIBLE_BASKET = REGISTRATE.item("collectible_basket", CollectibleBasketItem::new)
            .initialProperties(() -> new Item.Properties().stacksTo(1))
            .defaultModel()
            .register();

    public static void init() {
    }
}
