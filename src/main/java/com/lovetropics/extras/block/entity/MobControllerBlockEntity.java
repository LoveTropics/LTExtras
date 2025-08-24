package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MobControllerBlockEntity extends BlockEntity {
    public boolean loadState = true; // true -> mobs are loaded, false -> mobs are not loaded

    public final List<UUID> uuids = new ArrayList<>();
    public final Map<UUID, EntityType<?>> types = new HashMap<>();
    public final Map<UUID, Vec3> positions = new HashMap<>();

    public record MobConfig(UUID uuid, EntityType<?> type, Vec3 pos) {
        public static final Codec<MobConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(MobConfig::uuid),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(MobConfig::type),
                Vec3.CODEC.fieldOf("pos").forGetter(MobConfig::pos)
        ).apply(i, MobConfig::new));
    }

    public MobControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        List<MobConfig> mobConfigs = input.read("Mobs", MobConfig.CODEC.listOf()).orElse(List.of());
        uuids.clear();
        types.clear();
        positions.clear();
        for (MobConfig mobConfig : mobConfigs) {
            uuids.add(mobConfig.uuid());
            types.put(mobConfig.uuid(), mobConfig.type());
            positions.put(mobConfig.uuid(), mobConfig.pos());
        }

        loadState = input.getBooleanOr("LoadState", false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        List<MobConfig> mobConfigs = new ArrayList<>();
        for (UUID uuid : uuids) {
            mobConfigs.add(new MobConfig(uuid, types.get(uuid), positions.get(uuid)));
        }
        output.store("Mobs", MobConfig.CODEC.listOf(), mobConfigs);
        output.putBoolean("LoadState", loadState);
    }

    public void addEntity(Entity entity) {
        if (entity instanceof ExtendedCreatureEntity ex) { // should always be the case
            ex.linkToBlockEntity(this);

            UUID uuid = entity.getUUID();
            uuids.add(uuid);
            types.put(uuid, entity.getType());
            positions.put(uuid, entity.position());
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MobControllerBlockEntity controller) {
        if (level instanceof ServerLevel serverLevel) {
            long ticks = level.getGameTime();

            // Update pos semi frequently
            if (controller.loadState && ticks % (SharedConstants.TICKS_PER_SECOND / 4) == 0) {
                for (UUID uuid : controller.uuids) {
                    Entity entity = serverLevel.getEntity(uuid);

                    if (entity != null) {
                        controller.positions.put(uuid, entity.position());
                    }
                }
            }

            // Every second
            if (ticks % SharedConstants.TICKS_PER_SECOND == 0) {
                Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, EntitySelector.NO_SPECTATORS);

                if (controller.loadState) {
                    if (player == null) {
                        controller.loadState = false;

                        // Unload!
                        for (UUID uuid : controller.uuids) {
                            Entity entity = serverLevel.getEntity(uuid);

                            if (entity != null) {
                                entity.discard();
                            }
                        }
                    }
                } else {
                    if (player != null) {
                        controller.loadState = true;

                        for (UUID uuid : controller.uuids) {
                            Entity entity = controller.types.get(uuid).create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
                            Vec3 mobPos = controller.positions.get(uuid);

                            if (entity != null) {
                                entity.snapTo(mobPos, 0, 0);

                                entity.setUUID(uuid);
                                level.addFreshEntity(entity);

                                if (entity instanceof Mob mob) {
                                    mob.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
                                }

                                if (entity instanceof ExtendedCreatureEntity extended) {
                                    extended.linkToBlockEntity(controller);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
