package com.lovetropics.extras.mixin;

import com.google.common.collect.ImmutableList;
import com.lovetropics.extras.EverythingTag;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	private static final ImmutableList<Holder<Block>> EMPTY_TAG = ImmutableList.of(Blocks.AIR.builtInRegistryHolder());

	@Redirect(
			method = "expandBlockState",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/core/DefaultedRegistry;getTagOrEmpty(Lnet/minecraft/tags/TagKey;)Ljava/lang/Iterable;"
			)
	)
	private static Iterable<Holder<Block>> getPlacementTooltipForTag(DefaultedRegistry<Block> registry, TagKey<Block> tag) {
		if (tag.location().equals(EverythingTag.ID)) {
			return EMPTY_TAG;
		}
		return registry.getTagOrEmpty(tag);
	}
}
