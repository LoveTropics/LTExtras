package com.lovetropics.extras.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.lovetropics.extras.network.message.ClientboundSetAutoRejoinIntent;
import com.lovetropics.extras.rejoiner.AutoRejoinIntent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

/**
 * Don't make players auto rejoin when they are kicked from the server.
 * So they are forced to read the kick message
* */
@Mixin(KickCommand.class)
public class KickCommandMixin {

    @Inject(method = "kickPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private static void onPlayerKick(CommandSourceStack source, Collection<ServerPlayer> players, Component reason, CallbackInfoReturnable<Integer> cir, @Local(name = "player") ServerPlayer player) {
        player.connection.send(new ClientboundSetAutoRejoinIntent(AutoRejoinIntent.DISABLE));
    }
}
