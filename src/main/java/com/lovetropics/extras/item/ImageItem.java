package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ImageItem extends Item {
    public static final List<ImageData> PRESETS = List.of(
            new ImageData(Component.literal("[Redacted]"), LTExtras.location("textures/images/ccfucc_banner.png"), 3.0f, 5.0f),
            new ImageData(Component.literal("Plantain Ent"), LTExtras.location("textures/images/plantain_ent.png"), 2.5f, 3.0f),
            new ImageData(Component.literal("Manatee"), LTExtras.location("textures/images/manatee.png"), 2.5f, 3.0f),
            new ImageData(Component.literal("Shoebill"), LTExtras.location("textures/images/shoebill.png"), 2.5f, 3.0f),
            new ImageData(Component.literal("Team Cents Poster"), LTExtras.location("textures/images/team_cents_poster.png"), 1.0f, 1.0f),
            new ImageData(Component.literal("Team No Cents Poster"), LTExtras.location("textures/images/team_no_cents_poster.png"), 1.0f, 1.0f),
            imageWithHeight("Love Tropics", "love_tropics", 1.0f, 128, 128),
            imageWithWidth("Certificates", "certificates", 2.0f, 500, 306),
            imageWithWidth("Class 1", "class_1", 2.0f, 350, 263),
            imageWithHeight("EVA", "eva_1", 1.0f, 400, 180),
            imageWithWidth("Group School", "group_school", 2.0f, 400, 267),
            imageWithWidth("Longhouse Meet", "longhouse_meet", 2.0f, 400, 300),
            imageWithWidth("Student Seated", "students_seated", 2.0f, 350, 234),
            imageWithWidth("Tree Planting", "tree_planting", 2.0f, 300, 200),
            imageWithWidth("Longhouse Meal", "longhouse_meal", 2.0f, 300, 225)
    );

    private static ImageData imageWithWidth(String name, String path, float blockWidth, int textureWidth, int textureHeight) {
        float blockHeight = blockWidth / textureWidth * textureHeight;
        return new ImageData(Component.literal(name), LTExtras.location("textures/images/" + path + ".png"), blockWidth, blockHeight);
    }

    private static ImageData imageWithHeight(String name, String path, float blockHeight, int textureWidth, int textureHeight) {
        float blockWidth = blockHeight / textureHeight * textureWidth;
        return new ImageData(Component.literal(name), LTExtras.location("textures/images/" + path + ".png"), blockWidth, blockHeight);
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
