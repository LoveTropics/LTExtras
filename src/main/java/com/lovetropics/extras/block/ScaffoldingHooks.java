package com.lovetropics.extras.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class ScaffoldingHooks {

	public static int getDistance(IBlockReader world, BlockPos pos) {
		BlockPos.Mutable mutablePos = pos.mutable().move(Direction.DOWN);
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
