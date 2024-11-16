package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.client.map.ClientMapManager;
import com.lovetropics.extras.data.poi.MapConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

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

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, tooltipComponents, tooltipFlag);
        Holder<MapConfig> map = itemStack.get(ExtraDataComponents.MAP);
		if (map != null) {
			tooltipComponents.add(ComponentUtils.mergeStyles(map.value().description().copy(), Style.EMPTY.withColor(ChatFormatting.GRAY)));
		}
    }
}
