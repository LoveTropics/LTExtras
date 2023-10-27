package com.lovetropics.extras;

import com.lovetropics.extras.item.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ExtraItems {
    private static final Registrate REGISTRATE = LTExtras.registrate();

    public static final ItemEntry<EntityWandItem> ENTITY_WAND = REGISTRATE.item("entity_wand", EntityWandItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<Item> TROPICOIN = REGISTRATE.item("tropicoin", Item::new).lang("TropiCoin").register();

    public static final ItemEntry<CollectibleBasketItem> COLLECTIBLE_BASKET = REGISTRATE.item("collectible_basket", CollectibleBasketItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<CollectibleCompassItem> COLLECTIBLE_COMPASS = REGISTRATE.item("collectible_compass", CollectibleCompassItem::new)
            .properties(p -> p.stacksTo(1))
            .model((ctx, prov) -> {})
            .onRegister(item -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ExtraItemProperties::registerCollectibleCompassAngle))
            .register();

    public static final ItemEntry<ImageItem> IMAGE = REGISTRATE.item("image", ImageItem::new)
            .tab(LTExtras.TAB_KEY, modifier -> {
                for (final ImageData preset : ImageItem.PRESETS) {
                    final ItemStack stack = new ItemStack(ExtraItems.IMAGE.get());
                    ImageData.set(stack, preset);
                    modifier.accept(stack);
                }
            })
            .register();

    public static final ItemEntry<InviteItem> INVITE = REGISTRATE.item("invite", InviteItem::new)
            .tab(LTExtras.TAB_KEY, modifier -> {
                for (final ImageData preset : InviteItem.PRESETS) {
                    final ItemStack stack = new ItemStack(ExtraItems.INVITE.get());
                    ImageData.set(stack, preset);
                    modifier.accept(stack);
                }
            })
            .register();

    public static final ItemEntry<TropicMapItem> TROPICAL_MAP = REGISTRATE.item("tropical_map", TropicMapItem::new)
            .initialProperties(() -> new Item.Properties().stacksTo(1))
            .defaultModel()
            .register();

    public static void init() {
    }
}
