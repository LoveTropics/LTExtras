package com.lovetropics.extras.zipline;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraLangKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Use on two poles in turn to connect or disconnect them.
 * Using it on a rope to change its slack is handled on the client since ropes are not blocks nor entities.
 */
public class ZiplineWrenchItem extends Item {
    public ZiplineWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockState state = level.getBlockState(context.getClickedPos());
        if (!(state.getBlock() instanceof ZiplinePoleBlock)) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        BlockPos pos = ZiplinePoleBlock.getBasePos(context.getClickedPos(), state);
        GlobalPos selected = stack.get(ExtraDataComponents.ZIPLINE_SELECTED_POLE);
        if (selected == null) {
            stack.set(ExtraDataComponents.ZIPLINE_SELECTED_POLE, GlobalPos.of(level.dimension(), pos));
            player.sendOverlayMessage(ExtraLangKeys.ZIPLINE_POLE_SELECTED.get());
            return InteractionResult.SUCCESS_SERVER;
        }

        stack.remove(ExtraDataComponents.ZIPLINE_SELECTED_POLE);
        player.sendOverlayMessage(toggleConnection(level, selected, pos));
        return InteractionResult.SUCCESS_SERVER;
    }

    private static Component toggleConnection(Level level, GlobalPos selected, BlockPos pos) {
        BlockPos selectedPos = selected.pos();
        if (selectedPos.equals(pos)) {
            return ExtraLangKeys.ZIPLINE_SELECTION_CLEARED.get();
        }
        if (!selected.dimension().equals(level.dimension())) {
            return ExtraLangKeys.ZIPLINE_SELECTED_POLE_MISSING.get().withStyle(ChatFormatting.RED);
        }
        // Before looking the poles up, so that a far away selected pole doesn't get its chunk loaded
        if (!ZiplineSegment.anchor(selectedPos).closerThan(ZiplineSegment.anchor(pos), ZiplineSegment.MAX_SPAN)) {
            return ExtraLangKeys.ZIPLINE_TOO_FAR.format(ZiplineSegment.MAX_SPAN).withStyle(ChatFormatting.RED);
        }
        if (!(level.getBlockEntity(selectedPos) instanceof ZiplinePoleBlockEntity from)
                || !(level.getBlockEntity(pos) instanceof ZiplinePoleBlockEntity to)) {
            return ExtraLangKeys.ZIPLINE_SELECTED_POLE_MISSING.get().withStyle(ChatFormatting.RED);
        }
        if (from.getConnection(pos) != null) {
            ZiplinePoleBlockEntity.disconnect(level, selectedPos, pos);
            return ExtraLangKeys.ZIPLINE_DISCONNECTED.get().withStyle(ChatFormatting.YELLOW);
        }
        if (from.isFull() || to.isFull()) {
            return ExtraLangKeys.ZIPLINE_POLE_FULL.get().withStyle(ChatFormatting.RED);
        }
        ZiplinePoleBlockEntity.connect(level, selectedPos, pos, ZiplineSegment.DEFAULT_SLACK);
        return ExtraLangKeys.ZIPLINE_CONNECTED.get().withStyle(ChatFormatting.GREEN);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() || !stack.has(ExtraDataComponents.ZIPLINE_SELECTED_POLE)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            stack.remove(ExtraDataComponents.ZIPLINE_SELECTED_POLE);
            player.sendOverlayMessage(ExtraLangKeys.ZIPLINE_SELECTION_CLEARED.get());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(ExtraDataComponents.ZIPLINE_SELECTED_POLE) || super.isFoil(stack);
    }
}
