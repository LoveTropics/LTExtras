package com.lovetropics.extras.block;

import com.lovetropics.extras.LTExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

// sorry
@EventBusSubscriber(modid = LTExtras.MODID)
public class ScientificNameBlock extends Block {
    private final String scientificName;

    public ScientificNameBlock(Properties properties, String scientificName) {
        super(properties);
        this.scientificName = scientificName;
    }

    @SubscribeEvent
    public static void addToTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ScientificNameBlock seagrass) {
            event.getToolTip().add(Component.literal(seagrass.scientificName).withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
        }
    }
}
