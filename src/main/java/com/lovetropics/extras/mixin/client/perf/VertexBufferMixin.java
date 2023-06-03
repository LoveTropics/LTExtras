package com.lovetropics.extras.mixin.client.perf;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: 1.19 does this properly
@Deprecated
@Mixin(VertexBuffer.class)
public abstract class VertexBufferMixin {
//	@Shadow
//	private int indexCount;
//	@Shadow
//	private VertexFormat.Mode mode;
//	@Shadow
//	private VertexFormat.IndexType indexType;
//	@Shadow
//	private VertexFormat format;
//
//	@Shadow
//	protected abstract void bindVertexArray();
//
//	@Redirect(method = "upload_", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;unbind()V"))
//	private void dontUnbindAfterUpload() {
//	}
//
//	@Inject(method = "upload_", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;bind()V", shift = At.Shift.AFTER))
//	private void setupFormatStateAfterUpload(BufferBuilder builder, CallbackInfo ci) {
//		format.setupBufferState();
//	}
//
//	@Overwrite
//	public void drawChunkLayer() {
//		// We don't need to rebind
//		if (indexCount != 0) {
//			bindVertexArray();
//			RenderSystem.drawElements(mode.asGLMode, indexCount, indexType.asGLType);
//		}
//	}
}
