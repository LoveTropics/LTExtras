package com.lovetropics.extras.mixin;

import com.lovetropics.extras.duck.ServicesHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ServicesHolder {
    @Mutable
    @Shadow
    @Final
    protected Services services;

    @Override
    public void lt$setServices(Services services) {
        this.services = services;
    }
}
