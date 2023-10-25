package com.lovetropics.extras.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class InviteItem extends ImageItem {
    public InviteItem(final Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(final ItemStack stack) {
        return super.getName(stack).copy().withStyle(ChatFormatting.OBFUSCATED);
    }
}
