package com.lovetropics.extras.mixin.client;

import com.lovetropics.extras.client.world_effect.EnvironmentAttributeEffectHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
@Debug(export = true)
public class ClientLevelMixin {

    @Inject(method = "addEnvironmentAttributeLayers", at = @At("RETURN"))
    public void injectAttributes(EnvironmentAttributeSystem.Builder environmentAttributes, CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) {
        EnvironmentAttributeEffectHandler.inject((ClientLevel) (Object) this, environmentAttributes);
    }
}
