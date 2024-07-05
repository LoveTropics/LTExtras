package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ImageItem extends Item {
    public static final List<ImageData> PRESETS = List.of(
            new ImageData(Component.literal("[Redacted]"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/ccfucc_banner.png"), 3.0f, 5.0f),
            new ImageData(Component.literal("Panorama: Plantain"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/plantain_pano_50.png"), 8.73f, 2.0f),
            new ImageData(Component.literal("Compost"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/compost.png"), 2.664f, 2.0f),
            new ImageData(Component.literal("Cook Stove Top"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/cook_stove_top_r.png"), 2.64f, 2.0f),
            new ImageData(Component.literal("Group R"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/group_r.png"), 2.67f, 2.0f),
            new ImageData(Component.literal("Jocote"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/jocote.png"), 3.0f, 2.0f),
            new ImageData(Component.literal("Making Organic Fungicide"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/making_org_fung.png"), 2.672f, 2.0f),
            new ImageData(Component.literal("Panorama: Garden Bike"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/pano_garden_bike.png"), 7.89f, 3.0f),
            new ImageData(Component.literal("Agroforestry"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/agroforestry.png"), 2.0f, 2.0f),
            new ImageData(Component.literal("Big Scenic"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/big_scenic.png"), 9.75f, 3.0f),
            new ImageData(Component.literal("Coop 1"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/coop_1.png"), 1.48f, 2.0f),
            new ImageData(Component.literal("Coop 2"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/coop_2.png"), 1.5f, 2.0f),
            new ImageData(Component.literal("Diversity"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/diversity.png"), 2.66f, 2.0f),
            new ImageData(Component.literal("Healthy"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/healthy.png"), 2.66f, 2.0f),
            new ImageData(Component.literal("Kate WHO"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/kate_who.png"), 2.645f, 2.0f),
            new ImageData(Component.literal("Mangrove"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/mangrove.png"), 3.62f, 2.0f),
            new ImageData(Component.literal("Passion Fruit #1"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/passion_fruit_1.png"), 3.0f, 2.0f),
            new ImageData(Component.literal("Passion Fruit #2"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/passion_fruit_2.png"), 2.0f, 2.0f),
            new ImageData(Component.literal("Passion Fruit #3"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/passion_fruit_3.png"), 3.11f, 2.0f),
            new ImageData(Component.literal("Passion Fruit Flower"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/passion_fruit_flower.png"), 2.66f, 2.0f),
            new ImageData(Component.literal("Plantain Trees"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/plantain_trees.png"), 2.66f, 2.0f),
            new ImageData(Component.literal("SHI visit WHO"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/shi_visit_who.png"), 2.67f, 2.0f),
            new ImageData(Component.literal("Thatch House"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/thatch_house.png"), 1.5f, 2.0f),
            new ImageData(Component.literal("Water"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/water.png"), 1.5f, 2.0f),
            new ImageData(Component.literal("Plantain Ent"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/plantain_ent.png"), 2.5f, 3.0f),
            new ImageData(Component.literal("Manatee"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/manatee.png"), 2.5f, 3.0f),
            new ImageData(Component.literal("Manatee (Real)"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/manatee_real.png"), 3.0f, 2.0f),
            new ImageData(Component.literal("Gibnut 1"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/gibnut.png"), 2.66f, 2.0f),
            new ImageData(Component.literal("Gibnut 2"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/gibnut_2.png"), 3.67f, 2.0f),
            new ImageData(Component.literal("Mahogany Nut"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/mahogany_nut.png"), 1.48f, 2.0f),
            new ImageData(Component.literal("Slender Harvest Mouse"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/slender_harvest_mouse.png"), 2.75f, 2.0f),
            new ImageData(Component.literal("Toucan"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/toucan.png"), 2.0f, 2.0f),
            new ImageData(Component.literal("Background: STT2"), ResourceLocation.fromNamespaceAndPath(LTExtras.MODID, "textures/images/play_stt2.png"), 22.0f, 11.0f)
    );

    public ImageItem(final Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(final ItemStack stack, TooltipContext context, final List<Component> lines, final TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        final ImageData image = stack.get(ExtraDataComponents.IMAGE);
        if (image != null && image.name().isPresent()) {
            lines.add(image.name().get().copy().withStyle(ChatFormatting.GRAY));
        }
    }
}
