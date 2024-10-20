package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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

	public MobControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);

		ListTag mobUuids = tag.getList("Mobs", Tag.TAG_COMPOUND);

		uuids.clear();
		for (Tag mobNbt : mobUuids) {
			CompoundTag compoundNBT = (CompoundTag) mobNbt;
			UUID uuid = compoundNBT.getUUID("UUID");
			ResourceLocation type = ResourceLocation.parse(compoundNBT.getString("Type"));

			ListTag pos = compoundNBT.getList("Pos", Tag.TAG_DOUBLE);
			registries.lookupOrThrow(Registries.ENTITY_TYPE).get(ResourceKey.create(Registries.ENTITY_TYPE, type)).ifPresent(entityType -> {
				uuids.add(uuid);
				types.put(uuid, entityType.value());
				positions.put(uuid, new Vec3(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2)));
			});
		}

		loadState = tag.getBoolean("LoadState");
	}

	@Override
	protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
		super.saveAdditional(compound, registries);

		ListTag mobs = new ListTag();
		for (UUID uuid : uuids) {
			CompoundTag compoundNBT = new CompoundTag();
			compoundNBT.putUUID("UUID", uuid);
			compoundNBT.putString("Type", EntityType.getKey(types.get(uuid)).toString());

			Vec3 pos = positions.get(uuid);
			compoundNBT.put("Pos", newDoubleNBTList(pos.x, pos.y, pos.z));

			mobs.add(compoundNBT);
		}

		compound.put("Mobs", mobs);
		compound.putBoolean("LoadState", loadState);
	}

	protected ListTag newDoubleNBTList(double... numbers) {
		ListTag listnbt = new ListTag();

		for (double d0 : numbers) {
			listnbt.add(DoubleTag.valueOf(d0));
		}

		return listnbt;
	}

	public void addEntity(Entity entity) {
		if (entity instanceof ExtendedCreatureEntity) { // should always be the case
			ExtendedCreatureEntity ex = (ExtendedCreatureEntity) entity;

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

			// Update positions semi frequently
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
							Entity entity = controller.types.get(uuid).create(serverLevel);
							Vec3 mobPos = controller.positions.get(uuid);

							if (entity != null) {
								entity.moveTo(mobPos.x(), mobPos.y(), mobPos.z(), 0, 0);

								entity.setUUID(uuid);
								level.addFreshEntity(entity);

								if (entity instanceof Mob) {
									((Mob) entity).finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
								}

								if (entity instanceof ExtendedCreatureEntity) {
									((ExtendedCreatureEntity) entity).linkToBlockEntity(controller);
								}
							}
						}
					}
				}
			}
		}
	}
}
