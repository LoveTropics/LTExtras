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
            new ImageData(Component.literal("Plantain Pano 50"), new ResourceLocation(LTExtras.MODID, "textures/images/plantain_pano_50.png"), 8.73f, 2.0f)
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
