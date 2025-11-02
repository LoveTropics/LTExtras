package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.item.InteractActionData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlock.class)
public class LecternBlockMixin {

    @Inject(method = "useWithoutItem", at = @At(value = "HEAD"), cancellable = true)
    public void onUseWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.getBlockEntity(pos) instanceof LecternBlockEntity lecternblockentity) {
            ItemStack book = lecternblockentity.getBook();
            InteractActionData actionData = book.get(ExtraDataComponents.INTERACT_ACTION);
            if (actionData != null) {
                if (actionData.cancelEvent()) {
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    actionData.performCommands(serverPlayer);
                }
            }
        }
    }
}
