package com.lovetropics.extras.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(BlockBehaviour.Properties.class)
public interface BlockPropertiesMixin {

	@Accessor
	void setIsRandomlyTicking(boolean ticksRandomly);
}
