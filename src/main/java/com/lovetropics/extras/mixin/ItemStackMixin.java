package com.lovetropics.extras.mixin;

import com.lovetropics.extras.EverythingTag;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(
			method = "lambda$expandBlockState$8",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/commands/arguments/blocks/BlockStateParser$TagResult;tag()Lnet/minecraft/core/HolderSet;"
			),
			cancellable = true
	)
	private static void getPlacementTooltipForTag(BlockStateParser.TagResult result, CallbackInfoReturnable<List> cir) {
		Optional<TagKey<Block>> key = result.tag().unwrapKey();
		if (key.isPresent() && key.get().location().equals(EverythingTag.ID)) {
			cir.setReturnValue(List.of());
		}
	}
}
