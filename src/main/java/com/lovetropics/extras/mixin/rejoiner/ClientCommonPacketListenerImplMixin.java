package com.lovetropics.extras.mixin.rejoiner;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.client.ClientAutoRejoinHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.DisconnectionDetails;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {
	@Shadow
	@Final
	@Nullable
	protected ServerData serverData;

	@WrapOperation(method = "onDisconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientCommonPacketListenerImpl;createDisconnectScreen(Lnet/minecraft/network/DisconnectionDetails;)Lnet/minecraft/client/gui/screens/Screen;"))
	private Screen onDisconnect(ClientCommonPacketListenerImpl instance, DisconnectionDetails details, Operation<Screen> original) {
		if (serverData != null) {
			Screen newScreen = ClientAutoRejoinHandler.createConnectionClosedScreen(details, serverData);
			if (newScreen != null) {
				return newScreen;
			}
		}
		return original.call(instance, details);
	}
}
