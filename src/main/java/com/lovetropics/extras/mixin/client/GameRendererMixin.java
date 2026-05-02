package com.lovetropics.extras.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ProjectionMatrixBuffer;getBuffer(Lnet/minecraft/client/renderer/Projection;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"))
    private GpuBufferSlice captureProjectionMatrix(ProjectionMatrixBuffer instance, Projection projection, Operation<GpuBufferSlice> original) {
//        ClientPlayerSensorEffects.captureProjectionMatrix(projection.getMatrix(instance.)); // Todo 26.1 Port
        return original.call(instance, projection);
    }
}
