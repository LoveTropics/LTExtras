package com.lovetropics.extras.mixin.client.perf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: 1.19 does this properly
@Deprecated
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(method = "renderChunkLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ShaderInstance;clear()V"))
	private void clearVertexBuffer(RenderType renderType, PoseStack poseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projectionMatrix, CallbackInfo ci) {
		// We need to unbind the chunk vertex array so future render code doesn't reset and break our state
		VertexBuffer.unbindVertexArray();
	}
}
