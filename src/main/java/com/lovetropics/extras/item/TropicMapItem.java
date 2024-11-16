package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.client.map.ClientMapManager;
import com.lovetropics.extras.data.poi.MapConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TropicMapItem extends Item {
    public TropicMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Holder<MapConfig> map = itemStack.get(ExtraDataComponents.MAP);
        if (level.isClientSide() && map != null) {
            ClientMapManager.openScreen(player, map);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
