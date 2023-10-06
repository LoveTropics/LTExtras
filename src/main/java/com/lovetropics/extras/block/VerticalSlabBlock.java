package com.lovetropics.extras.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.world.level.block.state.properties.SlabType.BOTTOM;
import static net.minecraft.world.level.block.state.properties.SlabType.DOUBLE;


public class VerticalSlabBlock extends SlabBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final EnumProperty<SlabType> TYPE = EnumProperty.create("type", SlabType.class);
    public static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");

    private static final VoxelShape SLAB_SHAPE_EAST = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SLAB_SHAPE_WEST = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    private static final VoxelShape SLAB_SHAPE_NORTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    private static final VoxelShape SLAB_SHAPE_SOUTH = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SLAP_SHAPE_DOUBLE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public VerticalSlabBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, BOTTOM)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(TYPE) == DOUBLE) {
            return SLAP_SHAPE_DOUBLE;
        }

        switch (state.getValue(FACING)) {
            case SOUTH -> {
                return SLAB_SHAPE_SOUTH;
            }
            case WEST -> {
                return SLAB_SHAPE_WEST;
            }
            case EAST -> {
                return SLAB_SHAPE_EAST;
            }
            default -> {
                return SLAB_SHAPE_NORTH;
            }
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        boolean itemInHandIsThisSlab = context.getItemInHand().getItem() == this.asItem();
        boolean notDoubleSlab = state.getValue(TYPE) != DOUBLE;
        boolean notUpOrDown = context.getClickedFace() != Direction.UP && context.getClickedFace() != Direction.DOWN;
        boolean sameFace = context.getClickedFace() == state.getValue(FACING).getOpposite();
        return notDoubleSlab && notUpOrDown && sameFace && itemInHandIsThisSlab;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos clickedPos = context.getClickedPos();
        BlockPos thisPos = clickedPos.relative(context.getHorizontalDirection());
        Direction clickedFace = context.getClickedFace();
        BlockState currentBlock = context.getLevel().getBlockState(thisPos);

        if (currentBlock.is(this) && currentBlock.getValue(FACING) == context.getHorizontalDirection()
                && clickedFace != Direction.DOWN
                && clickedFace != Direction.UP) {
            return currentBlock.setValue(TYPE, DOUBLE).setValue(WATERLOGGED, false).setValue(FACING, context.getHorizontalDirection());
        } else if (context.getLevel().getBlockState(clickedPos).is(this) && clickedFace == Direction.UP) {
            return defaultBlockState().setValue(TYPE, BOTTOM).setValue(FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, false);
        }

        return Objects.requireNonNullElse(super.getStateForPlacement(context), defaultBlockState()).setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(TYPE) != DOUBLE;
    }
}
