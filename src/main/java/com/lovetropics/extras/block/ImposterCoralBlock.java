package com.lovetropics.extras.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;

public class ImposterCoralBlock extends BaseCoralPlantTypeBlock {
    public static final MapCodec<ImposterCoralBlock> CODEC = simpleCodec(ImposterCoralBlock::new);

    public ImposterCoralBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends ImposterCoralBlock> codec() {
        return CODEC;
    }
}
