package com.lovetropics.extras.mixin.perf;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapItemSavedData.class)
public class MapDataMixin {

	// NUKE
	@Redirect(
			method = "tickCarriedBy",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;contains(Lnet/minecraft/world/item/ItemStack;)Z")
	)
	public boolean searchForMap(Inventory playerInventory, ItemStack stack) {
		return false;
	}

	@Redirect(
			method = "tickCarriedBy",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;addDecoration(Lnet/minecraft/world/level/saveddata/maps/MapDecoration$Type;Lnet/minecraft/world/level/LevelAccessor;Ljava/lang/String;DDDLnet/minecraft/network/chat/Component;)V")
	)
	public void updateDecoration(MapItemSavedData mapData, MapDecoration.Type type, LevelAccessor worldIn, String decorationName, double worldX, double worldZ, double rotationIn, Component p_191095_10_) {
	}
}
