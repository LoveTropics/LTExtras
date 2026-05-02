package com.lovetropics.extras.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.client.ClientPlayerSensorEffects;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ProjectionMatrixBuffer;getBuffer(Lorg/joml/Matrix4f;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"))
    private GpuBufferSlice captureProjectionMatrix(ProjectionMatrixBuffer buffer, Matrix4f projectionMatrix, Operation<GpuBufferSlice> original) {
        ClientPlayerSensorEffects.captureProjectionMatrix(projectionMatrix);
        return original.call(buffer, projectionMatrix);
    }
}
