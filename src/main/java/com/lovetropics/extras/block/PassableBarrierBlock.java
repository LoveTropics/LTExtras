package com.lovetropics.extras.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import net.minecraft.block.AbstractBlock.Properties;

public final class PassableBarrierBlock extends CustomBarrierBlock {
    public PassableBarrierBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.getShape(state, world, pos, context);
    }
}
