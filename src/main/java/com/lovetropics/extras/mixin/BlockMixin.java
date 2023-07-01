package com.lovetropics.extras.mixin;

import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "isExceptionForConnection", at = @At("HEAD"), cancellable = true)
    private static void isExceptionsForConnection(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof BarrierBlock) {
            cir.setReturnValue(true);
        }
    }
}
