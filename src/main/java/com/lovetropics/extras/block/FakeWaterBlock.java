package com.lovetropics.extras.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Block.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FakeWaterBlock extends WaterBarrierBlock {

	public FakeWaterBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}

	@Override
	@Deprecated
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		if (worldIn instanceof World && ((World)worldIn).isRemote) {
			return getClientRaytraceShape(state, worldIn, pos);
		} else {
			return super.getRaytraceShape(state, worldIn, pos);
		}
	}

	@OnlyIn(Dist.CLIENT)
	private VoxelShape getClientRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.isCreative()) {
			return super.getRaytraceShape(state, worldIn, pos);
		} else {
			return VoxelShapes.empty();
		}
	}
}