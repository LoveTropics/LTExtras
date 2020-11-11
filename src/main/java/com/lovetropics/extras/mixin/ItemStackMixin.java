package com.lovetropics.extras.mixin;

import com.google.common.collect.ImmutableList;
import com.lovetropics.extras.EverythingTag;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	private static final ImmutableList<Block> TAG_FALLBACK = ImmutableList.of(Blocks.AIR);

	@Redirect(
			method = "getPlacementTooltip",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/tags/Tag;getAllElements()Ljava/util/Collection;"
			)
	)
	private static Collection<Block> getPlacementTooltipForTag(Tag<Block> tag) {
		if (tag.getId().equals(EverythingTag.ID)) {
			return TAG_FALLBACK;
		}
		return tag.getAllElements();
	}
}
