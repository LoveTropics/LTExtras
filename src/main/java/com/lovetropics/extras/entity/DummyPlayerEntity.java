package com.lovetropics.extras.entity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Maps;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.DummyPlayerItem;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;

public class DummyPlayerEntity extends ArmorStandEntity {

	public static final RegistryEntry<EntityType<DummyPlayerEntity>> DUMMY_PLAYER = LTExtras.registrate().object("dummy_player")
			.<DummyPlayerEntity>entity(DummyPlayerEntity::new, EntityClassification.MISC)
			.properties(b -> b.size(0.6F, 1.8F)) // Copied from player definition
			.register();

	public static final ItemEntry<DummyPlayerItem> SPAWNER = LTExtras.registrate()
			.item(DummyPlayerItem::new)
			.register();

	private static final IDataSerializer<GameProfile> PROFILE_SERIALIZER = new IDataSerializer<GameProfile>() {
		public GameProfile copyValue(GameProfile value) {
			return value == null ? null : new GameProfile(value.getId(), value.getName());
		}

		@Override
		public GameProfile read(PacketBuffer buf) {
			int mode = buf.readByte();
			UUID id = null;
			String name = null;
			if ((mode & 0b01) > 0) {
				id = buf.readUniqueId();
			}
			if ((mode & 0b10) > 0) {
				name = buf.readString(100);
			}
			if (id == null && name == null) {
				return null;
			}
			return new GameProfile(id, name);
		}

		@Override
		public void write(PacketBuffer buf, GameProfile value) {
			int mode = 0;
			if (value != null && value.getId() != null) {
				mode |= 0b01;
			}
			if (value != null && value.getName() != null) {
				mode |= 0b10;
			}
			buf.writeByte(mode);
			if ((mode & 0b01) > 0) {
				buf.writeUniqueId(value.getId());
			}
			if ((mode & 0b10) > 0) {
				buf.writeString(value.getName(), 100);
			}
		}
	};
	static {
		DataSerializers.registerSerializer(PROFILE_SERIALIZER);
	}

	private static final DataParameter<GameProfile> GAME_PROFILE = EntityDataManager.createKey(DummyPlayerEntity.class, PROFILE_SERIALIZER);
	private static final DataParameter<Optional<ITextComponent>> PREFIX = EntityDataManager.createKey(DummyPlayerEntity.class, DataSerializers.OPTIONAL_TEXT_COMPONENT);
	private static final DataParameter<Optional<ITextComponent>> SUFFIX = EntityDataManager.createKey(DummyPlayerEntity.class, DataSerializers.OPTIONAL_TEXT_COMPONENT);

	private static final LazyValue<PlayerProfileCache> PROFILE_CACHE = new LazyValue<>(() -> 
		ObfuscationReflectionHelper.getPrivateValue(SkullTileEntity.class, null, "field_184298_j"));

	public static void register() {}

	// Clientside texture info
	private boolean reloadTextures = true;
	private final Map<Type, ResourceLocation> playerTextures = Maps.newEnumMap(Type.class);
	@Nullable
	private String skinType;

	protected DummyPlayerEntity(EntityType<? extends DummyPlayerEntity> type, World worldIn) {
		super(type, worldIn);
		if (!worldIn.isRemote) {
			// Show arms always on
			this.dataManager.set(STATUS, (byte) 0b100);
		}
	}

	public DummyPlayerEntity(World worldIn, double posX, double posY, double posZ) {
		this(DUMMY_PLAYER.get(), worldIn);
		this.setPosition(posX, posY, posZ);
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(GAME_PROFILE, null);
		this.dataManager.register(PREFIX, Optional.empty());
		this.dataManager.register(SUFFIX, Optional.empty());
	}

	@Override
	public ITextComponent getProfessionName() {
		return getProfile() == null || getProfile().getName() == null ? super.getProfessionName() : new StringTextComponent(getProfile().getName());
	}

	@Override
	public ITextComponent getDisplayName() {
		ITextComponent ret = super.getDisplayName().deepCopy();
		ITextComponent prefix = this.dataManager.get(PREFIX).orElse(null);
		ITextComponent suffix = this.dataManager.get(SUFFIX).orElse(null);
		if (prefix != null) {
			ret = prefix.deepCopy().appendSibling(ret);
		}
		if (suffix != null) {
			ret = ret.appendSibling(suffix.deepCopy());
		}
		return ret;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(SPAWNER.get());
	}

