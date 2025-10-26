package com.lovetropics.extras.mixin;

import com.lovetropics.extras.block.entity.ForkliftEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Redirect(method="handleMoveVehicle", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"))
    public Entity replaceRootVehicle(ServerPlayer instance) {
        if (instance.getVehicle() instanceof ForkliftEntity) {
            return instance.getVehicle();
        }
        return instance.getRootVehicle();
    }

    @Redirect(method="tick", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"))
    public Entity handleTick(ServerPlayer instance) {
        if (instance.getVehicle() instanceof ForkliftEntity) {
            return instance.getVehicle();
        }
        return instance.getRootVehicle();
    }
}
