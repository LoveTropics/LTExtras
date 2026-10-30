package com.lovetropics.extras.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.lovetropics.extras.client.HatStackRenderer;
import com.lovetropics.extras.hats.StackedHatStore;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CustomHeadLayer.class)
public class CustomHeadLayerMixin<S extends LivingEntityRenderState> {

    @Redirect(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;isEmpty()Z"))
    private boolean isEmpty(ItemStackRenderState instance, @Local(argsOnly = true, name = "state") S state) {
        HatStackRenderer.StackRenderState stackRenderState = state.getRenderData(HatStackRenderer.HAT_STACK);
        if (stackRenderState != null && !stackRenderState.stack().isEmpty()) {
            return false; // skip the `state.headItem.isEmpty()` check if we have hats to render as they may be from attachments and not wearing a head item.
        }
        return !instance.isEmpty();
    }


    @WrapOperation(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    private void submit(ItemStackRenderState item, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor, Operation<Void> original, @Local(argsOnly = true, name = "state") S state) {
        if (HatStackRenderer.submit(poseStack, submitNodeCollector, lightCoords, outlineColor, state)) {
            return;
        }
        original.call(item, poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
    }
}
