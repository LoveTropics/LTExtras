package com.lovetropics.extras.mixin.perf;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapData.class)
public class MapDataMixin {

	// NUKE
	@Redirect(
			method = "tickCarriedBy",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;contains(Lnet/minecraft/item/ItemStack;)Z")
	)
	public boolean searchForMap(PlayerInventory playerInventory, ItemStack stack) {
		return false;
	}

	@Redirect(
			method = "tickCarriedBy",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/MapData;addDecoration(Lnet/minecraft/world/storage/MapDecoration$Type;Lnet/minecraft/world/IWorld;Ljava/lang/String;DDDLnet/minecraft/util/text/ITextComponent;)V")
	)
	public void updateDecoration(MapData mapData, MapDecoration.Type type, IWorld worldIn, String decorationName, double worldX, double worldZ, double rotationIn, ITextComponent p_191095_10_) {
	}
}
