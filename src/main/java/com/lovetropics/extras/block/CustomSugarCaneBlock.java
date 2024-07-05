package com.lovetropics.extras.block;

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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.TriState;

public final class CustomSugarCaneBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public CustomSugarCaneBlock(final Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TYPE, Type.TOP).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource rand) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        final Level level = context.getLevel();
        final FluidState fluid = level.getFluidState(context.getClickedPos());
        return defaultBlockState()
                .setValue(TYPE, getTypeAt(level, context.getClickedPos()))
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(final BlockState state, final Direction direction, final BlockState neighborState, final LevelAccessor level, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return state.setValue(TYPE, getTypeAt(level, pos));
    }

    private Type getTypeAt(final LevelAccessor world, final BlockPos pos) {
        final BlockState aboveState = world.getBlockState(pos.above());
        if (aboveState.is(this)) {
            final BlockState belowState = world.getBlockState(pos.below());
            if (belowState.is(this) || aboveState.getValue(TYPE) == Type.TOP) {
                return Type.MIDDLE;
            }
            return Type.BOTTOM;
        } else {
            return Type.TOP;
        }
    }

    @Override
    public boolean canSurvive(final BlockState state, final LevelReader world, final BlockPos pos) {
        final BlockPos groundPos = pos.below();
        final BlockState groundState = world.getBlockState(groundPos);
        final TriState result = groundState.canSustainPlant(world, groundPos, Direction.UP, defaultBlockState());
        if (result.isDefault()) {
            return groundState.getBlock() == this || canGrowOn(groundState);
        }
        return result.isTrue();
    }

    private boolean canGrowOn(final BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(BlockTags.SAND) || state.is(BlockTags.DIRT) || state.is(Tags.Blocks.GRAVELS) || state.is(Blocks.CLAY);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, TYPE);
    }

    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public enum Type implements StringRepresentable {
        BOTTOM("bottom"),
        MIDDLE("middle"),
        TOP("top"),
        ;

        private final String name;

        Type(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
