package com.lovetropics.extras;

import com.lovetropics.extras.item.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;

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
            .model((ctx, prov) -> {
            })
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

    public static final ItemEntry<Item> BANANA = REGISTRATE.item("banana", Item::new)
            .properties(p -> p.food(new FoodProperties.Builder().build()))
            .model((ctx, prov) -> prov.withExistingParent("banana", prov.mcLoc("item/generated"))
                    .texture("layer0", prov.modLoc("item/banana"))
                    .rootTransforms()
                    .end()
                    .transforms()
                    .transform(ItemDisplayContext.GROUND)
                    .scale(0.4f)
                    .translation(1.0f, 2.0f, 1.0f)
                    .end()
            )
            .register();

    public static final ItemEntry<Item> BANANA_PEELED = REGISTRATE.item("peeled_banana", Item::new)
            .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ctx.get())
                    .requires(BANANA.get(), 1)
                    .unlockedBy("has_banana", has(BANANA.get()))
                    .save(prov))
            .properties(p -> p.food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).fast().build()))
            .model((ctx, prov) -> prov.withExistingParent("peeled_banana", prov.mcLoc("item/generated"))
                    .texture("layer0", prov.modLoc("item/peeled_banana"))
                    .rootTransforms()
                    .end()
                    .transforms()
                    .transform(ItemDisplayContext.GROUND)
                    .scale(0.4f)
                    .translation(1.0f, 2.0f, 1.0f)
                    .end()
            )
            .register();

    public static final ItemEntry<Item> SOSIJ = REGISTRATE.item("sosij", Item::new)
            .properties(p -> p.food(new FoodProperties.Builder().build()))
            .model((ctx, prov) -> prov.withExistingParent("sosij", prov.mcLoc("item/generated"))
                    .texture("layer0", prov.modLoc("item/sosij"))
                    .rootTransforms()
                    .end()
                    .transforms()
                    .transform(ItemDisplayContext.GROUND)
                    .scale(0.4f)
                    .translation(1.0f, 2.0f, 1.0f)
                    .end()
            )
            .register();

    public static final ItemEntry<Item> SOSIJ_COOKED = REGISTRATE.item("cooked_sosij", Item::new)
            .recipe((ctx, prov) -> SimpleCookingRecipeBuilder.generic(Ingredient.of(SOSIJ), RecipeCategory.MISC, ctx.get(), 1,
                            200, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
                    .unlockedBy("has_sosij", has(SOSIJ)).save(prov, "cooked_sosij"))
            .properties(p -> p.food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).fast().build()))
            .model((ctx, prov) -> prov.withExistingParent("cooked_sosij", prov.mcLoc("item/generated"))
                    .texture("layer0", prov.modLoc("item/cooked_sosij"))
                    .rootTransforms()
                    .end()
                    .transforms()
                    .transform(ItemDisplayContext.GROUND)
                    .scale(0.4f)
                    .translation(1.0f, 2.0f, 1.0f)
                    .end()
            )
            .register();

    public static void init() {
    }
}
