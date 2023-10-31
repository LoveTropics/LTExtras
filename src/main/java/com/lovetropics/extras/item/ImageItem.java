package com.lovetropics.extras.item;

import com.lovetropics.extras.LTExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ImageItem extends Item {
    public static final List<ImageData> PRESETS = List.of(
            new ImageData(Component.literal("[Redacted]"), new ResourceLocation(LTExtras.MODID, "textures/images/ccfucc_banner.png"), 3.0f, 5.0f),
            new ImageData(Component.literal("Panorama: Plantain"), new ResourceLocation(LTExtras.MODID, "textures/images/plantain_pano_50.png"), 8.73f, 2.0f),
            new ImageData(Component.literal("Compost"), new ResourceLocation(LTExtras.MODID, "textures/images/compost.png"), 2.664f, 2.0f),
            new ImageData(Component.literal("Cook Stove Top"), new ResourceLocation(LTExtras.MODID, "textures/images/cook_stove_top_r.png"), 2.64f, 2.0f),
            new ImageData(Component.literal("Group R"), new ResourceLocation(LTExtras.MODID, "textures/images/group_r.png"), 2.67f, 2.0f),
            new ImageData(Component.literal("Jocote"), new ResourceLocation(LTExtras.MODID, "textures/images/jocote.png"), 3.0f, 2.0f),
            new ImageData(Component.literal("Making Organic Fungicide"), new ResourceLocation(LTExtras.MODID, "textures/images/making_org_fung.png"), 2.672f, 2.0f),
            new ImageData(Component.literal("Panorama: Garden Bike"), new ResourceLocation(LTExtras.MODID, "textures/images/pano_garden_bike.png"), 2.63f, 1.0f),
            new ImageData(Component.literal("Plantain Ent"), new ResourceLocation(LTExtras.MODID, "textures/images/plantain_ent.png"), 2.5f, 3.0f)
    );

    public ImageItem(final Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> lines, final TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        ImageData.get(stack).flatMap(ImageData::name).ifPresent(name -> lines.add(name.copy().withStyle(ChatFormatting.GRAY)));
    }
}
