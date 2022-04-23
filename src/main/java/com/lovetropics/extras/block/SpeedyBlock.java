package com.lovetropics.extras.block;

import com.lovetropics.lib.block.CustomShapeBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public class SpeedyBlock extends CustomShapeBlock {
	
	public static SpeedyBlock opaque(Block.Properties properties) {
		return new SpeedyBlock(VoxelShapes.block(), properties, false);
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
	public void stepOn(World worldIn, BlockPos pos, Entity entityIn) {
		double d0 = Math.abs(entityIn.getDeltaMovement().y);
		if (d0 < 0.1D && !entityIn.isSteppingCarefully()) {
			double d1 = 1.35D - d0 * 0.2D; // TODO config
			entityIn.setDeltaMovement(entityIn.getDeltaMovement().multiply(d1, 1.0D, d1));
		}

		super.stepOn(worldIn, pos, entityIn);
	}

}
