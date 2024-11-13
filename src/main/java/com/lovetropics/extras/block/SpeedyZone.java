package com.lovetropics.extras.block;

import com.lovetropics.lib.block.CustomShapeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpeedyZone extends CustomShapeBlock {

    private static final VoxelShape SMALL_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 0.5D, 16.0D);

    public SpeedyZone(Properties properties) {
        super(SMALL_SHAPE, properties.noTerrainParticles());
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entity) {
        if(entity instanceof LivingEntity livingEntity) {
            if(entity.isSteppingCarefully()) {
                return;
            }
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2, false, false));
        }
    }


}
