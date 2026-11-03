package com.lovetropics.extras.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.item.ItemExtensions;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {
    public LocalPlayerMixin(Level level, GameProfile profile) {
        super(level, profile);
    }

    @Inject(method = "getWaterVision", at = @At("HEAD"), cancellable = true)
    private void getWaterVision(CallbackInfoReturnable<Float> ci) {
        if (hasEffect(ExtraEffects.FISH_EYE)) {
            ci.setReturnValue(1.0f);
        }
    }

    @WrapOperation(method = "drop(Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;removeFromSelected(Z)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack drop(Inventory inventory, boolean all, Operation<ItemStack> original) {
        ItemStack stack = ItemExtensions.dropSelected(inventory, all);
        return stack != null ? stack : original.call(inventory, all);
    }
}
