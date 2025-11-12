package com.lovetropics.extras.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TransparentLeavesBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final MapCodec<TransparentLeavesBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraCodecs.floatRange(0.0f, 1.0f).fieldOf("leaf_particle_chance").forGetter(b -> b.leafParticleChance),
            ParticleTypes.CODEC.fieldOf("leaf_particle").forGetter(b -> b.leafParticle),
            propertiesCodec()
    ).apply(i, TransparentLeavesBlock::new));

    private static final ColorParticleOption TROPICS_LEAF_PARTICLE = ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, 0xff199d10);

    private final float leafParticleChance;
    private final ParticleOptions leafParticle;

    public TransparentLeavesBlock(float leafParticleChance, ParticleOptions leafParticle, BlockBehaviour.Properties properties) {
        super(properties);
        this.leafParticleChance = leafParticleChance;
        this.leafParticle = leafParticle;
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
    }

    public static TransparentLeavesBlock tropicsLeaves(Properties properties) {
        return new TransparentLeavesBlock(0.01f, TROPICS_LEAF_PARTICLE, properties);
    }

    @Override
    public MapCodec<TransparentLeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction neighborDirection, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        BlockPos belowPos = pos.below();
        BlockState blockBelow = level.getBlockState(belowPos);
        makeDrippingWaterParticles(level, pos, random, blockBelow, belowPos);
        makeFallingLeavesParticles(level, pos, random, blockBelow, belowPos);
    }

    private static void makeDrippingWaterParticles(Level level, BlockPos pos, RandomSource random, BlockState blockBelow, BlockPos belowPos) {
        if (level.isRainingAt(pos.above()) && random.nextInt(15) == 1) {
            if (!blockBelow.canOcclude() || !blockBelow.isFaceSturdy(level, belowPos, Direction.UP)) {
                ParticleUtils.spawnParticleBelow(level, pos, random, ParticleTypes.DRIPPING_WATER);
            }
        }
    }

    private void makeFallingLeavesParticles(Level level, BlockPos pos, RandomSource random, BlockState blockBelow, BlockPos belowPos) {
        if (random.nextFloat() >= leafParticleChance) {
            return;
        }
        if (!isFaceFull(blockBelow.getCollisionShape(level, belowPos), Direction.UP)) {
            ParticleUtils.spawnParticleBelow(level, pos, random, leafParticle);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }
}
