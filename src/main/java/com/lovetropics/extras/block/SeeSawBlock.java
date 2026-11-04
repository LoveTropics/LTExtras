package com.lovetropics.extras.block;

import com.lovetropics.extras.entity.BlockFormation;
import com.lovetropics.extras.entity.SeeSawEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class SeeSawBlock extends DirectionalBlock {
    public static final MapCodec<SeeSawBlock> CODEC = simpleCodec(SeeSawBlock::new);
    public SeeSawBlock(Properties properties) {
        super(properties);
        this.defaultBlockState().setValue(FACING, Direction.NORTH);
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild() || player.isShiftKeyDown())
            return InteractionResult.PASS;
        if (itemStack.isEmpty() && !level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            BlockFormation blockFormation = new BlockFormation();
            try {
                blockFormation.form(level, pos);
                blockFormation.removeBlocksFromLevel(level);
                SeeSawEntity seeSawEntity = SeeSawEntity.create(level, blockFormation);
                seeSawEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
                level.addFreshEntity(seeSawEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }
}
