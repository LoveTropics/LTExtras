package com.lovetropics.extras.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class FakeWaterBlock extends WaterBarrierBlock {

	public FakeWaterBlock(Properties properties) {
		super(properties);
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (worldIn instanceof World && ((World) worldIn).isRemote) {
			if (context.getEntity() instanceof PlayerEntity && !((PlayerEntity)context.getEntity()).isCreative()) {
				return VoxelShapes.empty();
			}
		}

		return super.getShape(state, worldIn, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}
}