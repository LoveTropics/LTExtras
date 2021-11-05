package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class MobControllerBlockEntity extends TileEntity implements ITickableTileEntity {
    public boolean loadState = true; // true -> mobs are loaded, false -> mobs are not loaded

    public final List<UUID> uuids = new ArrayList<>();
    public final Map<UUID, EntityType<?>> types = new HashMap<>();
    public final Map<UUID, Vector3d> positions = new HashMap<>();

    public MobControllerBlockEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);

        ListNBT mobUuids = nbt.getList("Mobs", Constants.NBT.TAG_COMPOUND);

        this.uuids.clear();
        for (INBT mobNbt : mobUuids) {
            CompoundNBT compoundNBT = (CompoundNBT) mobNbt;
            UUID uuid = compoundNBT.getUniqueId("UUID");
            String type = compoundNBT.getString("Type");

            ListNBT pos = compoundNBT.getList("Pos", Constants.NBT.TAG_DOUBLE);
            EntityType<?> entityType = Registry.ENTITY_TYPE.getOptional(new ResourceLocation(type)).get();

            this.uuids.add(uuid);
            this.types.put(uuid, entityType);
            this.positions.put(uuid, new Vector3d(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2)));
        }

        this.loadState = nbt.getBoolean("LoadState");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);

        ListNBT mobs = new ListNBT();
        for (UUID uuid : this.uuids) {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putUniqueId("UUID", uuid);
            compoundNBT.putString("Type", EntityType.getKey(this.types.get(uuid)).toString());

            Vector3d pos = this.positions.get(uuid);
            compoundNBT.put("Pos", newDoubleNBTList(pos.x, pos.y, pos.z));

            mobs.add(compoundNBT);
        }

        compound.put("Mobs", mobs);
        compound.putBoolean("LoadState", this.loadState);

        return compound;
    }

    protected ListNBT newDoubleNBTList(double... numbers) {
        ListNBT listnbt = new ListNBT();

        for(double d0 : numbers) {
            listnbt.add(DoubleNBT.valueOf(d0));
        }

        return listnbt;
    }

    public void addEntity(Entity entity) {
        if (entity instanceof ExtendedCreatureEntity) { // should always be the case
            ExtendedCreatureEntity ex = (ExtendedCreatureEntity) entity;

            ex.linkToBlockEntity(this);

            UUID uuid = entity.getUniqueID();
            this.uuids.add(uuid);
            this.types.put(uuid, entity.getType());
            this.positions.put(uuid, entity.getPositionVec());
        }
    }

    @Override
    public void tick() {
        World world = this.getWorld();

        if (world == null) {
            return;
        }

        if (!world.isRemote()) {
            ServerWorld serverWorld = (ServerWorld) world;

            long ticks = world.getGameTime();

            // Update positions semi frequently
            if (this.loadState && ticks % 5 == 0) {
                for (UUID uuid : this.uuids) {
                    Entity entity = serverWorld.getEntityByUuid(uuid);

                    if (entity != null) {
                        this.positions.put(uuid, entity.getPositionVec());
                    }
                }
            }

            // Every second
            if (ticks % 20 == 0) {
                BlockPos pos = this.getPos();
                PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, EntityPredicates.NOT_SPECTATING);

                if (this.loadState) {
                    if (player == null) {
                        this.loadState = false;

                        // Unload!
                        for (UUID uuid : this.uuids) {
                            Entity entity = serverWorld.getEntityByUuid(uuid);

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
                            Vector3d mobPos = this.positions.get(uuid);

                            if (entity != null) {
                                entity.setLocationAndAngles(mobPos.getX(), mobPos.getY(), mobPos.getZ(), 0, 0);

                                entity.setUniqueId(uuid);
                                world.addEntity(entity);

                                if (entity instanceof MobEntity) {
                                    ((MobEntity)entity).onInitialSpawn(serverWorld, world.getDifficultyForLocation(pos), SpawnReason.MOB_SUMMONED, null, null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
