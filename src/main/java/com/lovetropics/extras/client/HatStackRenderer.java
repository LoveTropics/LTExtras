package com.lovetropics.extras.client;

import com.google.common.reflect.TypeToken;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.lib.item.StackedHat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class HatStackRenderer {
    private static final ContextKey<StackRenderState> HAT_STACK = new ContextKey<>(LTExtras.id("hat_stack"));

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        ItemModelResolver itemModelResolver = Minecraft.getInstance().getItemModelResolver();
        event.registerEntityModifier(new TypeToken<LivingEntityRenderer<?, ?, ?>>() {
        }, (entity, state) -> extract(entity, state, itemModelResolver));
    }

    private static void extract(LivingEntity entity, LivingEntityRenderState state, ItemModelResolver itemModelResolver) {
        ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
        List<StackedHat> hats = headItem.getOrDefault(ExtraDataComponents.HAT_STACK, List.of());
        if (hats.isEmpty()) {
            return;
        }
        List<HatRenderState> itemStates = new ArrayList<>(hats.size());
        for (StackedHat hat : hats) {
            ItemStackRenderState itemState = new ItemStackRenderState();
            itemModelResolver.updateForLiving(itemState, hat.item().create(), ItemDisplayContext.HEAD, entity);
            itemStates.add(new HatRenderState(
                    itemState,
                    hat.width(),
                    hat.height(),
                    hat.bounciness()
            ));
        }
        state.setRenderData(HAT_STACK, new StackRenderState(itemStates));
    }

    public static boolean submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int outlineColor, LivingEntityRenderState state) {
        StackRenderState hatStack = state.getRenderData(HAT_STACK);
        if (hatStack == null) {
            return false;
        }

        float bounce = Mth.sin(state.walkAnimationPos) * state.walkAnimationSpeed * 0.0625f;
        Quaternionf wobble = Axis.ZP.rotationDegrees(Mth.sin(state.walkAnimationPos * 0.5f) * state.walkAnimationSpeed * 2.0f);
        Quaternionf bend = Axis.XP.rotationDegrees(state.walkAnimationSpeed * 2.0f);

        poseStack.pushPose();
        poseStack.translate(0.0f, 0.3f, 0.0f);

        for (HatRenderState hat : hatStack.stack) {
            AABB boundingBox = hat.item.getModelBoundingBox();

            poseStack.mulPose(wobble);
            poseStack.mulPose(bend);

            float squash = (bounce * hat.bounciness) + 1.0f;
            float yScale = hat.height * squash;

            // Position each hat consistently by its base - essentially ignoring the y of the transform
            // This is particularly important because full blocks are set to fully cover the head, while top hats are set on top
            poseStack.pushPose();
            poseStack.scale(hat.width, yScale, hat.width);
            poseStack.translate(0.0f, -boundingBox.minY, 0.0f);
            hat.item.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, outlineColor);
            poseStack.popPose();

            poseStack.translate(0.0f, boundingBox.getYsize() * yScale, 0.0f);
        }

        poseStack.popPose();
        return true;
    }

    private record StackRenderState(
            List<HatRenderState> stack
    ) {
    }

    private record HatRenderState(ItemStackRenderState item, float width, float height, float bounciness) {
    }
}
