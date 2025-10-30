package com.lovetropics.extras.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class AdjustableLampBlock extends RedstoneLampBlock {
    public static final MapCodec<RedstoneLampBlock> CODEC = simpleCodec(AdjustableLampBlock::new);

    @Override
    public MapCodec<RedstoneLampBlock> codec() {
        return CODEC;
    }

    public static final IntegerProperty LIGHT_LEVEL = BlockStateProperties.LEVEL;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = (blockState) -> blockState.getValue(LIT) ? blockState.getValue(LIGHT_LEVEL) : 0;
    private static final int DEFAULT_LIGHT_LEVEL = 7;

    public AdjustableLampBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(LIT, false).setValue(LIGHT_LEVEL, DEFAULT_LIGHT_LEVEL));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final boolean lit = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return defaultBlockState().setValue(LIT, lit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT).add(LIGHT_LEVEL);
    }
}
