package com.lovetropics.extras.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CeilingCarpetBlock extends CarpetBlock {

    private static final VoxelShape SHAPE = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public CeilingCarpetBlock(Properties props) {
        super(props);
    }

    @Override
    protected VoxelShape getShape(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos, final CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        return !levelReader.isEmptyBlock(blockPos.above());
    }
}
