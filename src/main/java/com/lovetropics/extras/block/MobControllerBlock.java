package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class MobControllerBlock extends Block {
    public MobControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MobControllerBlockEntity(ExtraBlocks.MOB_CONTROLLER_BE.get());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
