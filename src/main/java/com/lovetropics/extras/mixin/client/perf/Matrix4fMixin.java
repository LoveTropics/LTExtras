package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.client.perf.Matrix4fExt;
import net.minecraft.client.renderer.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public class Matrix4fMixin implements Matrix4fExt {
	@Shadow
	protected float m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33;

	@Override
	public void translate(float x, float y, float z) {
		this.m03 += this.m00 * x + this.m01 * y + this.m02 * z;
		this.m13 += this.m10 * x + this.m11 * y + this.m12 * z;
		this.m23 += this.m20 * x + this.m21 * y + this.m22 * z;
		this.m33 += this.m30 * x + this.m31 * y + this.m32 * z;
	}
}
