package com.lovetropics.extras.mixin;

import com.lovetropics.extras.client.particle.ExtraParticles;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Shulker.class)
public abstract class ShulkerMixin extends Entity {

    @Shadow
    @Final
    protected static EntityDataAccessor<Byte> DATA_PEEK_ID;

    public ShulkerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at=@At("HEAD"), cancellable = true)
    public void getHonied(CallbackInfo ci) {
        if (getData(ExtraAttachments.HONIED)) {
            if (!level().isClientSide) {
                // Shut yo face
                entityData.set(DATA_PEEK_ID, (byte) 0);

                ci.cancel();
            } else {
                lTExtras$addHoneyParticlesAroundSelf(ExtraParticles.SHORT_HONEY_PARTICLE.get());
            }
        }
    }

    @Unique
    private void lTExtras$addHoneyParticlesAroundSelf(ParticleOptions particleOption) {
        double d0 = getRandom().nextGaussian() * 0.02;
        double d1 = getRandom().nextGaussian() * 0.02;
        double d2 = getRandom().nextGaussian() * 0.02;
        level().addParticle(particleOption, getRandomX(0.7F), getRandomY(), getRandomZ(0.7F), d0, d1, d2);
    }
}
