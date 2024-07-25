package com.lovetropics.extras.collectible;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.network.message.ClientboundCollectiblesListPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber(modid = LTExtras.MODID)
public class CollectibleStore implements IAttachmentSerializer<Tag, CollectibleStore> {
    public static final ResourceLocation ID = LTExtras.location("collectibles");

    private static final Logger LOGGER = LogUtils.getLogger();

    private ServerPlayer player;

    private final List<Collectible> collectibles = new ArrayList<>();
    private boolean hasUnseen;
    private boolean locked;


    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        final CollectibleStore collectibles = get(event.getEntity());
        collectibles.sendToClient(true);
    }

    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        final Player oldPlayer = event.getOriginal();
        if (event.isWasDeath()) {
            final CollectibleStore oldCollectibles = get(oldPlayer);
            final CollectibleStore newCollectibles = get(event.getEntity());

            newCollectibles.collectibles.addAll(oldCollectibles.collectibles);
            newCollectibles.sendToClient(true);
        }
    }

    public static CollectibleStore get(final Player player) {
        CollectibleStore data = player.getData(ExtraAttachments.COLLECTABLE_STORE);
        data.player = (ServerPlayer) player;
        return data;
    }

    public CollectibleData asData() {
        return new CollectibleData(collectibles, hasUnseen);
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
        if (player != null) {
            PacketDistributor.sendToPlayer(player, new ClientboundCollectiblesListPacket(collectibles, silent, hasUnseen));
        }
    }

    @Override
    public CollectibleStore read(IAttachmentHolder holder, Tag nbt, HolderLookup.Provider provider) {
        CollectibleData.CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(Util.prefix("Collectibles: ", LOGGER::error)).ifPresent(data -> {
            collectibles.clear();
            collectibles.addAll(data.collectibles());
            hasUnseen = data.hasUnseen();
        });

        return this;
    }

    @Override
    public @org.jetbrains.annotations.Nullable Tag write(CollectibleStore attachment, HolderLookup.Provider provider) {
        return CollectibleData.CODEC.encodeStart(NbtOps.INSTANCE, attachment.asData()).getOrThrow(IllegalStateException::new);
    }
}
