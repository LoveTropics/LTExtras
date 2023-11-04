package com.lovetropics.extras.collectible;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.CollectiblesListPacket;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class CollectibleStore implements ICapabilitySerializable<Tag> {
    public static final ResourceLocation ID = new ResourceLocation(LTExtras.MODID, "collectibles");

    private static final Logger LOGGER = LogUtils.getLogger();

    private final LazyOptional<CollectibleStore> instance = LazyOptional.of(() -> this);

    private final ServerPlayer player;

    private final List<Collectible> collectibles = new ArrayList<>();
    private boolean hasUnseen;
    private boolean locked;

    private CollectibleStore(final ServerPlayer player) {
        this.player = player;
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof final ServerPlayer player) {
            event.addCapability(ID, new CollectibleStore(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        final CollectibleStore collectibles = getNullable(event.getEntity());
        if (collectibles != null) {
            collectibles.sendToClient(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        final Player oldPlayer = event.getOriginal();
        oldPlayer.reviveCaps();
        try {
            final CollectibleStore oldCollectibles = getNullable(oldPlayer);
            final CollectibleStore newCollectibles = getNullable(event.getEntity());

            if (oldCollectibles != null && newCollectibles != null) {
                newCollectibles.collectibles.addAll(oldCollectibles.collectibles);
                newCollectibles.sendToClient(true);
            }
        } finally {
            oldPlayer.invalidateCaps();
        }
    }

    @Nullable
    public static CollectibleStore getNullable(final Player player) {
        return player.getCapability(LTExtras.COLLECTIBLE_STORE).orElse(null);
    }

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
        return LTExtras.COLLECTIBLE_STORE.orEmpty(cap, instance);
    }

    @Override
    public Tag serializeNBT() {
        return Util.getOrThrow(CollectibleData.CODEC.encodeStart(NbtOps.INSTANCE, asData()), IllegalStateException::new);
    }

    public CollectibleData asData() {
        return new CollectibleData(collectibles, hasUnseen);
    }

    @Override
    public void deserializeNBT(final Tag nbt) {
        CollectibleData.CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(Util.prefix("Collectibles: ", LOGGER::error)).ifPresent(data -> {
            collectibles.clear();
            collectibles.addAll(data.collectibles());
            hasUnseen = data.hasUnseen();
        });
    }

    public boolean give(final Collectible collectible) {
        if (!collectibles.contains(collectible)) {
            collectibles.add(collectible);
            hasUnseen = true;
            sendToClient(false);
            return true;
        }
        return false;
    }

    public boolean clear(final Predicate<Collectible> predicate) {
        if (collectibles.removeIf(predicate)) {
            sendToClient(false);
            return true;
        }
        return false;
    }

    public boolean contains(final Collectible collectible) {
        return collectibles.contains(collectible);
    }

    public int count(final Predicate<Collectible> predicate) {
        int count = 0;
        for (final Collectible collectible : collectibles) {
            if (predicate.test(collectible)) {
                count++;
            }
        }
        return count;
    }

    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public void markSeen() {
        hasUnseen = false;
    }

    private void sendToClient(final boolean silent) {
        LTExtrasNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new CollectiblesListPacket(collectibles, silent, hasUnseen));
    }
}
