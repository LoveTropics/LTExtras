package com.lovetropics.extras;

import com.lovetropics.extras.item.CollectibleBasketItem;
import com.lovetropics.extras.item.CollectibleCompassItem;
import com.lovetropics.extras.item.EntityWandItem;
import com.lovetropics.extras.item.ExtraItemProperties;
import com.lovetropics.extras.item.ImageData;
import com.lovetropics.extras.item.ImageItem;
import com.lovetropics.extras.item.InviteItem;
import com.lovetropics.extras.item.TropicMapItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class ExtraItems {
    private static final Registrate REGISTRATE = LTExtras.registrate();

    public static final ItemEntry<EntityWandItem> ENTITY_WAND = REGISTRATE.item("entity_wand", EntityWandItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<Item> TROPICOIN = REGISTRATE.item("tropicoin", Item::new).lang("TropiCoin").register();

    public static final ItemEntry<CollectibleBasketItem> COLLECTIBLE_BASKET = REGISTRATE.item("collectible_basket", CollectibleBasketItem::new)
            .properties(p -> p.stacksTo(1))
            .model((ctx, prov) -> prov.generated(ctx)
                    .override()
                    .predicate(ExtraItemProperties.UNSEEN, 1.0f)
                    .model(prov.getBuilder(ctx.getName() + "_unseen")
                            .parent(new ModelFile.UncheckedModelFile("item/generated"))
                            .texture("layer0", prov.itemTexture(ctx).withPath(path -> path + "_unseen"))
                    ).end()
            )
            .register();

    public static final ItemEntry<CollectibleCompassItem> COLLECTIBLE_COMPASS = REGISTRATE.item("collectible_compass", CollectibleCompassItem::new)
            .properties(p -> p.stacksTo(1))
            .model((ctx, prov) -> {})
            .register();

    public static final ItemEntry<ImageItem> IMAGE = REGISTRATE.item("image", ImageItem::new)
            .tab(LTExtras.TAB_KEY, modifier -> {
                for (ImageData preset : ImageItem.PRESETS) {
                    ItemStack stack = new ItemStack(ExtraItems.IMAGE.get());
                    stack.set(ExtraDataComponents.IMAGE, preset);
                    modifier.accept(stack);
                }
            })
            .register();

    public static final ItemEntry<InviteItem> INVITE = REGISTRATE.item("invite", InviteItem::new)
            .tab(LTExtras.TAB_KEY, modifier -> {
                for (ImageData preset : InviteItem.PRESETS) {
                    ItemStack stack = new ItemStack(ExtraItems.INVITE.get());
                    stack.set(ExtraDataComponents.IMAGE, preset);
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
