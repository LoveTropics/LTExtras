package com.lovetropics.extras.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LaunchpadBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<LaunchpadBlock> CODEC = simpleCodec(LaunchpadBlock::new);
    private static final VoxelShape AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    //Power is divided by 10 since there is no FloatProperty
    public static final IntegerProperty VERTICAL_POWER = IntegerProperty.create("vertical_power", 0, 50);
    public static final IntegerProperty HORIZONTAL_POWER = IntegerProperty.create("horizontal_power", 0, 50);

    public LaunchpadBlock(final Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(VERTICAL_POWER, 10).setValue(HORIZONTAL_POWER, 10));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        final double verticalPower = (double) state.getValue(VERTICAL_POWER) / 10;
        final double horizontalPower = (double) state.getValue(HORIZONTAL_POWER) / 10;
        final Direction facing = state.getValue(FACING);

        final Vec3 upwards = new Vec3(0, verticalPower, 0);
        final Vec3 forward = new Vec3(facing.getStepX(), 0, facing.getStepZ()).scale(horizontalPower);

        entity.addDeltaMovement(forward.add(upwards));
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return AABB;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, VERTICAL_POWER, HORIZONTAL_POWER);
    }
}
