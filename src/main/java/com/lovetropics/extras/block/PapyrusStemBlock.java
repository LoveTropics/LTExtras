package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PapyrusStemBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    private static final VoxelShape SHAPE = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    protected static final int MAX_HEIGHT = 4;
    private static final int MIN_HEIGHT_FOR_FLOWERS = 2;

    public PapyrusStemBlock(Properties properties) {
        super(properties.randomTicks());
        registerDefaultState(getStateDefinition().any().setValue(TYPE, Type.PLAIN).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canGrow(level, pos)) {
            return;
        }

        //50% to grow taller
        if (random.nextBoolean()) {
            level.setBlockAndUpdate(pos.above(), defaultBlockState().setValue(TYPE, state.getValue(TYPE)));
            return;
        }

        //if taller than MIN_HEIGHT_FOR_FLOWERS(2), 50% to grow flower
        if (random.nextBoolean() && hasStemHeightInclusive(level, pos, MIN_HEIGHT_FOR_FLOWERS)) {
            level.setBlockAndUpdate(pos.above(), ExtraBlocks.PAPYRUS_UMBEL.getDefaultState().setValue(PapyrusUmbelBlock.TYPE, state.getValue(TYPE)));
        }

        //some chance to stop growing? only if has flower?
    }

    private boolean canGrow(ServerLevel level, BlockPos pos) {
        // Don't grow naturally if there is water above us
        boolean aboveIsEmpty = level.isEmptyBlock(pos.above());
		return aboveIsEmpty && !hasStemHeightInclusive(level, pos, MAX_HEIGHT);
    }

    private boolean hasStemHeightInclusive(ServerLevel level, BlockPos pos, int height) {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        for (int i = 0; i <= height; i++) {
            mutablePos.move(Direction.DOWN);
            if (!level.getBlockState(mutablePos).is(this)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());

        return stateBelow.is(BlockTags.DIRT) || stateBelow.is(this);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, WATERLOGGED);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        blockState.randomTick(serverLevel, blockPos, randomSource);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        if (levelReader instanceof ServerLevel serverLevel) {
            return canGrow(serverLevel, blockPos);
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return direction.getAxis() == Direction.Axis.Y && adjacentState.is(this);
    }

    public enum Type implements StringRepresentable {
        PLAIN("plain"),
        DRY("dry"),
        DEAD("dead"),
        ;

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
