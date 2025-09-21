package com.lovetropics.extras.block;

import com.lovetropics.extras.LTExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@EventBusSubscriber(modid = LTExtras.MODID)
public class CustomSeagrassBlock extends SeagrassBlock {

    private final String scientificName;
    @Nullable
    private final Supplier<Supplier<? extends TallSeagrassBlock>> tall;

    public CustomSeagrassBlock(Properties properties, String scientificName, @Nullable Supplier<Supplier<? extends TallSeagrassBlock>> tall) {
        super(properties);
        this.scientificName = scientificName;
        this.tall = tall;
    }

    @SubscribeEvent
    public static void addToTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CustomSeagrassBlock seagrass) {
            event.getToolTip().add(Component.literal(seagrass.scientificName).withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (tall == null) {
            return;
        }

        BlockState bottomState = tall.get().get().defaultBlockState();
        BlockState topState = bottomState.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);

        BlockPos topPos = pos.above();
        if (level.getBlockState(topPos).is(Blocks.WATER)) {
            level.setBlock(pos, bottomState, Block.UPDATE_CLIENTS);
            level.setBlock(topPos, topState, Block.UPDATE_CLIENTS);
        }
    }
}
