package com.lovetropics.extras;

import com.lovetropics.extras.client.item.CollectibleCompassAngle;
import com.lovetropics.extras.client.item.HasUnseenCollectible;
import com.lovetropics.extras.data.poi.MapConfig;
import com.lovetropics.extras.entity.ExtraEntities;
import com.lovetropics.extras.item.CleaningItemFrameItem;
import com.lovetropics.extras.item.CollectibleBasketItem;
import com.lovetropics.extras.item.CollectibleCompassItem;
import com.lovetropics.extras.item.CustomBootsItem;
import com.lovetropics.extras.item.EntityWandItem;
import com.lovetropics.extras.item.HighHeelsItem;
import com.lovetropics.extras.item.ImageData;
import com.lovetropics.extras.item.ImageItem;
import com.lovetropics.extras.item.InviteItem;
import com.lovetropics.extras.item.TradingCards;
import com.lovetropics.extras.item.TropicMapItem;
import com.lovetropics.extras.item.WalkSound;
import com.lovetropics.extras.item.loot.ConfigurableCrossbowItem;
import com.lovetropics.extras.model_modifer.ExtraModelModifiers;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.extras.sounds.ExtraSounds;
import com.lovetropics.extras.zipline.ZiplineWrenchItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.SharedConstants;
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
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static net.minecraft.client.data.models.model.ItemModelUtils.*;

public class ExtraItems {
    private static final Registrate REGISTRATE = LTExtras.registrate();

