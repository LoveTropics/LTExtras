package com.lovetropics.extras.mixin;

import com.google.common.collect.ImmutableList;
import com.lovetropics.extras.EverythingTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	private static final ImmutableList<Block> TAG_FALLBACK = ImmutableList.of(Blocks.AIR);

	@Redirect(
			method = "expandBlockState",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/tags/ITag;getValues()Ljava/util/List;"
			)
	)
	private static List<Block> getPlacementTooltipForTag(Tag<Block> tag) {
		if (tag instanceof Tag.Named) {
			ResourceLocation name = ((Tag.Named<Block>) tag).getName();
			if (name.equals(EverythingTag.ID)) {
				return TAG_FALLBACK;
			}
		}
		return tag.getValues();
	}
}
