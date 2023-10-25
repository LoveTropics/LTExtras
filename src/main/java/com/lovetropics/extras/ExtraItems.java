package com.lovetropics.extras;

import com.lovetropics.extras.item.CollectibleBasketItem;
import com.lovetropics.extras.item.EntityWandItem;
import com.lovetropics.extras.item.ImageItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ExtraItems {
    private static final Registrate REGISTRATE = LTExtras.registrate();

    public static final ItemEntry<EntityWandItem> ENTITY_WAND = REGISTRATE.item("entity_wand", EntityWandItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<Item> TROPICOIN = REGISTRATE.item("tropicoin", Item::new).lang("TropiCoin").register();

    public static final ItemEntry<CollectibleBasketItem> COLLECTIBLE_BASKET = REGISTRATE.item("collectible_basket", CollectibleBasketItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<ImageItem> IMAGE = REGISTRATE.item("image", ImageItem::new)
            .tab(LTExtras.TAB_KEY, modifier -> {
                for (final ImageItem.Data preset : ImageItem.PRESETS) {
                    final ItemStack stack = new ItemStack(ExtraItems.IMAGE.get());
                    ImageItem.set(stack, preset);
                    modifier.accept(stack);
                }
            })
            .register();

    public static final ItemEntry<ImageItem> INVITE = REGISTRATE.item("invite", ImageItem::new).register();

    public static void init() {
    }
}
