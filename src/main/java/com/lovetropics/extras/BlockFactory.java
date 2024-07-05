package com.lovetropics.extras;

import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface BlockFactory<T extends Block> extends NonNullFunction<BlockBehaviour.Properties, T> {
	@Override
	T apply(BlockBehaviour.Properties properties);
}
