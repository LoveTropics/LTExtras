package com.lovetropics.extras.item;

import com.lovetropics.extras.client.screen.container.CollectibleBasketScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CollectibleBasketItem extends Item {
    public CollectibleBasketItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        if (level.isClientSide()) {
            openScreen(player);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    private static void openScreen(final Player player) {
        Minecraft.getInstance().setScreen(new CollectibleBasketScreen(player.getInventory()));
    }
}
