package com.lovetropics.extras.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.AbstractBlock;

@Mixin(AbstractBlock.Properties.class)
public interface BlockPropertiesMixin {

	@Accessor
	void setTicksRandomly(boolean ticksRandomly);
}