    public static final FoodProperties PC_ENERGY_FOOD = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.2F).alwaysEdible().build();
    public static final FoodProperties TROPI_COOKIES_FOOD = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.2F).alwaysEdible().build();
    public static final FoodProperties GEGY_CHIPS_FOOD = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F).alwaysEdible().build();
    public static final Consumable PC_ENERGY_CONSUME = Consumables.defaultDrink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 10 * SharedConstants.TICKS_PER_SECOND, 0)))
            .build();

    private static final ResourceKey<EquipmentAsset> PLACEHOLDER_EQUIPMENT_ASSET = EquipmentAssets.LEATHER;

    public static final ItemEntry<EntityWandItem> ENTITY_WAND = REGISTRATE.item("entity_wand", EntityWandItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<Item> TROPICOIN = REGISTRATE.item("tropicoin", Item::new).lang("TropiCoin").register();

    public static final ItemEntry<Item> BLUE_KEY = REGISTRATE.item("blue_key", Item::new).register();
    public static final ItemEntry<Item> RED_KEY = REGISTRATE.item("red_key", Item::new).register();

    public static final ItemEntry<ZiplineWrenchItem> ZIPLINE_WRENCH = REGISTRATE.item("zipline_wrench", ZiplineWrenchItem::new)
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<Item> WHITE_KEY = REGISTRATE.item("white_key", Item::new).register();
    public static final ItemEntry<Item> YELLOW_KEY = REGISTRATE.item("yellow_key", Item::new).register();
    public static final ItemEntry<Item> DENTED_CAN = REGISTRATE.item("dented_can", Item::new).register();
    public static final ItemEntry<Item> FILLED_LUBRICANT_JAR = REGISTRATE.item("filled_lubricant_jar", Item::new).register();
    public static final ItemEntry<Item> LUBRICANT_JAR = REGISTRATE.item("lubricant_jar", Item::new).register();
    public static final ItemEntry<Item> PAPER_BAG = REGISTRATE.item("paper_bag", Item::new).register();
    public static final ItemEntry<Item> PC_ENERGY = REGISTRATE.item("pc_energy", Item::new)
            .properties(p -> p.food(PC_ENERGY_FOOD).component(DataComponents.CONSUMABLE, PC_ENERGY_CONSUME))
            .lang("PCEnergy")
            .register();
    public static final ItemEntry<Item> TROPI_COOKIES = REGISTRATE.item("tropi_cookies", Item::new)
            .properties(p -> p.food(TROPI_COOKIES_FOOD).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_FOOD))
            .register();
    public static final ItemEntry<Item> GEGY_POTATO_CHIPS = REGISTRATE.item("gegy_potato_chips", Item::new)
            .properties(p -> p.food(GEGY_CHIPS_FOOD).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_FOOD))
            .lang("Gegy's Potato Chips")
            .register();
    public static final ItemEntry<Item> OIL_CAN = REGISTRATE.item("oil_can", Item::new).register();

    public static final ItemEntry<Item> ORANGE_GLASSES = sunglasses("orange_glasses").register();
    public static final ItemEntry<Item> BLUE_GLASSES = sunglasses("blue_glasses").register();
    public static final ItemEntry<Item> GREEN_GLASSES = sunglasses("green_glasses").register();
    public static final ItemEntry<Item> WALLHACK_GLASSES = sunglasses("wallhack_glasses").register();

    public static final ItemEntry<Item> HUGE_GLASSES = REGISTRATE.item("huge_glasses", Item::new)
            .properties(p -> p.stacksTo(1).equippable(EquipmentSlot.HEAD))
            .model(() -> (ctx, prov) -> prov.createWithExistingModel(ctx.get(), LTExtras.id("item/huge_glasses")))
            .register();

    public static final ItemEntry<Item> WRENCH = REGISTRATE.item("wrench", Item::new).lang("Wrench").register();
    public static final ItemEntry<Item> STEERING_WHEEL = REGISTRATE.item("steering_wheel", Item::new).lang("Steering Wheel").register();
    public static final ItemEntry<Item> PUMP = REGISTRATE.item("pump", Item::new).lang("Pump").register();
    public static final ItemEntry<Item> PLASTIC_SHEETS = REGISTRATE.item("plastic_sheets", Item::new).lang("Plastic Sheets").register();
    public static final ItemEntry<Item> HOSE = REGISTRATE.item("hose", Item::new).lang("Hose").register();
    public static final ItemEntry<Item> GEARBOX = REGISTRATE.item("gearbox", Item::new).lang("Gearbox").register();
    public static final ItemEntry<Item> FORKLIFT_FORKS = REGISTRATE.item("forklift_forks", Item::new).lang("Forklift Forks").register();
    public static final ItemEntry<Item> FOOT_PEDAL = REGISTRATE.item("foot_pedal", Item::new).lang("Foot Pedal").register();
    public static final ItemEntry<Item> BOLT = REGISTRATE.item("bolt", Item::new).lang("Bolt").register();
    public static final ItemEntry<Item> BATTERY = REGISTRATE.item("battery", Item::new).lang("Battery").register();

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

    public static final ItemEntry<Item> TRADING_CARD = REGISTRATE.item("trading_card", Item::new)
            .tab(LTExtras.TAB_KEY, (ctx, modifier) -> {
                for (TradingCards.Preset preset : TradingCards.PRESETS) {
                    modifier.accept(preset.create(ctx.get()));
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

    public static final ItemEntry<Item> HARD_HAT = REGISTRATE.item("hard_hat", Item::new)
            .properties(p -> p.stacksTo(1)
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                            .setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
                            .build())
            )
            .model(() -> (ctx, prov) -> prov.createWithExistingModel(ctx.get(), LTExtras.id("item/hard_hat")))
            .register();

    public static final ItemEntry<Item> TOP_HAT = createTopHat("top_hat").register();
    public static final ItemEntry<Item> BRONZE_TOP_HAT = createTopHat("bronze_top_hat").register();
    public static final ItemEntry<Item> SILVER_TOP_HAT = createTopHat("silver_top_hat").register();
    public static final ItemEntry<Item> GOLD_TOP_HAT = createTopHat("gold_top_hat").register();

    private static ItemBuilder<Item, Registrate> createTopHat(String id) {
        return REGISTRATE.item(id, Item::new)
                .properties(properties -> properties
                        .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                                .setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
                                .setDamageOnHurt(false)
                                .build())
                        .stacksTo(1)
                )
                .model(() -> Models::generateTopHat);
    }

    public static final ItemEntry<Item> STACK_OF_HATS = REGISTRATE.item("stack_of_hats", Item::new)
            .properties(p -> p.stacksTo(1)
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                            .setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
                            .build())
            )
            .model(() -> (ctx, prov) -> Models.generateTopHat(ctx, prov, TextureMapping.getItemTexture(TOP_HAT.get())))
            .register();

    public static final ItemEntry<Item> REFLECTIVE_VEST = REGISTRATE.item("reflective_vest", Item::new)
            .properties(p -> p.stacksTo(1)
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.CHEST)
                            .setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
                            .setAsset(ResourceKey.create(EquipmentAssets.ROOT_ID, LTExtras.id("reflective_vest")))
                            .build())
            )
            .defaultModel()
            .register();

    public static final ItemEntry<HighHeelsItem> HIGH_HEELS = REGISTRATE.item("high_heels", HighHeelsItem::new)
            .properties(p -> p.stacksTo(1)
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.FEET)
                            .setAsset(PLACEHOLDER_EQUIPMENT_ASSET)
                            .build())
                    .delayedComponent(ExtraDataComponents.MODEL_MODIFIER.get(), modifierList(ExtraModelModifiers.FABULOUS))
                    .component(ExtraDataComponents.WALK_SOUND, WalkSound.builder().soundEvent(ExtraSounds.HEELS_STEP).cooldown(0.9f).volume(.5f).build())
                    .component(ExtraDataComponents.ADJUST_HEIGHT, 0.2F)
            )
            .clientExtension(() -> HighHeelsItem.ClientExtensions::new)
            .defaultModel()
            .register();

    public static final ItemEntry<CustomBootsItem> BIG_BOOT = REGISTRATE.item("big_boot", CustomBootsItem::new)
            .properties(p -> p.stacksTo(1)
                    .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.FEET)
                            .setAsset(PLACEHOLDER_EQUIPMENT_ASSET)
                            .build())
                    .delayedComponent(ExtraDataComponents.MODEL_MODIFIER.get(), modifierList(ExtraModelModifiers.STIFF_LEGS, ExtraModelModifiers.HOP_WALK))
                    .component(ExtraDataComponents.ADJUST_HEIGHT, 0.45F)
            )
            .model(() -> (ctx, prov) -> prov.createWithExistingModel(ctx.get(), LTExtras.id("item/big_boot")))
            .register();

    public static final ItemEntry<Item> FORKLIFT_CERTIFICATION = REGISTRATE.item("forklift_certification", Item::new)
            .register();

    public static final ItemEntry<SpawnEggItem> FORKLIFT_SPAWN_EGG = REGISTRATE.item("forklift_spawn_egg", SpawnEggItem::new)
            .properties(properties -> properties.spawnEgg(ExtraEntities.FORKLIFT.get()))
            .register();

    public static final ItemEntry<SpawnEggItem> WATER_COOLER_SPAWN_EGG = REGISTRATE.item("water_cooler_spawn_egg", SpawnEggItem::new)
            .properties(properties -> properties.spawnEgg(ExtraEntities.WATER_COOLER.get()))
            .register();

    public static final ItemEntry<SpawnEggItem> AMAZON_RIVER_DOLPHIN_SPAWN_EGG = REGISTRATE.item("amazon_river_dolphin_spawn_egg", SpawnEggItem::new)
            .properties(properties -> properties.spawnEgg(ExtraEntities.AMAZON_RIVER_DOLPHIN.get()))
            .register();

    public static final ItemEntry<SpawnEggItem> GLASS_FROG_SPAWN_EGG = REGISTRATE.item("glass_frog_spawn_egg", SpawnEggItem::new)
            .properties(properties -> properties.spawnEgg(ExtraEntities.GLASS_FROG.get()))
            .register();

    public static final ItemEntry<CleaningItemFrameItem> CLEANING_ITEM_FRAME = REGISTRATE.item("cleaning_item_frame", CleaningItemFrameItem::new)
            .register();

    public static final ItemEntry<Item> BLOODWOOD_SAP = REGISTRATE.item("bloodwood_sap", Item::new).register();

    public static final ItemEntry<Item> GUAYUSA_LEAF = REGISTRATE.item("guayusa_leaf", Item::new).register();
    public static final ItemEntry<Item> GUAYUSA_TEA = REGISTRATE.item("guayusa_tea", Item::new).register();

    public static final ItemEntry<ConfigurableCrossbowItem> ROCKET_CROSSBOW = REGISTRATE.item("rocket_crossbow", (properties) -> new ConfigurableCrossbowItem(properties, new ConfigurableCrossbowItem.Settings(
            false, Optional.empty(), Optional.empty(), ConfigurableCrossbowItem.FIREWORK_ONLY, ConfigurableCrossbowItem.FIREWORK_ONLY
            )))
            .properties(properties -> properties
                    .stacksTo(1)
                    .durability(9)
                    .useCooldown(4.0f)
                    .component(DataComponents.CHARGED_PROJECTILES, new ChargedProjectiles(
                            List.of( new ItemStackTemplate(Items.FIREWORK_ROCKET, 1, DataComponentPatch.builder().set(DataComponents.FIREWORKS, new Fireworks(2, List.of(
                                    new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(16753964), IntList.of(5748024), true, true),
                                    new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(16753964), IntList.of(5748024), true, true),
                                    new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(16753964), IntList.of(5748024), true, true),
                                    new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(16753964), IntList.of(5748024), true, true)
                            ))).build()))
                    ))
                    .component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.CHARGED_PROJECTILES, true).withHidden(DataComponents.MAX_DAMAGE, true).withHidden(DataComponents.DAMAGE, true))
                    .component(DataComponents.WEAPON, new Weapon(1, 0))
            )
            .register();

    public static final ItemEntry<ConfigurableCrossbowItem> RAPID_FIRE_CROSSBOW = REGISTRATE.item("rapid_fire_crossbow", (properties) -> new ConfigurableCrossbowItem(properties, new ConfigurableCrossbowItem.Settings(
                    true, Optional.of(3.0), Optional.of(0.01), ProjectileWeaponItem.ARROW_ONLY, ProjectileWeaponItem.ARROW_ONLY
            )))
            .initialProperties(() -> new Item.Properties())
            .properties(properties -> properties
                    .stacksTo(1)
                    .component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.CHARGED_PROJECTILES, true))
                    .component(ExtraDataComponents.CROSSBOW_CHARGE_TIME, 0.2f)
            )
            .register();

    public static void init() {
    }

    private static class Models {
        private static final TextureSlot SLOT_HAT = TextureSlot.create("hat");

        public static final ModelTemplate GLASSES_EQUIPPED_TEMPLATE = ModelTemplates.createItem(LTExtras.id("template_glasses_equipped").toString(), "_equipped", TextureSlot.LAYER0);
        public static final ModelTemplate TOP_HAT_TEMPLATE = ModelTemplates.createItem(LTExtras.id("template_top_hat").toString(), SLOT_HAT);

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

        private static void generateHeadEquippable(DataGenContext<Item, Item> ctx, RegistrateItemModelGenerator prov, Identifier model, Identifier equippedModel) {
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

        private static void generateTopHat(DataGenContext<Item, Item> ctx, RegistrateItemModelGenerator prov) {
            generateTopHat(ctx, prov, TextureMapping.getItemTexture(ctx.get()));
        }

        private static void generateTopHat(DataGenContext<Item, Item> ctx, RegistrateItemModelGenerator prov, Material texture) {
            Identifier model = TOP_HAT_TEMPLATE.create(ctx.get(), TextureMapping.singleSlot(SLOT_HAT, texture), prov.modelOutput);
            prov.itemModelOutput.accept(ctx.get(), plainModel(model));
        }
    }

    @SafeVarargs
    private static DataComponentInitializers.SingleComponentInitializer<List<Holder<ModelModifier>>> modifierList(ResourceKey<ModelModifier>... modifiers) {
        return context -> {
            List<Holder<ModelModifier>> holders = new ArrayList<>();
            for (ResourceKey<ModelModifier> key : modifiers) {
                holders.add(context.getOrThrow(key));
            }
            return holders;
        };
    }

}
