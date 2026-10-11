package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.entity.ExtraEntities;
import com.lovetropics.extras.zipline.ZiplineEntity;
import com.lovetropics.extras.zipline.ZiplinePath;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ZiplineAnchorBlock extends Block {
    public static final MapCodec<ZiplineAnchorBlock> CODEC = simpleCodec(ZiplineAnchorBlock::new);
    private static final VoxelShape SHAPE = Block.column(8.0, 0.0, 16.0);

    public ZiplineAnchorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ZiplineAnchorBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.STRING)) {
            if (player instanceof ServerPlayer serverPlayer) {
                link(stack, level, pos, serverPlayer);
            }
            return InteractionResult.SUCCESS;
        }
        return useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer serverPlayer) {
            ZiplineEntity cable = ZiplineEntity.atAnchor(level, pos);
            if (cable != null) {
                cable.boardFromAnchor(serverPlayer, pos);
            } else {
                serverPlayer.sendSystemMessage(ExtraLangKeys.ZIPLINE_UNLINKED.get(), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void link(ItemStack string, Level level, BlockPos pos, ServerPlayer player) {
        if (!player.mayBuild() || !level.mayInteract(player, pos)) {
            return;
        }
        GlobalPos selected = string.get(ExtraDataComponents.ZIPLINE_ANCHOR);
        if (selected == null) {
            if (ZiplineEntity.atAnchor(level, pos) != null) {
                player.sendSystemMessage(ExtraLangKeys.ZIPLINE_ALREADY_LINKED.get(), true);
                return;
            }
            string.set(ExtraDataComponents.ZIPLINE_ANCHOR, GlobalPos.of(level.dimension(), pos));
            player.sendSystemMessage(ExtraLangKeys.ZIPLINE_SELECTED.get(), true);
            return;
        }
        // Clicking the first anchor again cancels; a dimension change or broken selection resets it.
        string.remove(ExtraDataComponents.ZIPLINE_ANCHOR);
        if (!selected.dimension().equals(level.dimension()) || !level.hasChunkAt(selected.pos())
                || !level.getBlockState(selected.pos()).is(this) || selected.pos().equals(pos)) {
            player.sendSystemMessage(ExtraLangKeys.ZIPLINE_SELECTION_CLEARED.get(), true);
            return;
        }
        if (!level.mayInteract(player, selected.pos())) {
            return;
        }
        ZiplinePath path = new ZiplinePath(ZiplineEntity.attachment(selected.pos()), ZiplineEntity.attachment(pos));
        if (!path.isValid()) {
            player.sendSystemMessage(ExtraLangKeys.ZIPLINE_INVALID_SPAN.get(), true);
            return;
        }
        if (ZiplineEntity.atAnchor(level, selected.pos()) != null || ZiplineEntity.atAnchor(level, pos) != null) {
            player.sendSystemMessage(ExtraLangKeys.ZIPLINE_ALREADY_LINKED.get(), true);
            return;
        }
        // Creating a connection requires a loaded route; boarding later supplies temporary tickets.
        for (int step = 0; step <= (int) Math.ceil(path.length()); step++) {
            if (!level.hasChunkAt(BlockPos.containing(path.point(step / Math.ceil(path.length()))))) {
                player.sendSystemMessage(ExtraLangKeys.ZIPLINE_UNAVAILABLE.get(), true);
                return;
            }
        }
        ZiplineEntity cable = new ZiplineEntity(ExtraEntities.ZIPLINE.get(), level);
        cable.setAnchors(selected.pos(), pos);
        if (level.addFreshEntity(cable)) {
            string.consume(1, player);
            player.sendSystemMessage(ExtraLangKeys.ZIPLINE_LINKED.get(), true);
        }
    }
}
