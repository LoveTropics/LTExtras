package com.lovetropics.extras.mixin.client.world_effect;

import com.lovetropics.extras.client.world_effect.SkyColorEffectHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @ModifyVariable(method = "getSkyColor", ordinal = 2, at = @At(value = "STORE", ordinal = 0))
    private Vec3 modifySkyColor(Vec3 color, Vec3 position, float partialTicks) {
        return SkyColorEffectHandler.modifyColor(color, partialTicks);
    }
}
