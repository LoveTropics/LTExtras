package com.lovetropics.extras.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FakeWaterBlock extends WaterBarrierBlock {

	public FakeWaterBlock(Properties properties) {
		super(properties);
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (worldIn instanceof Level level && level.isClientSide()) {
			if (context instanceof EntityCollisionContext entityContext) {
				if (entityContext.getEntity() instanceof Player player && !player.isCreative()) {
					return Shapes.empty();
				}
			}
		}
		return super.getShape(state, worldIn, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
}
