package com.lovetropics.extras.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class ConveyorBeltBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<ConveyorBeltBlock> CODEC = simpleCodec(ConveyorBeltBlock::new);


    private static final float SPEED = 0.1f;
    private static final float CENTERING_FACTOR = 0.2f;
    private static final float THRESHOLD = 0.02f;

    public ConveyorBeltBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        moveItem(entity, state.getValue(FACING), pos);
        super.stepOn(level, pos, state, entity);
    }

    public static void moveItem(Entity entity, Direction direction, BlockPos pos) {
        if (!(entity instanceof ItemEntity item)) {
            return;
        }

        double dx = pos.getCenter().x() - item.getX();
        double dz = pos.getCenter().z() - item.getZ();

        double stepX = direction.getStepX();
        double stepZ = direction.getStepZ();
        boolean alongX = stepX != 0;

        double forwardX = stepX * SPEED;
        double forwardZ = stepZ * SPEED;
        double halfForwardX = forwardX * 0.5;
        double halfForwardZ = forwardZ * 0.5;

        boolean offCenter = Math.abs(alongX ? dz : dx) > THRESHOLD;
        double centerAdjust = (alongX ? dz : dx) * CENTERING_FACTOR;

        double motionX = alongX ? (offCenter ? halfForwardX : forwardX) : (offCenter ? centerAdjust : forwardX);
        double motionZ = alongX ? (offCenter ? centerAdjust : forwardZ) : (offCenter ? halfForwardZ : forwardZ);

        entity.setDeltaMovement(motionX, 0, motionZ);
    }
}
