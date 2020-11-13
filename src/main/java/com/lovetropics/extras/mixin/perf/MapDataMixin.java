package com.lovetropics.extras.mixin.perf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;

@Mixin(MapData.class)
public class MapDataMixin {

	// NUKE
	@Overwrite
	public void updateVisiblePlayers(PlayerEntity player, ItemStack mapStack) {}
}
