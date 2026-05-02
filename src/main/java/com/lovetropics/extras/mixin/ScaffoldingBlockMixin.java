package com.lovetropics.extras.mixin;

import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockMixin {
    @Redirect(method = "getDistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean matchesScaffolding(BlockState state, Object o) {
        return state.getBlock() instanceof ScaffoldingBlock;
    }
}
