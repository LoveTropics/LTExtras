package com.lovetropics.extras.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SixWayBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PianguasBlock extends Block implements IWaterLoggable {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final BooleanProperty UP = SixWayBlock.UP;
    private static final BooleanProperty DOWN = SixWayBlock.DOWN;
    private static final BooleanProperty NORTH = SixWayBlock.NORTH;
    private static final BooleanProperty EAST = SixWayBlock.EAST;
    private static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    private static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final Map<Direction, BooleanProperty> ATTACHMENTS = SixWayBlock.FACING_TO_PROPERTY_MAP;

    private static final VoxelShape UP_SHAPE = Block.makeCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape DOWN_SHAPE = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    private static final Direction[] DIRECTIONS = Direction.values();

    private final Map<BlockState, VoxelShape> stateToShape;

    public PianguasBlock(AbstractBlock.Properties properties) {
        super(properties);

        this.setDefaultState(this.stateContainer.getBaseState()
                .with(UP, false)
                .with(DOWN, false)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(WATERLOGGED, false)
        );

        this.stateToShape = this.stateContainer.getValidStates().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        PianguasBlock::getShapeForState
                ));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.stateToShape.get(state);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return hasAttachments(removeInvalidAttachments(state, world, pos));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();

        BlockState currentState = world.getBlockState(pos);
        boolean extend = currentState.matchesBlock(this);

        BlockState placementState = extend ? currentState : this.getDefaultState();

        Fluid fluid = world.getFluidState(pos).getFluid();
        placementState = placementState.with(WATERLOGGED, fluid == Fluids.WATER);

        for (Direction direction : context.getNearestLookingDirections()) {
            BooleanProperty property = getPropertyFor(direction);
            boolean replacing = extend && currentState.get(property);
            if (!replacing && canAttachTo(world, pos.offset(direction), direction)) {
                return placementState.with(property, true);
            }
        }

        return extend ? placementState : null;
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState adjacentState, IWorld world, BlockPos currentPos, BlockPos adjacentPos) {
        BlockState newState = removeInvalidAttachments(state, world, currentPos);
        if (!hasAttachments(newState)) {
            return Blocks.AIR.getDefaultState();
        }

        if (state.get(WATERLOGGED)) {
            world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return newState;
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext context) {
        BlockState currentState = context.getWorld().getBlockState(context.getPos());
        if (currentState.matchesBlock(this)) {
            return getAttachmentCount(currentState) < ATTACHMENTS.size();
        } else {
            return super.isReplaceable(state, context);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90: return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
            case CLOCKWISE_90: return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
            default: return state;
        }
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            case FRONT_BACK: return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            default: return super.mirror(state, mirror);
        }
    }

    public static boolean canAttachTo(IBlockReader world, BlockPos attachPos, Direction direction) {
        BlockState attachState = world.getBlockState(attachPos);
        VoxelShape attachShape = attachState.getCollisionShapeUncached(world, attachPos);
        return Block.doesSideFillSquare(attachShape, direction.getOpposite());
    }

    private static BlockState removeInvalidAttachments(BlockState state, IBlockReader world, BlockPos pos) {
        for (Direction direction : DIRECTIONS) {
            BooleanProperty property = getPropertyFor(direction);
            if (!state.get(property)) continue;

            BlockPos attachPos = pos.offset(direction);
            if (!canAttachTo(world, attachPos, direction)) {
                state = state.with(property, false);
            }
        }

        return state;
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape shape = VoxelShapes.empty();
        if (state.get(UP)) shape = UP_SHAPE;
        if (state.get(DOWN)) shape = DOWN_SHAPE;
        if (state.get(NORTH)) shape = VoxelShapes.or(shape, SOUTH_SHAPE);
        if (state.get(SOUTH)) shape = VoxelShapes.or(shape, NORTH_SHAPE);
        if (state.get(EAST)) shape = VoxelShapes.or(shape, WEST_SHAPE);
        if (state.get(WEST)) shape = VoxelShapes.or(shape, EAST_SHAPE);

        return shape;
    }

    private static boolean hasAttachments(BlockState state) {
        return getAttachmentCount(state) > 0;
    }

    private static int getAttachmentCount(BlockState state) {
        int count = 0;
        for (BooleanProperty property : ATTACHMENTS.values()) {
            if (state.get(property)) {
                count++;
            }
        }
        return count;
    }

    private static BooleanProperty getPropertyFor(Direction side) {
        return ATTACHMENTS.get(side);
    }
}
