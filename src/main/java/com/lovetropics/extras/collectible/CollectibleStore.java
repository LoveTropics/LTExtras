package com.lovetropics.extras.collectible;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.lovetropics.extras.network.CollectiblesListPacket;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
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

    private static final Codec<List<Collectible>> CODEC = Collectible.CODEC.listOf().fieldOf("collectibles").codec();

    private final LazyOptional<CollectibleStore> instance = LazyOptional.of(() -> this);

    private final ServerPlayer player;

    private final List<Collectible> collectibles = new ArrayList<>();

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
        get(event.getEntity()).sendToClient(true);
    }

    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            final CollectibleStore oldCollectibles = getNullable(event.getOriginal());
            final CollectibleStore newCollectibles = getNullable(event.getEntity());

            if (oldCollectibles == null || newCollectibles == null) {
                return;
            }

            newCollectibles.collectibles.addAll(oldCollectibles.collectibles);
            newCollectibles.sendToClient(true);
        }
    }

    public static CollectibleStore get(final Player player) {
        return player.getCapability(LTExtras.COLLECTIBLE_STORE).orElseThrow(IllegalStateException::new);
    }

    @Nullable public static CollectibleStore getNullable(final Player player) {
        return player.getCapability(LTExtras.COLLECTIBLE_STORE).orElse(null);
    }

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
        return LTExtras.COLLECTIBLE_STORE.orEmpty(cap, instance);
    }

    @Override
    public Tag serializeNBT() {
        return Util.getOrThrow(CODEC.encodeStart(NbtOps.INSTANCE, collectibles), IllegalStateException::new);
    }

    @Override
    public void deserializeNBT(final Tag nbt) {
        CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(Util.prefix("Collectibles: ", LOGGER::error)).ifPresent(list -> {
            collectibles.clear();
            collectibles.addAll(list);
        });
    }

    public boolean give(final Collectible collectible) {
        if (!collectibles.contains(collectible)) {
            collectibles.add(collectible);
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

    private void sendToClient(final boolean silent) {
        LTExtrasNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new CollectiblesListPacket(collectibles, silent));
    }
}
