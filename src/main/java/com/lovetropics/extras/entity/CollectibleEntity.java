package com.lovetropics.extras.entity;

import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.UUID;

public class CollectibleEntity extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String KEY_COLLECTIBLE = "collectible";

    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(CollectibleEntity.class, EntityDataSerializers.ITEM_STACK);

    @Nullable
    private Collectible collectible;

    @Nullable
    private UUID lastGrantedPlayerId;

    public CollectibleEntity(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    public ItemStack getDisplayedItem() {
        return getEntityData().get(DATA_ITEM);
    }

    public void setCollectible(@Nullable final Collectible collectible) {
        this.collectible = collectible;
        getEntityData().set(DATA_ITEM, collectible != null ? collectible.createItemStack() : ItemStack.EMPTY);
        lastGrantedPlayerId = null;
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void playerTouch(final Player player) {
        if (!level().isClientSide && collectible != null) {
            if (player.getUUID().equals(lastGrantedPlayerId)) {
                return;
            }
            final CollectibleStore collectibles = CollectibleStore.get(player);
            collectibles.give(collectible);
            lastGrantedPlayerId = player.getUUID();
        }
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag tag) {
        if (tag.contains(KEY_COLLECTIBLE)) {
            Collectible.CODEC.parse(NbtOps.INSTANCE, tag.get(KEY_COLLECTIBLE))
                    .resultOrPartial(Util.prefix("Collectible: ", LOGGER::error))
                    .ifPresent(this::setCollectible);
        } else {
            setCollectible(null);
        }
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag tag) {
        if (collectible != null) {
            tag.put(KEY_COLLECTIBLE, Util.getOrThrow(Collectible.CODEC.encodeStart(NbtOps.INSTANCE, collectible), IllegalStateException::new));
        }
    }

    @Override
    public Component getDisplayName() {
        final ItemStack displayedItem = getDisplayedItem();
        if (displayedItem != null && !hasCustomName()) {
            return displayedItem.getHoverName();
        }
        return super.getDisplayName();
    }

    @Override
    public boolean shouldShowName() {
        return getDisplayName() != null || super.shouldShowName();
    }

    @Override
    public boolean isPickable() {
        return true;
    }
}
