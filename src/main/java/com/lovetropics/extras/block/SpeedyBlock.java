package com.lovetropics.extras.block;

import com.lovetropics.lib.block.CustomShapeBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.Level;

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
	public void stepOn(final Level level, final BlockPos pos, final BlockState state, final Entity entity) {
		double d0 = Math.abs(entity.getDeltaMovement().y);
		if (d0 < 0.1D && !entity.isSteppingCarefully()) {
			double d1 = 1.35D - d0 * 0.2D; // TODO config
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(d1, 1.0D, d1));
		}

		super.stepOn(level, pos, state, entity);
	}
}
