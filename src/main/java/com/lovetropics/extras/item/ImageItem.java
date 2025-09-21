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
            imageWithHeight("Papyrus Canary", "canary_papyrus", 3.0f, 256, 335),
            imageWithHeight("Habitat Restoration - Riverbank", "habitat_restoration_riverbank_1", 3.0f, 256, 340),
            imageWithWidth("Papyrus Gonolek", "papyrus_gonolek", 2.0f, 266, 200),
            imageWithHeight("Papyrus Harvest", "papyrus_harvest", 2.0f, 340, 256),
            imageWithWidth("Shoebill Stork", "shoebill_stork", 3.0f, 317, 256),
            imageWithWidth("White-Winged Warbler", "white_winged_warbler", 3.0f, 301, 200),
            imageWithHeight("Bird Survey", "bird_survey", 2.0f, 400, 301),
            imageWithHeight("Bridge", "bridge", 3.0f, 300, 450),
            imageWithHeight("Love Tropics", "love_tropics", 1.0f, 128, 128),
            imageWithHeight("WEA", "wea", 1.0f, 128, 128),
            imageWithWidth("Mpanga River", "mpanga_river", 2.0f, 256, 340),
            imageWithWidth("Papyrus", "papyrus", 2.0f, 341, 256),
            imageWithWidth("Papyrus Yellow Warbler", "papyrus_yellow_warbler", 1.0f, 256, 184),
            imageWithHeight("Women Holding Seeds", "women_holding_seeds", 1.0f, 300, 200)
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