	public GameProfile getProfile() {
		return this.dataManager.get(GAME_PROFILE);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

		this.dataManager.get(PREFIX)
			.ifPresent(prefix -> compound.putString("NamePrefix", ITextComponent.Serializer.toJson(prefix)));
		this.dataManager.get(SUFFIX)
			.ifPresent(suffix -> compound.putString("NameSuffix", ITextComponent.Serializer.toJson(suffix)));

		GameProfile profile = getProfile();
		if (profile == null) return;
		if (profile.getId() != null) {
			compound.putUniqueId("ProfileID", profile.getId());
		} else if (profile.getName() != null) {
			compound.putString("ProfileName", profile.getName());
		}
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		if (compound.contains("NamePrefix", Constants.NBT.TAG_STRING)) {
			this.dataManager.set(PREFIX, Optional.ofNullable(ITextComponent.Serializer.fromJson(compound.getString("NamePrefix"))));
		}
		if (compound.contains("NameSuffix", Constants.NBT.TAG_STRING)) {
			this.dataManager.set(SUFFIX, Optional.ofNullable(ITextComponent.Serializer.fromJson(compound.getString("NameSuffix"))));
		}

		if (compound.contains("ProfileName", Constants.NBT.TAG_STRING)) {
			String name = compound.getString("ProfileName");
			if (!StringUtils.isBlank(name)) {
				this.dataManager.set(GAME_PROFILE, new GameProfile(null, compound.getString("ProfileName")));
			} else {
				this.dataManager.set(GAME_PROFILE, null);
			}
			fillProfile();
		} else if (compound.hasUniqueId("ProfileID")) {
			String existingName = getProfile() == null ? null : getProfile().getName();
			UUID newId = compound.getUniqueId("ProfileID");
			if (getProfile() == null || getProfile().getId() == null && getProfile().getName() == null
					|| !getProfile().getId().equals(newId)) {
				// Only update the profile (and thus the texture) if it has changed in some way
				// Avoids unnecessary texture reloads on the client when changing pose/name
				this.dataManager.set(GAME_PROFILE, new GameProfile(compound.getUniqueId("ProfileID"), existingName));
				fillProfile();
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!world.isRemote && reloadTextures) {
			LTExtrasNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new UpdateDummyTexturesMessage(this.getEntityId()));
			reloadTextures = false;
		}
	}

	void fillProfile() {
		final GameProfile profile = getProfile();
		if (profile == null) {
			reloadTextures();
		}
		CompletableFuture.supplyAsync(() -> {
			GameProfile ret;
			if (profile.getId() != null) {
				ret = PROFILE_CACHE.getValue().getProfileByUUID(getProfile().getId());
			} else {
				ret = PROFILE_CACHE.getValue().getGameProfileForUsername(getProfile().getName());
			}
			return ret == null ? profile : ret;
		}).thenAcceptAsync(gp -> {
			DummyPlayerEntity.this.dataManager.set(GAME_PROFILE, gp);
			reloadTextures();
		}, getServer());
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	protected void updateSkinTexture() {
		if (reloadTextures) {
			synchronized (this) {
				if (reloadTextures) {
					this.playerTextures.clear();
					reloadTextures = false;
					if (getProfile() == null || getProfile().getId() == null) return;
					LogManager.getLogger().info("Loading skin data for GameProfile: " + getProfile());
					Minecraft.getInstance().getSkinManager().loadProfileTextures(getProfile(), (p_210250_1_, p_210250_2_, p_210250_3_) -> {
						synchronized (playerTextures) {
							this.playerTextures.put(p_210250_1_, p_210250_2_);
							if (p_210250_1_ == Type.SKIN) {
								this.skinType = p_210250_3_.getMetadata("model");
								if (this.skinType == null) {
									this.skinType = "default";
								}
							}
						}

					}, true);
				}
			}
		}
	}

	private UUID getSkinUUID() {
		return getProfile() == null || getProfile().getId() == null ? getUniqueID() : getProfile().getId();
	}

	public ResourceLocation getSkin() {
		updateSkinTexture();
		synchronized (playerTextures) {
			return playerTextures.computeIfAbsent(Type.SKIN,
					$ -> DefaultPlayerSkin.getDefaultSkin(getSkinUUID()));
		}
	}

	public ResourceLocation getCape() {
		return playerTextures.get(Type.CAPE);
	}

	public ResourceLocation getElytra() {
		return playerTextures.getOrDefault(Type.ELYTRA, getCape());
	}

	public String getSkinType() {
		return this.skinType == null ? DefaultPlayerSkin.getSkinType(getSkinUUID()) : this.skinType;
	}

	void reloadTextures() {
		this.reloadTextures = true;
	}
}
