package com.lovetropics.extras.block;

import com.lovetropics.lib.block.CustomShapeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpeedyBlock extends CustomShapeBlock {
    public static SpeedyBlock opaque(Block.Properties properties) {
        return new SpeedyBlock(Shapes.block(), properties, false);
    }

    public static SpeedyBlock transparent(VoxelShape shape, Block.Properties properties) {
        return new SpeedyBlock(shape, properties, true);
    }

    private final boolean transparent;

    public SpeedyBlock(VoxelShape shape, Block.Properties properties, boolean transparent) {
        super(shape, properties);
        this.transparent = transparent;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return transparent;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        applySpeedy(entity);
        super.stepOn(level, pos, state, entity);
    }

    public static void applySpeedy(Entity entity) {
        double movementY = Math.abs(entity.getDeltaMovement().y);
        if (movementY < 0.1 && !entity.isSteppingCarefully()) {
            double factor = 1.35 - movementY * 0.2; // TODO config
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(factor, 1.0, factor));
        }
    }
}
