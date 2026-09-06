package com.lovetropics.extras.client.environmentattribute;

import com.lovetropics.extras.environmentattribute.ExtraEnvironmentAttributes;
import com.lovetropics.extras.environmentattribute.ExtraParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientExtraEnvironmentAttributeHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.getCameraEntity();
        if (entity != null && !minecraft.isPaused()) {
            Level level = entity.level();
            ExtraParticles particles = level.environmentAttributes().getValue(ExtraEnvironmentAttributes.EXTRA_PARTICLES.get(), entity.position());
            for (ExtraParticles.Particle particle : particles.particles()) {
                addParticles(level, entity.getRandom(), entity.blockPosition(), particle);
            }
        }
    }

    private static void addParticles(Level level, RandomSource random, BlockPos playerPosition, ExtraParticles.Particle particle) {
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
}
