package com.lovetropics.extras.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireExtinguisherItem extends BlockItem {

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
        Vec3 pos = livingEntity.getEyePosition();
        Vec3 deltaMovement = livingEntity.getDeltaMovement();
        float dist = 2.0f;
        Vec3 lookVec = livingEntity.getLookAngle().multiply(dist, dist, dist);
        float velocityAmp = 0.5f;
        Vec3 velocity = new Vec3(velocityAmp * lookVec.x, lookVec.y, velocityAmp * lookVec.z);
        float spray = 6.0f;

        if (level.isClientSide) {
            for (int i = -5; i < 5; i++) {
                double xSpeed = velocity.x + (i / spray);
                double zSpeed = velocity.z + (i / spray);

                double xLoc = pos.x + lookVec.x + level.random.triangle(0, 1f);
                double zLoc = pos.z + lookVec.z + level.random.triangle(0, 1f);
                level.addParticle(ParticleTypes.CLOUD, true, true, xLoc, pos.y, zLoc, xSpeed, velocity.y, zSpeed);
                if (i % 3 == 0) {
                    level.addParticle(ParticleTypes.SMOKE, true, true, xLoc, pos.y, zLoc, xSpeed, velocity.y, zSpeed);
                }
            }
        }

        if (livingEntity.tickCount % 2 == 0) {
            level.playSound(livingEntity, pos.x, pos.y, pos.z, SoundEvents.CANDLE_EXTINGUISH, SoundSource.PLAYERS, 1f, 1f);
        }

        if (!level.isClientSide) {
            HitResult result = ProjectileUtil.getHitResultOnViewVector(livingEntity, Entity::isPickable, 10);
            if (result.getType() != HitResult.Type.MISS) {
                if (result instanceof final EntityHitResult entityHitResult) {
                    if (entityHitResult.getEntity().isOnFire()) {
                        entityHitResult.getEntity().extinguishFire();
                    }
                } else if (result instanceof final BlockHitResult blockHitResult) {
                    BlockPos hitPos = blockHitResult.getBlockPos();
                    BlockPos above = hitPos.above();
                    if (level.getBlockState(above).is(BlockTags.FIRE)) {
                        level.setBlockAndUpdate(above, Blocks.AIR.defaultBlockState());
                    }
                }
            }

        }
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
