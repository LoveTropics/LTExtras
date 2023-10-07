package com.lovetropics.extras;

import com.lovetropics.extras.item.EntityWandItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.ModelFile;

import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;

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

    public static final ItemEntry<Item> PASSION_FRUIT = REGISTRATE.item("passion_fruit", Item::new)
            .properties(p -> p.food(new FoodProperties.Builder().build()))
            .model((ctx, prov) -> prov.withExistingParent("passion_fruit", prov.mcLoc("item/generated"))
                    .texture("layer0", prov.modLoc("item/passion_fruit"))
                    .rootTransforms()
                    .end()
                    .transforms()
                    .transform(ItemDisplayContext.GROUND)
                    .scale(0.2f)
                    .translation(1.0f, 2.0f, 1.0f)
                    .end()
            )
            .register();

    public static final ItemEntry<Item> OPEN_PASSION_FRUIT = REGISTRATE.item("open_passion_fruit", Item::new)
            .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ctx.get())
                    .requires(PASSION_FRUIT.get(), 1)
                    .unlockedBy("has_passion_fruit", has(PASSION_FRUIT.get()))
                    .save(prov))
            .properties(p -> p.food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).fast().build()))
            .model((ctx, prov) -> prov.withExistingParent("open_passion_fruit", prov.mcLoc("item/generated"))
                    .texture("layer0", prov.modLoc("item/open_passion_fruit"))
                    .rootTransforms()
                    .end()
                    .transforms()
                    .transform(ItemDisplayContext.GROUND)
                    .scale(0.2f)
                    .translation(1.0f, 2.0f, 1.0f)
                    .end()
            )
            .register();

    public static void init() {
    }
}
