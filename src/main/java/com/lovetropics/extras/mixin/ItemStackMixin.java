package com.lovetropics.extras.mixin;

import com.google.common.collect.ImmutableList;
import com.lovetropics.extras.EverythingTag;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	private static final ImmutableList<Block> TAG_FALLBACK = ImmutableList.of(Blocks.AIR);

	@Redirect(
			method = "getPlacementTooltip",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/tags/ITag;getAllElements()Ljava/util/List;"
			)
	)
	private static List<Block> getPlacementTooltipForTag(ITag<Block> tag) {
		if (tag instanceof ITag.INamedTag) {
			ResourceLocation name = ((ITag.INamedTag<Block>) tag).getName();
			if (name.equals(EverythingTag.ID)) {
				return TAG_FALLBACK;
			}
		}
		return tag.getAllElements();
	}
}
