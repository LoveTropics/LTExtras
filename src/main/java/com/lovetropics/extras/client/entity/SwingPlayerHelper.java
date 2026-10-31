package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.entity.SwingEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;

public class SwingPlayerHelper {

	private static final ContextKey<SwingPose> KEY_SWING = new ContextKey<>(LTExtras.id("swing"));

	public static <T extends LivingEntity, S extends LivingEntityRenderState> void updateLivingEntityRenderState(T entity, S renderState) {
		if (entity.getVehicle() instanceof SwingEntity swingEntity) {
			float partialTicks = renderState.partialTick;
			renderState.setRenderData(KEY_SWING, new SwingPose(
					swingEntity.getAngleDegrees(partialTicks)
			));
		} else {
			renderState.setRenderData(KEY_SWING, null);
		}
	}

	public static void apply(LivingEntityRenderState renderState, EntityModel<?> model) {
		if (!(renderState instanceof HumanoidRenderState humanoidRenderState) || !(model instanceof HumanoidModel<?> humanoidModel)) {
			return;
		}
		SwingPose pose = humanoidRenderState.getRenderData(KEY_SWING);
		if (pose == null) {
			return;
		}

		ModelPart root = humanoidModel.root();
        root.xRot += -pose.rotationAngle;
        root.y += 12f * (1 - Mth.cos(-pose.rotationAngle));
        root.z -= 12f * Mth.sin(-pose.rotationAngle);
	}


	private record SwingPose(
			float rotationAngle
	) {
	}
}
