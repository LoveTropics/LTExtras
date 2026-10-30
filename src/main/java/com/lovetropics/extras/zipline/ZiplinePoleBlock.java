package com.lovetropics.extras.zipline;

import com.lovetropics.extras.ExtraBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Ziplines are strung between these. Standing on the floor, it is a two block-tall pole, but it can also be mounted on
 * a wall or hung from a ceiling as a single block. It has no collision so that riders can fly past it, and it doesn't
 * need the block that it is attached to in order to stay up.
 */
public class ZiplinePoleBlock extends Block implements EntityBlock {
    public static final MapCodec<ZiplinePoleBlock> CODEC = simpleCodec(ZiplinePoleBlock::new);
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    // Only the floor pole has an upper half
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    // Where ropes are tied, relative to the block holding the block entity
    public static final Vec3 FLOOR_ANCHOR = new Vec3(0.5, 2.0, 0.5);
    public static final Vec3 MOUNTED_ANCHOR = new Vec3(0.5, 0.5, 0.5);

    private static final VoxelShape FLOOR_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    private static final Map<Direction, VoxelShape> WALL_SHAPES = Shapes.rotateHorizontal(Block.box(5.0, 3.0, 6.0, 11.0, 13.0, 16.0));
    private static final VoxelShape CEILING_SHAPE = Block.box(5.0, 6.0, 5.0, 11.0, 16.0, 11.0);

    public ZiplinePoleBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACE, AttachFace.FLOOR)
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
        );
    }

    @Override
    protected MapCodec<? extends ZiplinePoleBlock> codec() {
        return CODEC;
    }

    public static BlockPos getBasePos(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }

    public static Vec3 getAnchorOffset(BlockState state) {
        return state.getValue(FACE) == AttachFace.FLOOR ? FLOOR_ANCHOR : MOUNTED_ANCHOR;
    }

    private static VoxelShape getShape(BlockState state) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> FLOOR_SHAPE;
            case WALL -> WALL_SHAPES.get(state.getValue(FACING));
            case CEILING -> CEILING_SHAPE;
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return getShape(state);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (level.isClientSide() || !(entity instanceof LivingEntity living) || !ZiplineRider.canAutoAttach(living)) {
            return;
        }
        // Head along the rope that the mob is walking or facing towards
        Vec3 motion = entity.getDeltaMovement();
        Vec3 heading = motion.horizontalDistanceSqr() > 1.0e-4 ? motion : entity.getLookAngle();
        ZiplineSegment segment = ZiplineNetwork.get(level).pickSegmentFrom(getBasePos(pos, state), heading);
        if (segment != null) {
            ZiplineRider.startRiding(entity, new ZiplineRide(segment, 0.0, motion.dot(segment.tangent(0.0)), 1));
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (state.getValue(FACE) == AttachFace.FLOOR) {
            DoubleBlockHalf half = state.getValue(HALF);
            boolean towardsOtherHalf = directionToNeighbour.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (directionToNeighbour == Direction.UP);
            if (towardsOtherHalf && !(neighbourState.is(this) && neighbourState.getValue(HALF) != half)) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        Direction face = context.replacingClickedOnBlock() ? Direction.UP : context.getClickedFace();
        return switch (face) {
            case UP -> {
                BlockPos pos = context.getClickedPos();
                Level level = context.getLevel();
                if (pos.getY() < level.getMaxY() && level.getBlockState(pos.above()).canBeReplaced(context)) {
                    yield state.setValue(FACE, AttachFace.FLOOR);
                }
                yield null;
            }
            case DOWN -> state.setValue(FACE, AttachFace.CEILING);
            default -> state.setValue(FACE, AttachFace.WALL).setValue(FACING, face);
        };
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        if (state.getValue(FACE) == AttachFace.FLOOR) {
            level.setBlockAndUpdate(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER));
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            if (player.preventsBlockDrops()) {
                preventDropFromBottomPart(level, pos, state, player);
            } else {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            ZiplineNetwork.get(level).removePole(pos);
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack destroyedWith) {
        super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, destroyedWith);
    }

    private static void preventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos bottomPos = pos.below();
            BlockState bottomState = level.getBlockState(bottomPos);
            if (bottomState.is(state.getBlock()) && bottomState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(bottomPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, bottomPos, Block.getId(bottomState));
            }
        }
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, HALF);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new ZiplinePoleBlockEntity(ExtraBlocks.ZIPLINE_POLE_ENTITY.get(), pos, state);
        }
        return null;
    }
}
