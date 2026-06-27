package com.lovetropics.extras.block;

import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.entity.ForkliftEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class WarehouseRoadBoosterBlock extends Block {
    public static final MapCodec<WarehouseRoadBoosterBlock> CODEC = simpleCodec(WarehouseRoadBoosterBlock::new);
    private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 1.0);
    public static final EnumProperty<Direction> FACING;
    private static final Map<Direction, VoxelShape> SHAPES;
    private static final int FORKLIFT_BOOST_TICKS = 70;

    public MapCodec<? extends WarehouseRoadBoosterBlock> codec() {
        return CODEC;
    }

    public WarehouseRoadBoosterBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES.get(blockState.getValue(FACING));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    protected BlockState updateShape(BlockState blockState, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos blockPos, Direction direction, BlockPos neighborPos, BlockState neighbourState, RandomSource randomSource) {
        return !blockState.canSurvive(levelReader, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, neighborPos, neighbourState, randomSource);
    }

    protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return !levelReader.isEmptyBlock(blockPos.below());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity instanceof LivingEntity livingEntity && !livingEntity.isSteppingCarefully()) {
            Entity vehicle = entity.getVehicle();
            if (vehicle instanceof ForkliftEntity forkliftEntity) {
                forkliftEntity.applySpeedBoost(FORKLIFT_BOOST_TICKS);
                livingEntity.addEffect(new MobEffectInstance(ExtraEffects.FORKLIFT_BOOST, FORKLIFT_BOOST_TICKS, 1, false, false, true));
            }
        }
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        SHAPES = Shapes.rotateHorizontal(SHAPE);
    }
}
