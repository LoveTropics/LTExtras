package com.lovetropics.extras.client.world_effect;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.world_effect.ParticlesEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class WorldParticleEffectHandler {
    @Nullable
    private static ParticlesEffect effect;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            effect = null;
            return;
        }

        if (effect != null && !minecraft.isPaused()) {
            for (ParticlesEffect.Particle particle : effect.particles()) {
                addParticles(player.level(), player.getRandom(), player.blockPosition(), particle);
            }
        }
    }

    private static void addParticles(Level level, RandomSource random, BlockPos playerPosition, ParticlesEffect.Particle particle) {
        int range = particle.range();
        BlockPos origin = playerPosition.offset(particle.offset());

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < particle.count(); i++) {
            pos.setWithOffset(origin,
                    random.nextInt(range) - random.nextInt(range),
                    random.nextInt(range) - random.nextInt(range),
                    random.nextInt(range) - random.nextInt(range)
            );

            BlockState state = level.getBlockState(pos);
            if (!state.isCollisionShapeFullBlock(level, pos)) {
                level.addParticle(particle.particle(), pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
            }
        }
    }

    public static void set(@Nullable ParticlesEffect effect) {
        WorldParticleEffectHandler.effect = effect;
    }
}
