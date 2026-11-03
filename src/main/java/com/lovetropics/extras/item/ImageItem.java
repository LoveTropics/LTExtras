package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ImageItem extends Item {
    public static final List<ImageData> PRESETS = List.of(
            new ImageData(Component.literal("[Redacted]"), LTExtras.id("textures/images/ccfucc_banner.png"), 3.0f, 5.0f),
            new ImageData(Component.literal("Plantain Ent"), LTExtras.id("textures/images/plantain_ent.png"), 2.5f, 3.0f),
            new ImageData(Component.literal("Manatee"), LTExtras.id("textures/images/manatee.png"), 2.5f, 3.0f),
            new ImageData(Component.literal("Shoebill"), LTExtras.id("textures/images/shoebill.png"), 2.5f, 3.0f),
            new ImageData(Component.literal("Team Cents Poster"), LTExtras.id("textures/images/team_cents_poster.png"), 1.0f, 1.0f),
            new ImageData(Component.literal("Team No Cents Poster"), LTExtras.id("textures/images/team_no_cents_poster.png"), 1.0f, 1.0f),

            imageWithHeight("Cracked Cubes", "sponsor/cracked_cubes", 1.0f, 400, 80),
            imageWithHeight("Creeperhost", "sponsor/creeperhost", 1.0f, 400, 80),
            imageWithHeight("Essential", "sponsor/essential", 1.0f, 400, 80),
            imageWithHeight("Modrinth", "sponsor/modrinth", 1.0f, 400, 80),
            imageWithHeight("Mojang", "sponsor/mojang", 1.0f, 250, 211),
            imageWithHeight("Pathway Studios", "sponsor/pathway_studios", 1.0f, 282, 160),
            imageWithHeight("Spark Universe", "sponsor/spark_universe", 1.0f, 400, 80),

            imageWithHeight("Love Tropics", "love_tropics", 1.0f, 128, 128),
            imageWithWidth("Dulao Forest", "2026/dulao_forest", 3.0f, 300, 216),
            imageWithWidth("Dulao 1", "2026/dulao_1", 4.0f, 600, 308),
            imageWithWidth("Dulao 2", "2026/dulao_2", 4.0f, 600, 328),
            imageWithWidth("Dulao Map", "2026/dulao_map", 3.0f, 500, 375)
    );

    private static ImageData imageWithWidth(String name, String path, float blockWidth, int textureWidth, int textureHeight) {
        float blockHeight = blockWidth / textureWidth * textureHeight;
        return new ImageData(Component.literal(name), LTExtras.id("textures/images/" + path + ".png"), blockWidth, blockHeight);
    }

    private static ImageData imageWithHeight(String name, String path, float blockHeight, int textureWidth, int textureHeight) {
        float blockWidth = blockHeight / textureHeight * textureWidth;
        return new ImageData(Component.literal(name), LTExtras.id("textures/images/" + path + ".png"), blockWidth, blockHeight);
    }

    public ImageItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        ImageData image = stack.get(ExtraDataComponents.IMAGE);
        if (image != null && image.name().isPresent()) {
            return image.name().get();
        }
        return super.getName(stack);
    }
}
