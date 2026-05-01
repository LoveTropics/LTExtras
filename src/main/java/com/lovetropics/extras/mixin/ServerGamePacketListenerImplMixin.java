package com.lovetropics.extras.mixin;

import com.lovetropics.extras.entity.ForkliftEntity;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {

    @Shadow
    public ServerPlayer player;

    @Unique
    private List<Entity> lastSentPassengers = Collections.emptyList();

    public ServerGamePacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
        super(server, connection, cookie);
    }

    @Redirect(method="handleMoveVehicle", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"))
    public Entity replaceRootVehicle(ServerPlayer instance) {
        if (instance.getVehicle() instanceof ForkliftEntity) {
            return instance.getVehicle();
        }
        return instance.getRootVehicle();
    }

    @Redirect(method="tickPlayer", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"))
    public Entity handleTick(ServerPlayer instance) {
        if (instance.getVehicle() instanceof ForkliftEntity) {
            return instance.getVehicle();
        }
        return instance.getRootVehicle();
    }

    // Required for allowing /ride to work with players
    @Inject(method = "tick", at = @At("TAIL"))
    private void sendPassengers(CallbackInfo ci) {
        List<Entity> list = this.player.getPassengers();
        if (!list.equals(this.lastSentPassengers)) {
            this.send(new ClientboundSetPassengersPacket(this.player));
            this.lastSentPassengers = list;
        }
    }
}
