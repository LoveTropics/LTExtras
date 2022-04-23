package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.client.perf.Matrix4fExt;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Deque;

@Mixin(PoseStack.class)
public class MatrixStackMixin {
	@Shadow
	@Final
	private Deque<PoseStack.Pose> poseStack;

	/**
	 * @reason Remove allocation and matrix multiplication for translate operation. We can do so much simpler!
	 * TODO: remove in 1.17: integrated into vanilla
	 * @author gegy1000
	 */
	@Overwrite
	public void translate(double x, double y, double z) {
		PoseStack.Pose entry = this.poseStack.getLast();
		Matrix4f matrix = entry.pose();
		((Matrix4fExt) (Object) matrix).translate((float) x, (float) y, (float) z);
	}
}
