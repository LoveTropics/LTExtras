package com.lovetropics.extras;

import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public interface BlockFactory<T extends Block> extends NonNullFunction<AbstractBlock.Properties, T> {
    @Override
    T apply(AbstractBlock.Properties properties);
}
