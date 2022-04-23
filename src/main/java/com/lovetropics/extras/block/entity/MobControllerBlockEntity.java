package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.Constants;

import java.util.*;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;

public class MobControllerBlockEntity extends BlockEntity implements TickableBlockEntity {
    public boolean loadState = true; // true -> mobs are loaded, false -> mobs are not loaded

    public final List<UUID> uuids = new ArrayList<>();
    public final Map<UUID, EntityType<?>> types = new HashMap<>();
    public final Map<UUID, Vec3> positions = new HashMap<>();

    public MobControllerBlockEntity(BlockEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void load(BlockState state, CompoundTag nbt) {
        super.load(state, nbt);

        ListTag mobUuids = nbt.getList("Mobs", Constants.NBT.TAG_COMPOUND);

        this.uuids.clear();
        for (Tag mobNbt : mobUuids) {
            CompoundTag compoundNBT = (CompoundTag) mobNbt;
            UUID uuid = compoundNBT.getUUID("UUID");
            String type = compoundNBT.getString("Type");

            ListTag pos = compoundNBT.getList("Pos", Constants.NBT.TAG_DOUBLE);
            EntityType<?> entityType = Registry.ENTITY_TYPE.getOptional(new ResourceLocation(type)).get();

            this.uuids.add(uuid);
            this.types.put(uuid, entityType);
            this.positions.put(uuid, new Vec3(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2)));
        }

        this.loadState = nbt.getBoolean("LoadState");
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);

        ListTag mobs = new ListTag();
        for (UUID uuid : this.uuids) {
            CompoundTag compoundNBT = new CompoundTag();
            compoundNBT.putUUID("UUID", uuid);
            compoundNBT.putString("Type", EntityType.getKey(this.types.get(uuid)).toString());

            Vec3 pos = this.positions.get(uuid);
            compoundNBT.put("Pos", newDoubleNBTList(pos.x, pos.y, pos.z));

            mobs.add(compoundNBT);
        }

        compound.put("Mobs", mobs);
        compound.putBoolean("LoadState", this.loadState);

        return compound;
    }

    protected ListTag newDoubleNBTList(double... numbers) {
        ListTag listnbt = new ListTag();

        for(double d0 : numbers) {
            listnbt.add(DoubleTag.valueOf(d0));
        }

        return listnbt;
    }

    public void addEntity(Entity entity) {
        if (entity instanceof ExtendedCreatureEntity) { // should always be the case
            ExtendedCreatureEntity ex = (ExtendedCreatureEntity) entity;

            ex.linkToBlockEntity(this);

            UUID uuid = entity.getUUID();
            this.uuids.add(uuid);
            this.types.put(uuid, entity.getType());
            this.positions.put(uuid, entity.position());
        }
    }

    @Override
    public void tick() {
        Level world = this.getLevel();

        if (world == null) {
            return;
        }

        if (!world.isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) world;

            long ticks = world.getGameTime();

            // Update positions semi frequently
            if (this.loadState && ticks % 5 == 0) {
                for (UUID uuid : this.uuids) {
                    Entity entity = serverWorld.getEntity(uuid);

                    if (entity != null) {
                        this.positions.put(uuid, entity.position());
                    }
                }
            }

            // Every second
            if (ticks % 20 == 0) {
                BlockPos pos = this.getBlockPos();
                Player player = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, EntitySelector.NO_SPECTATORS);

                if (this.loadState) {
                    if (player == null) {
                        this.loadState = false;

                        // Unload!
                        for (UUID uuid : this.uuids) {
                            Entity entity = serverWorld.getEntity(uuid);

                            if (entity != null) {
                                entity.remove();
                            }
                        }
                    }
                } else {
                    if (player != null) {
                        this.loadState = true;

                        for (UUID uuid : this.uuids) {
                            Entity entity = this.types.get(uuid).create(serverWorld);
                            Vec3 mobPos = this.positions.get(uuid);

                            if (entity != null) {
                                entity.moveTo(mobPos.x(), mobPos.y(), mobPos.z(), 0, 0);

                                entity.setUUID(uuid);
                                world.addFreshEntity(entity);

                                if (entity instanceof Mob) {
                                    ((Mob)entity).finalizeSpawn(serverWorld, world.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
                                }

                                if (entity instanceof ExtendedCreatureEntity) {
                                    ((ExtendedCreatureEntity)entity).linkToBlockEntity(this);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
