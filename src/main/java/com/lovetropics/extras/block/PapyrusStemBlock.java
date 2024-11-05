package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Set;

public final class PapyrusStemBlock extends Block implements BonemealableBlock {


    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    public static final BooleanProperty GROWING = BooleanProperty.create("growing");
    private static final Set<Block> GROWS_ON = Set.of(Blocks.GRASS_BLOCK, Blocks.DIRT);
    private static final VoxelShape SHAPE = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    protected static final int MAX_HEIGHT = 4;
    private static final int MIN_HEIGHT_FOR_FLOWERS = 2;

    public PapyrusStemBlock(final Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TYPE, Type.PLAIN));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState();
    }

    @Override
    protected boolean isRandomlyTicking(final BlockState state) {
        //TODO can stop ticking if there is a flower on top?
        return true;
    }

    @Override
    protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {

        //if block below is stem, and above is air, grow umbel on top
        final BlockState stateBelow = level.getBlockState(pos.below());
        final BlockState stateAbove = level.getBlockState(pos.above());

        boolean aboveIsStem = stateAbove.is(this);
        boolean aboveIsUmbel = stateAbove.is(ExtraBlocks.PAPYRUS_UMBEL.get());
        boolean aboveIsAir = level.isEmptyBlock(pos.above());
        boolean hasReachedMaxHeight = hasStemHeight(level, pos, MAX_HEIGHT);
        boolean enoughHeightForFlower = hasStemHeight(level, pos, MIN_HEIGHT_FOR_FLOWERS);

        //if above is stem, do nothing
        if (aboveIsStem || aboveIsUmbel) {
            return;
        }

        //if not reached max height and above is air, 50% to grow taller
        if (!hasReachedMaxHeight && aboveIsAir && random.nextBoolean()) {
            level.setBlockAndUpdate(pos.above(), defaultBlockState().setValue(TYPE, state.getValue(TYPE)));
            return;
        }

        //if height between 2 and 4, 50% to grow flower
        if (enoughHeightForFlower && random.nextBoolean()) {
            level.setBlockAndUpdate(pos.above(), ExtraBlocks.PAPYRUS_UMBEL.get().defaultBlockState().setValue(PapyrusUmbelBlock.TYPE, stateBelow.getValue(TYPE)));
            return;
        }

        //some chance to stop growing? only if has flower?
    }

    private boolean hasStemHeight(final ServerLevel level, final BlockPos pos, final int height) {
        for (int i = 1; i < height; i++) {
            if (!level.getBlockState(pos.below(i)).is(this)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isValidBonemealTarget(final LevelReader levelReader, final BlockPos blockPos, final BlockState blockState) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(final Level level, final RandomSource randomSource, final BlockPos blockPos, final BlockState blockState) {
        return false;
    }

    @Override
    public void performBonemeal(final ServerLevel serverLevel, final RandomSource randomSource, final BlockPos blockPos, final BlockState blockState) {

    }

    @Override
    protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
        final BlockState stateBelow = level.getBlockState(pos.below());


        return canGrowOn(stateBelow);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    private boolean canGrowOn(BlockState state) {
        return GROWS_ON.contains(state.getBlock()) || state.is(ExtraBlocks.PAPYRUS_STEM);
    }

    public enum Type implements StringRepresentable {
        PLAIN("plain"),
        DRY("dry"),
        DEAD("dead"),
        ;

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
