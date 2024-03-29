package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.item.CollectibleCompassItem;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public class CollectibleEntity extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String KEY_COLLECTIBLE = "collectible";
    private static final String KEY_PARTICLES = "particles";

    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(CollectibleEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> DATA_PARTICLES = SynchedEntityData.defineId(CollectibleEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private Collectible collectible;

    public CollectibleEntity(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    public ItemStack getDisplayedItem() {
        return getEntityData().get(DATA_ITEM);
    }

    public void setCollectible(@Nullable final Collectible collectible) {
        this.collectible = collectible;
        getEntityData().set(DATA_ITEM, collectible != null ? collectible.createItemStack(Util.NIL_UUID) : ItemStack.EMPTY);
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
        getEntityData().define(DATA_PARTICLES, true);
    }

    @Override
    public void playerTouch(final Player player) {
        if (!level().isClientSide && collectible != null) {
            tryGiveCollectible(player, collectible);
        }
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (collectible == null) {
            return super.interact(player, hand);
        }
        if (!level().isClientSide) {
            tryGiveCollectible(player, collectible);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public boolean skipAttackInteraction(final Entity entity) {
        if (collectible != null && entity instanceof final Player player) {
            if (!level().isClientSide) {
                tryGiveCollectible(player, collectible);
            }
            return true;
        }
        return false;
    }

    private void tryGiveCollectible(final Player player, final Collectible collectible) {
        final CollectibleStore collectibles = CollectibleStore.getNullable(player);
        if (collectibles != null && collectibles.give(collectible)) {
            recycleCollectibleCompass(player);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide() && shouldShowParticles()) {
            tickParticles();
        }
    }

    private void tickParticles() {
        final double x = getX() + random.nextGaussian() * 0.2;
        final double y = getY() + random.nextGaussian() * 0.1 + 0.1;
        final double z = getZ() + random.nextGaussian() * 0.2;
        final double speedX = random.nextGaussian() * 0.005;
        final double speedY = random.nextGaussian() * 0.005;
        final double speedZ = random.nextGaussian() * 0.005;
        level().addParticle(ParticleTypes.END_ROD, false, x, y, z, speedX, speedY, speedZ);
    }

    private void setShowParticles(final boolean particles) {
        getEntityData().set(DATA_PARTICLES, particles);
    }

    private boolean shouldShowParticles() {
        return getEntityData().get(DATA_PARTICLES);
    }

    private void recycleCollectibleCompass(final Player player) {
        final Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            final ItemStack stack = inventory.getItem(i);
            final CollectibleCompassItem.Target target = CollectibleCompassItem.getTarget(stack);
            if (target == null || !target.id().equals(getUUID())) {
                continue;
            }
            inventory.removeItemNoUpdate(i);
            final int coinCount = CollectibleCompassItem.getCoinCount(stack);
            if (coinCount > 0) {
                inventory.placeItemBackInInventory(new ItemStack(ExtraItems.TROPICOIN, coinCount));
            }
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
        if (tag.contains(KEY_PARTICLES, Tag.TAG_BYTE)) {
            setShowParticles(tag.getBoolean(KEY_PARTICLES));
        } else {
            setShowParticles(true);
        }
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag tag) {
        if (collectible != null) {
            tag.put(KEY_COLLECTIBLE, Util.getOrThrow(Collectible.CODEC.encodeStart(NbtOps.INSTANCE, collectible), IllegalStateException::new));
        }
        tag.putBoolean(KEY_PARTICLES, shouldShowParticles());
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

    @Nullable
    public Collectible getCollectible() {
        return collectible;
    }
}
