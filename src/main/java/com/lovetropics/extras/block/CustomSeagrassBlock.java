package com.lovetropics.extras.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.Nullable;

import com.lovetropics.extras.ExtraBlocks;

import java.util.List;
import java.util.Random;

public class CustomSeagrassBlock extends SeagrassBlock {

    private final String scientificName;

    public CustomSeagrassBlock(final Properties properties, final String scientificName) {
        super(properties);
        this.scientificName = scientificName;
    }

    @Override
    public void appendHoverText(final ItemStack itemStack, final @Nullable BlockGetter level, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(new TextComponent(scientificName).withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
    }
    
    private final Lazy<BlockState> tall = Lazy.of(() -> ExtraBlocks.REGISTRATE
    		.getOptional("tall_" + ForgeRegistries.BLOCKS.getKey(this).getPath(), Block.class)
    		.map(Block::defaultBlockState)
    		.orElse(null));

    @Override
    public void performBonemeal(ServerLevel p_154498_, Random p_154499_, BlockPos p_154500_, BlockState p_154501_) {
        BlockState blockstate = tall.get();
        if (blockstate == null) return;
        BlockState blockstate1 = blockstate.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
        BlockPos blockpos = p_154500_.above();
        if (p_154498_.getBlockState(blockpos).is(Blocks.WATER)) {
           p_154498_.setBlock(p_154500_, blockstate, 2);
           p_154498_.setBlock(blockpos, blockstate1, 2);
        }

     }
}
