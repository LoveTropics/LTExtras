package com.lovetropics.extras.mounts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.phys.Vec3;
import org.lovetropics.peekaboo.api.TypedEntityDataInstantiator;

public record Mount(TypedEntityData<EntityType<?>> data) {

    public static final Codec<Mount> MAP_CODEC = TypedEntityData.codec(EntityType.CODEC).xmap(Mount::new, Mount::data);

    public boolean spawnFor(Entity player, ServerLevel serverLevel) {
        Entity entity = EntityType.loadEntityRecursive(data.type(), data.copyTagWithoutId(), serverLevel, EntitySpawnReason.COMMAND, e -> {
            e.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            e.setYRot(player.getYRot());
            e.setYBodyRot(player.getYRot());
            e.setYHeadRot(player.getYRot());
            return e;
        });

        if (entity == null) {
            return false;
        }

        if (entity instanceof Mob mob) {
            mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.COMMAND, null);
        }

        if (serverLevel.tryAddFreshEntityWithPassengers(entity)) {
            MountStore.markMount(entity);
            player.startRiding(entity, true, false);
        }

        return true;
    }

}
