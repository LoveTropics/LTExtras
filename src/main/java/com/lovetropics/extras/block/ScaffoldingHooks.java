package com.lovetropics.extras.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public class ScaffoldingHooks {

	public static int getDistance(BlockGetter world, BlockPos pos) {
		BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.DOWN);
		BlockState state = world.getBlockState(mutablePos);
		int distance = 7;
		if (state.getBlock() instanceof ScaffoldingBlock) {
			distance = state.getValue(ScaffoldingBlock.DISTANCE);
		} else if (state.isFaceSturdy(world, mutablePos, Direction.UP)) {
			return 0;
		}

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState blockstate1 = world.getBlockState(mutablePos.set(pos).move(direction));
			if (blockstate1.getBlock() instanceof ScaffoldingBlock) {
				distance = Math.min(distance, blockstate1.getValue(ScaffoldingBlock.DISTANCE) + 1);
				if (distance == 1) {
					break;
				}
			}
		}

		return distance;
	}
}
