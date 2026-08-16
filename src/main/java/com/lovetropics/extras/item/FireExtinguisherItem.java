package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class FireExtinguisherItem extends BlockItem {
    /** Modifiable in FireExtinguisher data component */
    public static final float DEFAULT_SHOOT_DIST = 12;
    public static final float DEFAULT_IMPULSE_ON_HIT = 0.25f;
    public static final float DEFAULT_IMPULSE_ON_MISS = 0.05f;

    public static final float SPRAY = 6.0f;
    public static final int HOW_FAR_IN_FRONT_TO_SHOOT_FROM = 2;

    public FireExtinguisherItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        final FireExtinguisher extinguisherComponent = stack.getOrDefault(ExtraDataComponents.FIRE_EXTINGUISHER, FireExtinguisher.getDefault());

        Vec3 pos = livingEntity.getEyePosition();
        Vec3 lookVec = livingEntity.getLookAngle();
        Vec3 startPos = livingEntity.getLookAngle().scale(HOW_FAR_IN_FRONT_TO_SHOOT_FROM);
        boolean hit = false;

        for (int i = -5; i < 5; i++) {
            final double xSpeed = lookVec.x + (i / SPRAY);
            final double zSpeed = lookVec.z + (i / SPRAY);

            final double xLoc = pos.x + startPos.x + level.getRandom().triangle(0, 1f);
            final double zLoc = pos.z + startPos.z + level.getRandom().triangle(0, 1f);
            if (level.isClientSide()) {
                level.addParticle(ParticleTypes.CLOUD, true, true, xLoc, pos.y, zLoc, xSpeed, lookVec.y, zSpeed);
                if (i % 3 == 0) {
                    level.addParticle(ParticleTypes.SMOKE, true, true, xLoc, pos.y, zLoc, xSpeed, lookVec.y, zSpeed);
                }
            }

            Vec3 to = livingEntity.getViewVector(0.0F).scale(extinguisherComponent.shootDist());
            to = to.add(xSpeed, 0, zSpeed);

            // Put out fires
            HitResult result = getHitResultOnViewVector(livingEntity, to, Entity::isPickable);
            if (result.getType() != HitResult.Type.MISS) {
                hit = true;
                if (!level.isClientSide() && extinguisherComponent.shouldExtinguish()) {
                    if (result instanceof final EntityHitResult entityHitResult) {
                        if (entityHitResult.getEntity().isOnFire()) {
                            entityHitResult.getEntity().extinguishFire();
                        }
                    } else if (result instanceof final BlockHitResult blockHitResult) {
                        BlockPos hitPos = blockHitResult.getBlockPos();
                        FluidState hitFluidState = level.getFluidState(hitPos);

                        if (hitFluidState.is(FluidTags.LAVA)) {
                            level.playSound(null, hitPos.getX(), hitPos.getY(), hitPos.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.2f, 1f);
                            level.setBlockAndUpdate(hitPos, Blocks.OBSIDIAN.defaultBlockState());
                        }

                        BlockPos above = hitPos.above();
                        BlockState aboveState = level.getBlockState(above);

                        if (aboveState.is(BlockTags.FIRE)) {
                            level.destroyBlock(above, false, livingEntity);
                        } else if (AbstractCandleBlock.isLit(aboveState)) {
                            AbstractCandleBlock.extinguish(null, aboveState, level, above);
                        } else if (CampfireBlock.isLitCampfire(aboveState)) {
                            level.levelEvent(null, 1009, above, 0);
                            CampfireBlock.dowse(livingEntity, level, above, aboveState);
                            level.setBlockAndUpdate(above, aboveState.setValue(CampfireBlock.LIT, false));
                        }
                    }
                }
            }
        }

        final float scale = hit ? extinguisherComponent.impulseOnHit() : extinguisherComponent.impulseOnMiss();
        if(livingEntity.getVehicle() != null && !level.isClientSide()){
            livingEntity.getVehicle().needsSync = true;
            livingEntity.getVehicle().setDeltaMovement(lookVec.reverse().scale(scale));
        } else {
            if (level.isClientSide()) {
                livingEntity.needsSync = true;
                livingEntity.setDeltaMovement(lookVec.reverse().scale(scale));
            } else {
                EquipmentSlot slot = stack.equals(livingEntity.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                stack.hurtAndBreak(extinguisherComponent.durabilityLossPerTick(), livingEntity, slot);
            }
        }


        if (livingEntity.tickCount % 2 == 0) {
            level.playSound(livingEntity, pos.x, pos.y, pos.z, SoundEvents.CANDLE_EXTINGUISH, SoundSource.PLAYERS, 1f, 1f);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    private static HitResult getHitResultOnViewVector(Entity projectile, Vec3 to, Predicate<Entity> filter) {
        Level level = projectile.level();
        Vec3 from = projectile.getEyePosition();
        return getHitResult(from, projectile, filter, to, level);
    }

    private static HitResult getHitResult(Vec3 from, Entity projectile, Predicate<Entity> filter, Vec3 to, Level level) {
        Vec3 ray = from.add(to);
        HitResult hitresult = level.clipIncludingBorder(new ClipContext(from, ray, ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.ANY, projectile));
        if (hitresult.getType() != HitResult.Type.MISS) {
            ray = hitresult.getLocation();
        }

        HitResult hitresult1 = ProjectileUtil.getEntityHitResult(level, projectile, from, ray, projectile.getBoundingBox().expandTowards(to).inflate((double)1.0F), filter, (float) 0.0);
        if (hitresult1 != null) {
            hitresult = hitresult1;
        }

        return hitresult;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 999999;
    }
}
