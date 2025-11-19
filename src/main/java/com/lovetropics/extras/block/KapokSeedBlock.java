package com.lovetropics.extras.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class KapokSeedBlock extends Block {
    public static final MapCodec<KapokSeedBlock> CODEC = simpleCodec(KapokSeedBlock::new);
    private static final VoxelShape SHAPE = Block.box(4.5f, 1.0f, 4.5f, 11.5f, 14.0f, 11.5f);

    public KapokSeedBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<KapokSeedBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return direction == Direction.UP && !canAttachTo(level, neighborState, neighborPos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos attachPos = pos.above();
        return canAttachTo(level, level.getBlockState(attachPos), attachPos);
    }

    private static boolean canAttachTo(LevelReader level, BlockState state, BlockPos pos) {
        return state.isFaceSturdy(level, pos, Direction.DOWN) || state.is(BlockTags.LEAVES);
    }
}
