package com.lovetropics.extras.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class ScaffoldingHooks {

	public static int func_220117_a(IBlockReader p_220117_0_, BlockPos p_220117_1_) {
		BlockPos.Mutable blockpos$mutable = (new BlockPos.Mutable(p_220117_1_)).move(Direction.DOWN);
		BlockState blockstate = p_220117_0_.getBlockState(blockpos$mutable);
		int i = 7;
		if (blockstate.getBlock() instanceof ScaffoldingBlock) {
			i = blockstate.get(ScaffoldingBlock.field_220118_a);
		} else if (blockstate.isSolidSide(p_220117_0_, blockpos$mutable, Direction.UP)) {
			return 0;
		}

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState blockstate1 = p_220117_0_.getBlockState(blockpos$mutable.setPos(p_220117_1_).move(direction));
			if (blockstate1.getBlock() instanceof ScaffoldingBlock) {
				i = Math.min(i, blockstate1.get(ScaffoldingBlock.field_220118_a) + 1);
				if (i == 1) {
					break;
				}
			}
		}

		return i;
	}
}
