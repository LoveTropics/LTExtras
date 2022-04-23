package com.lovetropics.extras.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockMixin {
	@Redirect(method = "getDistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;is(Lnet/minecraft/block/Block;)Z"))
	private static boolean matchesScaffolding(BlockState state, Block block) {
		return state.getBlock() instanceof ScaffoldingBlock;
	}
}
