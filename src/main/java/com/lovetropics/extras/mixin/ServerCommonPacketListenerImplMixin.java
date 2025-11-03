package com.lovetropics.extras.mixin;

import com.lovetropics.extras.click_events.ExtraClickEvents;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {

    @Shadow
    @Final
    protected Connection connection;

    @Inject(method = "handleCustomClickAction", at = @At("HEAD"))
    private void onHandle(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
        ServerCommonPacketListenerImpl thisObj = (ServerCommonPacketListenerImpl) (Object) this;
        if (thisObj instanceof ServerGamePacketListenerImpl serverGamePacketListener) {
            ServerPlayer player = serverGamePacketListener.player;
            ExtraClickEvents.handleCustomClickAction(player, packet.id(), packet.payload());
        }
    }
}
