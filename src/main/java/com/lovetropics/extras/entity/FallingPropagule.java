package com.lovetropics.extras.entity;

import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.EventHooks;

public class FallingPropagule extends AbstractHurtingProjectile {

    public FallingPropagule(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean shouldBurn() {
        return false;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.005f;
    }

    @Override
    public void tick() {
        super.tick();

        applyGravity();
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level() instanceof ServerLevel serverlevel) {
            boolean flag = EventHooks.canEntityGrief(serverlevel, getOwner());
            serverlevel.explode(this, getX(), getY(), getZ(), 0.1f, flag, Level.ExplosionInteraction.MOB);
            discard();
        }
    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level() instanceof ServerLevel serverlevel) {
            Entity hitEntity = result.getEntity();
            DamageSource source = damageSources().cactus();
            hitEntity.hurtServer(serverlevel, source, 2.0F);
            EnchantmentHelper.doPostAttackEffects(serverlevel, hitEntity, source);

            if (hitEntity instanceof final LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(ExtraEffects.PROPAGULED, SharedConstants.TICKS_PER_SECOND * 10));
            }
        }
    }
}
