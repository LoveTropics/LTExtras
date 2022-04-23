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
    public static final Map<Direction, BooleanProperty> ATTACHMENTS = SixWayBlock.PROPERTY_BY_DIRECTION;

    private static final VoxelShape UP_SHAPE = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape DOWN_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape EAST_SHAPE = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape NORTH_SHAPE = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    private static final Direction[] DIRECTIONS = Direction.values();

    private final Map<BlockState, VoxelShape> stateToShape;

    public PianguasBlock(AbstractBlock.Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(WATERLOGGED, false)
        );

        this.stateToShape = this.stateDefinition.getPossibleStates().stream()
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
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        return hasAttachments(removeInvalidAttachments(state, world, pos));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState currentState = world.getBlockState(pos);
        boolean extend = currentState.is(this);

        BlockState placementState = extend ? currentState : this.defaultBlockState();

        Fluid fluid = world.getFluidState(pos).getType();
        placementState = placementState.setValue(WATERLOGGED, fluid == Fluids.WATER);

        for (Direction direction : context.getNearestLookingDirections()) {
            BooleanProperty property = getPropertyFor(direction);
            boolean replacing = extend && currentState.getValue(property);
            if (!replacing && canAttachTo(world, pos.relative(direction), direction)) {
                return placementState.setValue(property, true);
            }
        }

        return extend ? placementState : null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState adjacentState, IWorld world, BlockPos currentPos, BlockPos adjacentPos) {
        BlockState newState = removeInvalidAttachments(state, world, currentPos);
        if (!hasAttachments(newState)) {
            return Blocks.AIR.defaultBlockState();
        }

        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return newState;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext context) {
        BlockState currentState = context.getLevel().getBlockState(context.getClickedPos());
        if (currentState.is(this)) {
            return getAttachmentCount(currentState) < ATTACHMENTS.size();
        } else {
            return super.canBeReplaced(state, context);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90: return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
            case CLOCKWISE_90: return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
            default: return state;
        }
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK: return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
            default: return super.mirror(state, mirror);
        }
    }

    public static boolean canAttachTo(IBlockReader world, BlockPos attachPos, Direction direction) {
        BlockState attachState = world.getBlockState(attachPos);
        VoxelShape attachShape = attachState.getCollisionShape(world, attachPos);
        return Block.isFaceFull(attachShape, direction.getOpposite());
    }

    private static BlockState removeInvalidAttachments(BlockState state, IBlockReader world, BlockPos pos) {
        for (Direction direction : DIRECTIONS) {
            BooleanProperty property = getPropertyFor(direction);
            if (!state.getValue(property)) continue;

            BlockPos attachPos = pos.relative(direction);
            if (!canAttachTo(world, attachPos, direction)) {
                state = state.setValue(property, false);
            }
        }

        return state;
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape shape = VoxelShapes.empty();
        if (state.getValue(UP)) shape = UP_SHAPE;
        if (state.getValue(DOWN)) shape = DOWN_SHAPE;
        if (state.getValue(NORTH)) shape = VoxelShapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(SOUTH)) shape = VoxelShapes.or(shape, NORTH_SHAPE);
        if (state.getValue(EAST)) shape = VoxelShapes.or(shape, WEST_SHAPE);
        if (state.getValue(WEST)) shape = VoxelShapes.or(shape, EAST_SHAPE);

        return shape;
    }

    private static boolean hasAttachments(BlockState state) {
        return getAttachmentCount(state) > 0;
    }

    private static int getAttachmentCount(BlockState state) {
        int count = 0;
        for (BooleanProperty property : ATTACHMENTS.values()) {
            if (state.getValue(property)) {
                count++;
            }
        }
        return count;
    }

    private static BooleanProperty getPropertyFor(Direction side) {
        return ATTACHMENTS.get(side);
    }
}
