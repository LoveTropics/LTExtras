package com.lovetropics.extras;

import com.lovetropics.extras.client.item.CollectibleCompassAngle;
import com.lovetropics.extras.client.item.HasUnseenCollectible;
import com.lovetropics.extras.data.poi.MapConfig;
import com.lovetropics.extras.item.CollectibleBasketItem;
import com.lovetropics.extras.item.CollectibleCompassItem;
import com.lovetropics.extras.item.EntityWandItem;
import com.lovetropics.extras.item.ForkliftSpawnEggItem;
import com.lovetropics.extras.item.HighHeelsItem;
import com.lovetropics.extras.item.ImageData;
import com.lovetropics.extras.item.ImageItem;
import com.lovetropics.extras.item.InviteItem;
import com.lovetropics.extras.item.TropicMapItem;
import com.lovetropics.extras.item.WalkAnimation;
import com.lovetropics.extras.item.WalkSound;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.extras.sounds.ExtraSounds;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.minecraft.client.data.models.model.ItemModelUtils.*;

public class ExtraItems {
    private static final Registrate REGISTRATE = LTExtras.registrate();

    private static final ResourceKey<EquipmentAsset> PLACEHOLDER_EQUIPMENT_ASSET = EquipmentAssets.LEATHER;

    public static final ItemEntry<EntityWandItem> ENTITY_WAND = REGISTRATE.item("entity_wand", EntityWandItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<Item> TROPICOIN = REGISTRATE.item("tropicoin", Item::new).lang("TropiCoin").register();

    public static final ItemEntry<Item> ORANGE_GLASSES = sunglasses("orange_glasses").register();
    public static final ItemEntry<Item> BLUE_GLASSES = sunglasses("blue_glasses").register();

    private static ItemBuilder<Item, Registrate> sunglasses(String name) {
        return REGISTRATE.item(name, Item::new)
                .properties(p -> p.stacksTo(1).equippable(EquipmentSlot.HEAD))
                .model(() -> Models::generateGlasses);
    }

    public static final ItemEntry<CollectibleBasketItem> COLLECTIBLE_BASKET = REGISTRATE.item("collectible_basket", CollectibleBasketItem::new)
            .properties(p -> p.stacksTo(1))
            .model(() -> Models::generateCollectibleBasket)
            .register();

    public static final ItemEntry<CollectibleCompassItem> COLLECTIBLE_COMPASS = REGISTRATE.item("collectible_compass", CollectibleCompassItem::new)
            .properties(p -> p.stacksTo(1))
            .model(() -> Models::generateCollectibleCompass)
            .register();

    public static final ItemEntry<ImageItem> IMAGE = REGISTRATE.item("image", ImageItem::new)
            .tab(LTExtras.TAB_KEY, (ctx, modifier) -> {
                for (ImageData preset : ImageItem.PRESETS) {
                    ItemStack stack = new ItemStack(ctx.get());
                    stack.set(ExtraDataComponents.IMAGE, preset);
                    modifier.accept(stack);
                }
            })
            .register();

    public static final ItemEntry<InviteItem> INVITE = REGISTRATE.item("invite", InviteItem::new)
            .tab(LTExtras.TAB_KEY, (ctx, modifier) -> {
                for (ImageData preset : InviteItem.PRESETS) {
                    ItemStack stack = new ItemStack(ctx.get());
                    stack.set(ExtraDataComponents.IMAGE, preset);
                    modifier.accept(stack);
                }
            })
            .register();

    public static final ItemEntry<Item> QUEST = REGISTRATE.item("quest", Item::new).register();

    public static final ItemEntry<TropicMapItem> TROPICAL_MAP = REGISTRATE.item("tropical_map", TropicMapItem::new)
            .initialProperties(() -> new Item.Properties().stacksTo(1))
            .tab(LTExtras.TAB_KEY, (ctx, modifier) -> {
                HolderLookup<MapConfig> maps = modifier.getParameters().holders().lookupOrThrow(ExtraRegistries.MAP);
                maps.listElements().forEach(map -> {
                    ItemStack stack = new ItemStack(ctx.get());
                    stack.set(ExtraDataComponents.MAP, map);
                    modifier.accept(stack);
                });
            })
            .defaultModel()
            .register();

    public static final ItemEntry<HighHeelsItem> HIGH_HEELS = REGISTRATE.item("high_heels", HighHeelsItem::new)
            .properties(p -> p.stacksTo(1)
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.FEET)
                            .setAsset(PLACEHOLDER_EQUIPMENT_ASSET)
                            .build())
                    .component(ExtraDataComponents.WALK_ANIMATION, WalkAnimation.FABULOUS)
                    .component(ExtraDataComponents.WALK_SOUND, WalkSound.builder().soundEvent(ExtraSounds.HEELS_STEP).cooldown(0.9f).volume(.5f).build())
                    .component(ExtraDataComponents.ADJUST_HEIGHT, 0.2F)
            )
            .clientExtension(() -> HighHeelsItem.ClientExtensions::new)
            .defaultModel()
            .register();

    public static final ItemEntry<Item> FORKLIFT_CERTIFICATION = REGISTRATE.item("forklift_certification", Item::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<ForkliftSpawnEggItem> FORKLIFT_SPAWN_EGG = REGISTRATE.item("forklift_spawn_egg", ForkliftSpawnEggItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static void init() {
    }

    @EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
    private static class Models {
        public static final ModelTemplate GLASSES_EQUIPPED_TEMPLATE = ModelTemplates.createItem(LTExtras.location("template_glasses_equipped").toString(), "_equipped", TextureSlot.LAYER0);

        private static void generateForkliftCertification(DataGenContext<Item, CollectibleBasketItem> ctx, RegistrateItemModelGenerator prov) {
            prov.itemModelOutput.accept(ctx.get(), conditional(new HasUnseenCollectible(),
                    plainModel(prov.createFlatItemModel(ctx.get(), "_unseen", ModelTemplates.FLAT_ITEM)),
                    plainModel(prov.createFlatItemModel(ctx.get(), ModelTemplates.FLAT_ITEM))
            ));
        }

        private static void generateCollectibleBasket(DataGenContext<Item, CollectibleBasketItem> ctx, RegistrateItemModelGenerator prov) {
            prov.itemModelOutput.accept(ctx.get(), conditional(new HasUnseenCollectible(),
                    plainModel(prov.createFlatItemModel(ctx.get(), "_unseen", ModelTemplates.FLAT_ITEM)),
                    plainModel(prov.createFlatItemModel(ctx.get(), ModelTemplates.FLAT_ITEM))
            ));
        }

        private static void generateGlasses(DataGenContext<Item, Item> ctx, RegistrateItemModelGenerator prov) {
            generateHeadEquippable(ctx, prov,
                    ModelTemplates.FLAT_ITEM.create(ctx.get(), TextureMapping.layer0(ctx.get()), prov.modelOutput),
                    GLASSES_EQUIPPED_TEMPLATE.create(ctx.get(), TextureMapping.layer0(TextureMapping.getItemTexture(ctx.get(), "_equipped")), prov.modelOutput)
            );
        }

        private static void generateHeadEquippable(DataGenContext<Item, Item> ctx, RegistrateItemModelGenerator prov, ResourceLocation model, ResourceLocation equippedModel) {
            prov.itemModelOutput.accept(ctx.get(), select(
                    new DisplayContext(),
                    plainModel(model),
                    when(List.of(ItemDisplayContext.HEAD), plainModel(equippedModel))
            ));
        }

        private static void generateCustomCompass(DataGenContext<Item, CollectibleCompassItem> ctx, RegistrateItemModelGenerator prov, RangeSelectItemModelProperty angle) {
            ItemModel.Unbaked baseModel = plainModel(ModelLocationUtils.getModelLocation(Items.COMPASS, "_16"));

            List<RangeSelectItemModel.Entry> modelByAngle = new ArrayList<>();
            modelByAngle.add(ItemModelUtils.override(baseModel, 0.0f));

            for (int i = 1; i < 32; i++) {
                int threshold = Mth.positiveModulo(i - 16, 32);
                modelByAngle.add(ItemModelUtils.override(
                        plainModel(ModelLocationUtils.getModelLocation(Items.COMPASS, String.format(Locale.ROOT, "_%02d", threshold))),
                        i - 0.5f
                ));
            }

            modelByAngle.add(ItemModelUtils.override(baseModel, 31.5f));

            prov.itemModelOutput.accept(
                    ctx.get(),
                    rangeSelect(angle, 32.0f, modelByAngle)
            );
        }

        private static void generateCollectibleCompass(DataGenContext<Item, CollectibleCompassItem> ctx, RegistrateItemModelGenerator prov) {
            generateCustomCompass(ctx, prov, new CollectibleCompassAngle());
        }
    }
}
