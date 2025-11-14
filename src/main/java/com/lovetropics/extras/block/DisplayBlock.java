package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.block.entity.DisplayBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DisplayBlock extends BaseEntityBlock {

    private static final MapCodec<DisplayBlock> CODEC = simpleCodec(DisplayBlock::new);

    public DisplayBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayBlockEntity(ExtraBlocks.DISPLAY_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel) {
            ItemStack heldStack = player.getItemInHand(hand);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DisplayBlockEntity displayBlockEntity) {
                ItemStack currentStack = displayBlockEntity.getItemStack();
                if (currentStack.isEmpty()) {
                    if (!heldStack.isEmpty() && displayBlockEntity.getFilter().test(heldStack)) {
                        ItemStack stackToInsert = heldStack.copy();
                        stackToInsert.setCount(1);
                        displayBlockEntity.setItemStack(stackToInsert);
                        heldStack.shrink(1);
                        return InteractionResult.CONSUME;
                    }
                } else {
                    player.getInventory().add(currentStack);
                    displayBlockEntity.setItemStack(ItemStack.EMPTY);
                    displayBlockEntity.setConversionProgress(0);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ExtraBlocks.DISPLAY_BLOCK_ENTITY.get(), DisplayBlockEntity::serverTick);
    }
}
