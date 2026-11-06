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
            imageWithWidth("Dulao 3", "2026/dulao_3", 4.0f, 550, 286),
            imageWithWidth("Dulao 4", "2026/dulao_4", 4.0f, 500, 375),
            imageWithWidth("Dulao 5", "2026/dulao_5", 4.0f, 400, 300),
            imageWithWidth("Dulao 6", "2026/dulao_6", 4.0f, 400, 300),
            imageWithWidth("Dulao 7", "2026/dulao_7", 5.0f, 667, 300),
            imageWithWidth("Dulao Map", "2026/dulao_map", 3.0f, 500, 375),
            imageWithWidth("Dulao Map 2", "2026/dulao_map2", 3.0f, 512, 512),
            imageWithWidth("Fauna 1", "2026/fauna_1", 2.0f, 256, 171),
            imageWithWidth("Fauna 2", "2026/fauna_2", 2.0f, 256, 181),
            imageWithWidth("Fauna 3", "2026/fauna_3", 2.0f, 256, 171),
            imageWithWidth("Fauna 4", "2026/fauna_4", 2.0f, 300, 200),
            imageWithWidth("Fauna 5", "2026/fauna_5", 2.0f, 256, 171),
            imageWithWidth("Fauna 6", "2026/fauna_6", 2.0f, 256, 191),
            imageWithWidth("Flora 1", "2026/flora_1", 2.0f, 256, 384),
            imageWithWidth("School", "2026/school", 2.0f, 512, 385),
            imageWithWidth("Micro hydro", "2026/micro_hydro", 1.0f, 230, 512),

            imageWithWidth("Direct Relief 2017", "charities/lt_2017", 5.0f, 256, 256),
            imageWithWidth("Team Rubicon 2018", "charities/lt_2018", 5.0f, 200, 71),
            imageWithWidth("Cool Earth 2019", "charities/lt_2019", 5.0f, 200, 75),
            imageWithWidth("Oceana 2020", "charities/lt_2020", 5.0f, 200, 40),
            imageWithWidth("Osa Conservation 2021", "charities/lt_2021", 5.0f, 256, 282),
            imageWithWidth("Project Seagrass 2022", "charities/lt_2022", 5.0f, 256, 101),
            imageWithWidth("Sustainable Harvest International 2023", "charities/lt_2023", 5.0f, 256, 163),
            imageWithWidth("WEA 2024", "charities/lt_2024", 5.0f, 256, 83),
            imageWithWidth("Pachamama Alliance 2025", "charities/lt_2025", 5.0f, 256, 256),
            imageWithWidth("Seacology 2026", "charities/lt_2026", 5.0f, 256, 177),
            imageWithWidth("Seacology 2026 1x1", "charities/lt_2026", 1.0f, 256, 177)
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
