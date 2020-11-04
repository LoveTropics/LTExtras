package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.client.perf.Matrix4fExt;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Deque;

@Mixin(MatrixStack.class)
public class MatrixStackMixin {
	@Shadow
	@Final
	private Deque<MatrixStack.Entry> stack;

	/**
	 * @reason Remove allocation and matrix multiplication for translate operation. We can do so much simpler!
	 * @author gegy1000
	 */
	@Overwrite
	public void translate(double x, double y, double z) {
		MatrixStack.Entry entry = this.stack.getLast();
		Matrix4f matrix = entry.getMatrix();
		((Matrix4fExt) (Object) matrix).translate((float) x, (float) y, (float) z);
	}
}
