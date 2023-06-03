package com.lovetropics.extras.mixin.client;

import com.lovetropics.extras.effect.ExtraEffects;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {

	public LocalPlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile, @Nullable ProfilePublicKey pProfilePublicKey) {
		super(pLevel, pPos, pYRot, pGameProfile, pProfilePublicKey);
	}

	@Inject(method = "getWaterVision", at = @At("HEAD"), cancellable = true)
	private void getWaterVision(CallbackInfoReturnable<Float> ci) {
		if (hasEffect(ExtraEffects.FISH_EYE.get())) {
			ci.setReturnValue(1.0f);
		}
	}
}
