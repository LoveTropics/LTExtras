package com.lovetropics.extras.client.world_effect;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.world_effect.ParticlesEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class WorldParticleEffectHandler {
    private static ParticlesEffect effect;

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = minecraft.player;
        if (player == null) {
            effect = null;
            return;
        }

        if (effect != null && event.phase == TickEvent.Phase.START && !minecraft.isPaused()) {
            for (final ParticlesEffect.Particle particle : effect.particles()) {
                addParticles(player.level(), player.getRandom(), player.blockPosition(), particle);
            }
        }
    }

    private static void addParticles(final Level level, final RandomSource random, final BlockPos playerPosition, final ParticlesEffect.Particle particle) {
        final int range = particle.range();
        final BlockPos origin = playerPosition.offset(particle.offset());

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < particle.count(); i++) {
            pos.setWithOffset(origin,
                    random.nextInt(range) - random.nextInt(range),
                    random.nextInt(range) - random.nextInt(range),
                    random.nextInt(range) - random.nextInt(range)
            );

            final BlockState state = level.getBlockState(pos);
            if (!state.isCollisionShapeFullBlock(level, pos)) {
                level.addParticle(particle.particle(), pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
            }
        }
    }

    public static void set(@Nullable final ParticlesEffect effect) {
        WorldParticleEffectHandler.effect = effect;
    }
}
