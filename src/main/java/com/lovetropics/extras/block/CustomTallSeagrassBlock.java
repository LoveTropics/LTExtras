package com.lovetropics.extras.block;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CustomTallSeagrassBlock extends TallSeagrassBlock {

    private final NonNullSupplier<? extends SeagrassBlock> drop;

    public CustomTallSeagrassBlock(Properties p, NonNullSupplier<? extends SeagrassBlock> drop) {
        super(p);
        this.drop = drop;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader p_304988_, BlockPos p_154750_, BlockState p_154751_, boolean p_387662_) {
        return new ItemStack(drop.get());
    }
}
