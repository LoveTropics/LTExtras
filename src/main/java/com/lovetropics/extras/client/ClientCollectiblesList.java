package com.lovetropics.extras.client;

import com.google.common.collect.ImmutableList;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.client.screen.container.CollectibleBasketScreen;
import com.lovetropics.extras.collectible.Collectible;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@EventBusSubscriber(Dist.CLIENT)
public class ClientCollectiblesList {
    private static @Nullable ClientCollectiblesList instance;

    private List<Entry> entries = List.of();
    private boolean hasUnseen;

    public static ClientCollectiblesList get() {
        return Objects.requireNonNull(instance, "Cannot get collectibles list, not currently in a world");
    }

    public static @Nullable ClientCollectiblesList getOrNull() {
        return instance;
    }

    @SubscribeEvent
    public static void onLogIn(ClientPlayerNetworkEvent.LoggingIn event) {
        instance = new ClientCollectiblesList();
    }

    @SubscribeEvent
    public static void onLogOut(ClientPlayerNetworkEvent.LoggingOut event) {
        instance = null;
    }

    public List<Entry> entries() {
        return entries;
    }

    public void update(List<Holder<Collectible>> collectibles, List<Holder<Collectible>> lockedCollectibles, boolean silent, boolean hasUnseen) {
        Set<Holder<Collectible>> oldCollectibles = entries.stream()
                .filter(e -> !e.locked)
                .map(e -> e.collectible)
                .collect(Collectors.toSet());
        List<Holder<Collectible>> newCollectibles = collectibles.stream()
                .filter(c -> !oldCollectibles.contains(c))
                .toList();

        UUID playerId = Minecraft.getInstance().player.getUUID();
        ImmutableList.Builder<Entry> newEntries = ImmutableList.builder();
        for (Holder<Collectible> collectible : collectibles) {
            newEntries.add(new Entry(collectible, Collectible.createItemStack(collectible, playerId), false));
        }
        for (Holder<Collectible> lockedCollectible : lockedCollectibles) {
            ItemStack itemStack = Collectible.createItemStack(lockedCollectible, playerId);
            lockedCollectible.value().lock().ifPresent(l -> itemStack.set(ExtraDataComponents.COLLECTIBLE_LOCK, l));
            newEntries.add(new Entry(lockedCollectible, itemStack, true));
        }
        entries = newEntries.build();

        if (!silent && !newCollectibles.isEmpty()) {
            notifyCollections(newCollectibles);
        }
        this.hasUnseen = hasUnseen;
    }

    private static void notifyCollections(List<Holder<Collectible>> newCollectibles) {
        Minecraft minecraft = Minecraft.getInstance();
        for (Holder<Collectible> newCollectible : newCollectibles) {
            minecraft.gui.toastManager().addToast(new CollectibleToast(newCollectible));
        }
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f));
    }

    public static void openScreen() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreenAndShow(new CollectibleBasketScreen(minecraft.player.getInventory()));
        get().hasUnseen = false;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public boolean hasUnseen() {
        return hasUnseen;
    }

    public record Entry(
            Holder<Collectible> collectible,
            ItemStack itemStack,
            boolean locked
    ) {
    }
}
