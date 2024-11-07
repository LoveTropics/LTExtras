package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ImageItem extends Item {
    public static final List<ImageData> PRESETS = List.of(
            new ImageData(Component.literal("[Redacted]"), LTExtras.location("textures/images/ccfucc_banner.png"), 3.0f, 5.0f),
            new ImageData(Component.literal("Plantain Ent"), LTExtras.location("textures/images/plantain_ent.png"), 2.5f, 3.0f),
            new ImageData(Component.literal("Manatee"), LTExtras.location("textures/images/manatee.png"), 2.5f, 3.0f)
    );

    public ImageItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        ImageData image = stack.get(ExtraDataComponents.IMAGE);
        if (image != null && image.name().isPresent()) {
            lines.add(image.name().get().copy().withStyle(ChatFormatting.GRAY));
        }
    }
}
