package com.lovetropics.extras.item;

import com.lovetropics.extras.entity.CleaningItemFrame;
import com.lovetropics.extras.entity.ExtraEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemFrameItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class CleaningItemFrameItem extends ItemFrameItem {

    public CleaningItemFrameItem(Properties properties) {
        super(ExtraEntities.CLEANING_ITEM_FRAME.get(), properties);
    }

    // Mostly a copy of ItemFrameItem.useOn to support CleaningItemFrame
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos blockpos1 = blockpos.relative(direction);
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
            return InteractionResult.FAIL;
        } else {
            Level level = context.getLevel();

            HangingEntity hangingentity = CleaningItemFrame.create(level, blockpos1, direction);
            EntityType.<HangingEntity>createDefaultStackConfig(level, itemstack, player).apply(hangingentity);
            if (hangingentity.survives()) {
                if (!level.isClientSide()) {
                    hangingentity.playPlacementSound();
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, hangingentity.position());
                    level.addFreshEntity(hangingentity);
                }
                itemstack.shrink(1);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        }
    }
}
