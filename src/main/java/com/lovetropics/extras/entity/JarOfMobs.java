package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.effect.TransientExistenceEffect;
import com.lovetropics.extras.item.loot.JarredMobsData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class JarOfMobs extends ThrowableItemProjectile {
    private static final double BEE_ANGRY_RANGE = 40;

    public JarOfMobs(EntityType<? extends JarOfMobs> type, Level level) {
        super(type, level);
    }

    public JarOfMobs(Level level, LivingEntity mob, ItemStack itemStack) {
        super(ExtraEntities.JAR_OF_MOBS.get(), mob, level, itemStack);
    }

    public JarOfMobs(Level level, double x, double y, double z, ItemStack itemStack) {
        super(ExtraEntities.JAR_OF_MOBS.get(), x, y, z, level, itemStack);
    }

    protected Item getDefaultItem() {
        return ExtraItems.JAR_OF_MOBS.asItem();
    }

    private ParticleOptions getParticle() {
        return new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(this.getItem()));
    }

    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particle = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

    }

    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        DamageSource damageSource = this.damageSources().thrown(this, this.getOwner());
        if (entity.level() instanceof ServerLevel serverLevel) {
            entity.hurtServer(serverLevel, damageSource, (float)1.0);
            return;
        }
        entity.hurtClient(damageSource);
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)3);
            if (this.level() instanceof ServerLevel serverLevel) {
                this.spawnContents(serverLevel, hitResult.getLocation());
            }
            this.discard();
        }
    }

    protected void spawnContents(ServerLevel serverLevel, Vec3 location) {
        JarredMobsData contents = this.getItem().getOrDefault(ExtraDataComponents.JARRED_MOBS, JarredMobsData.getEmpty());
        if (contents.entities().isEmpty()) {
            return;
        }
        double spread = Math.clamp(contents.spread(), 0.0, 4.0);
        RandomSource random = serverLevel.getRandom();
        for (TypedEntityData<EntityType<?>> typedEntityData : contents.entities()) {
            for (int i = 0; i < contents.spawnCount(); i++) {
                EntityType<?> entityType = typedEntityData.type();
                if (!entityType.canSpawn(serverLevel)) {
                    continue;
                }

                Entity entity = entityType.create(serverLevel, null, this.blockPosition(), EntitySpawnReason.SPAWN_ITEM_USE, false, false);
                if (entity == null) {
                    continue;
                }
                typedEntityData.loadInto(entity);

                Vec3 spawnLoc = location.add((1-(random.nextDouble()*2))*spread, random.nextDouble()*spread, (1-(random.nextDouble()*2))*spread);
                BlockPos blockPos = BlockPos.containing(spawnLoc);
                BlockState blockstate = serverLevel.getBlockState(blockPos);

                entity.snapTo(spawnLoc);

                //if (blockstate.isValidSpawn(serverLevel, blockPos, entityType)) {
                //
                //} else {
                //    entity.snapTo(this.position());
                //}

                if (entity.is(EntityTypes.BEE)) {
                    this.makeBeeAngry(serverLevel, (Bee) entity);
                }

                serverLevel.addFreshEntityWithPassengers(entity);
                if (entity instanceof Mob mob) {
                    mob.playAmbientSound();
                }
            }
        }
    }


    protected void makeBeeAngry(ServerLevel serverLevel, Bee bee) {
        // check for throwing players team -> get nearby players -> eliminate those with the same team -> set targets randomly
        List<Player> players = serverLevel.getNearbyPlayers(
            TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting().range(BEE_ANGRY_RANGE),
            bee,
            AABB.ofSize(this.position(), BEE_ANGRY_RANGE*2, BEE_ANGRY_RANGE*2, BEE_ANGRY_RANGE*2)
        );


        // Filter out owner and owners teammates from valid targets
        if (this.getOwner() instanceof Player owner) {
            if (owner.getTeam() instanceof PlayerTeam ownerTeam) {
                players.removeIf((player) -> player.isAlliedTo(ownerTeam) || player.is(owner));
            } else {
                players.removeIf((player) -> player.is(owner));
            }
        }

        // If no potential targets remain, the bee's don't get angry :shrug:
        if (players.isEmpty()) {
            return;
        }

        Optional<Player> target = Util.getRandomSafe(players, serverLevel.getRandom());
        if (target.isEmpty()) {
            return;
        }

        target.get().sendSystemMessage(Component.literal("You are a bee target"));

        // Make da' bee angry and also cut short its existence
        bee.setPersistentAngerTarget(EntityReference.of(target.get()));
        bee.setPersistentAngerEndTime(99999);
        bee.startPersistentAngerTimer();
        bee.addEffect(new MobEffectInstance(ExtraEffects.TRANSIENT_EXISTENCE, 45*20, 0, false, false));
    }
}
