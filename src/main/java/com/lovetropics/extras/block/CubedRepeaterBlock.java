package com.lovetropics.extras.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CubedRepeaterBlock extends DiodeBlock {
    public static final MapCodec<CubedRepeaterBlock> CODEC = simpleCodec(CubedRepeaterBlock::new);
    public CubedRepeaterBlock(Properties p_55801_) {
        super(p_55801_);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false)
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    protected MapCodec<? extends DiodeBlock> codec() {
        return CODEC;
    }

    @Override
    protected int getDelay(BlockState state) {
        return 0;
    }

    @Override
    protected boolean sideInputDiodesOnly() {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (!blockState.getValue(POWERED)) {
            return 0;
        } else {
            if(side == Direction.DOWN){
                if(blockState.getValue(POWERED)){
                    return 15;
                }
            }
            return blockState.getValue(FACING) == side ? this.getOutputSignal(blockAccess, pos, blockState) : 0;
        }
    }
}
