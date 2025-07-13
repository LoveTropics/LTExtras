package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
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

	public record MobConfig(UUID uuid, ResourceKey<EntityType<?>> type, Vec3 pos){
		public static final Codec<MobConfig> CODEC = RecordCodecBuilder.create(inst ->
				inst.group(UUIDUtil.CODEC.fieldOf("uuid").forGetter(MobConfig::uuid),
								ResourceKey.codec(Registries.ENTITY_TYPE).fieldOf("type").forGetter(MobConfig::type),
								Vec3.CODEC.fieldOf("pos").forGetter(MobConfig::pos))
						.apply(inst, MobConfig::new));
	}

	public MobControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);

		tag.read("Mobs", MobConfig.CODEC.listOf()).ifPresent(mobConfigs -> {
			uuids.clear();
			for (MobConfig mobConfig : mobConfigs) {
				registries.lookupOrThrow(Registries.ENTITY_TYPE).get(mobConfig.type()).ifPresent(entityType -> {
					uuids.add(mobConfig.uuid());
					types.put(mobConfig.uuid(), entityType.value());
					positions.put(mobConfig.uuid(), mobConfig.pos());
				});
			}
		});

		loadState = tag.getBooleanOr("LoadState", false);
	}

	@Override
	protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
		super.saveAdditional(compound, registries);

		List<MobConfig> mobConfigs = new ArrayList<>();
		for (UUID uuid : uuids) {
			ResourceLocation key = EntityType.getKey(types.get(uuid));
			ResourceKey<EntityType<?>> entityTypeResourceKey = ResourceKey.create(Registries.ENTITY_TYPE, key);
			Vec3 pos = positions.get(uuid);
			mobConfigs.add(new MobConfig(uuid, entityTypeResourceKey, pos));
		}
		compound.store("Mobs", MobConfig.CODEC.listOf(), mobConfigs);
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
								entity.moveOrInterpolateTo(mobPos, 0, 0);

								entity.setUUID(uuid);
								level.addFreshEntity(entity);

								if (entity instanceof Mob) {
									((Mob) entity).finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
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
