package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.extension.LingeringPotionExtension;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.AreaEffectCloud;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin {

    @Shadow
    public abstract void setRadius(float radius);

    @Shadow
    public abstract void setRadiusPerTick(float radiusPerTick);

    @Shadow
    public abstract void setRadiusOnUse(float radiusOnUse);

    @Shadow
    public abstract void setDuration(int duration);

    @Shadow
    public abstract void setDurationOnUse(int durationOnUse);

    @Shadow
    public abstract void setWaitTime(int waitTime);

    @Shadow
    public abstract void setCustomParticle(@Nullable ParticleOptions customParticle);

    @Shadow
    private int reapplicationDelay;

    @Inject(method = "applyImplicitComponents", at = @At("RETURN"))
    private void applyImplicitComponents(DataComponentGetter components, CallbackInfo ci) {
        LingeringPotionExtension extension = components.get(ExtraDataComponents.LINGERING_POTION_EXTENSION);
        if (extension != null) {
            ltextras$applyFor(extension);
        }
    }

    @Unique
    public void ltextras$applyFor(LingeringPotionExtension extension) {
        extension.reapplicationDelay().ifPresent(newDelay -> reapplicationDelay = newDelay);
        extension.radius().ifPresent(this::setRadius);
        extension.radiusPerTick().ifPresent(this::setRadiusPerTick);
        extension.radiusOnUse().ifPresent(this::setRadiusOnUse);
        extension.duration().ifPresent(this::setDuration);
        extension.durationOnUse().ifPresent(this::setDurationOnUse);
        extension.waitTime().ifPresent(this::setWaitTime);
        extension.customParticle().ifPresent(this::setCustomParticle);
    }


}
