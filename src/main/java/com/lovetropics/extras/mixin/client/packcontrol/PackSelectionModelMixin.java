package com.lovetropics.extras.mixin.client.packcontrol;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.client.ClientPackControl;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(PackSelectionModel.class)
public class PackSelectionModelMixin {
	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;getAvailablePacks()Ljava/util/Collection;"))
	private Collection<Pack> initGetAvailablePacks(PackRepository repository, Operation<Collection<Pack>> original) {
		return ClientPackControl.removeHidden(original.call(repository), repository);
	}

	@WrapOperation(method = "findNewPacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;getAvailablePacks()Ljava/util/Collection;"))
	private Collection<Pack> findNewPacksGetAvailablePacks(PackRepository repository, Operation<Collection<Pack>> original) {
		return ClientPackControl.removeHidden(original.call(repository), repository);
	}
}
