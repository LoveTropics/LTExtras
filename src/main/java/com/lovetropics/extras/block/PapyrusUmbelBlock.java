package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PapyrusUmbelBlock extends Block {

    public static final EnumProperty<PapyrusStemBlock.Type> TYPE = PapyrusStemBlock.TYPE;
    public static final BooleanProperty AGEING = BooleanProperty.create("ageing");
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public PapyrusUmbelBlock(final Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TYPE, PapyrusStemBlock.Type.PLAIN).setValue(AGEING, true));
    }

    @Override
    protected boolean isRandomlyTicking(final BlockState state) {
        return state.getValue(AGEING);
    }

    @Override
    protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
        //10% chance to stop ageing
        if (random.nextInt(10) == 0) {
            level.setBlockAndUpdate(pos, state.setValue(AGEING, false));
        }

        //10% chance to age
        if (state.getValue(AGEING) && random.nextInt(10) == 0) {
            PapyrusStemBlock.Type type = state.getValue(TYPE);
            if (type == PapyrusStemBlock.Type.PLAIN) {
                ageSelfAndStem(PapyrusStemBlock.Type.DRY, level, pos);
            } else if (type == PapyrusStemBlock.Type.DRY) {
                ageSelfAndStem(PapyrusStemBlock.Type.DEAD, level, pos);
            }
        }
    }

    private void ageSelfAndStem(PapyrusStemBlock.Type type, ServerLevel level, BlockPos pos) {
        level.setBlockAndUpdate(pos, defaultBlockState().setValue(TYPE, type));

        BlockPos currentPos = pos;
        for (int i = 0; i < PapyrusStemBlock.MAX_HEIGHT; i++) {
            BlockPos belowPos = currentPos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(ExtraBlocks.PAPYRUS_STEM.get())) {
                level.setBlockAndUpdate(belowPos, belowState.setValue(PapyrusStemBlock.TYPE, type));
            } else {
                break;
            }
            currentPos = belowPos;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos().below());
        if (blockstate.is(ExtraBlocks.PAPYRUS_STEM.get())) {
            return defaultBlockState().setValue(TYPE, blockstate.getValue(PapyrusStemBlock.TYPE));
        }

        return defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, AGEING);
    }
}
