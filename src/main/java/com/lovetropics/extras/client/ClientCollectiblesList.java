package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.screen.container.CollectibleBasketScreen;
import com.lovetropics.extras.collectible.Collectible;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ClientCollectiblesList {
    private static ClientCollectiblesList instance;

    private List<Collectible> collectibles = List.of();
    private List<ItemStack> itemStacks = List.of();
    private boolean hasUnseen;

    public static ClientCollectiblesList get() {
        return Objects.requireNonNull(instance, "Cannot get collectibles list, not currently in a world");
    }

    @Nullable
    public static ClientCollectiblesList getOrNull() {
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

    public List<Collectible> collectibles() {
        return collectibles;
    }

    public List<ItemStack> itemStacks() {
        return itemStacks;
    }

    public void update(List<Collectible> collectibles, boolean silent, boolean hasUnseen) {
        Minecraft minecraft = Minecraft.getInstance();
        List<Collectible> newCollectibles = collectibles.stream().filter(c -> !this.collectibles.contains(c)).toList();
        this.collectibles = List.copyOf(collectibles);
        UUID playerId = minecraft.player.getUUID();
        itemStacks = collectibles.stream().map(collectible -> collectible.createItemStack(playerId)).toList();
        if (!silent && !newCollectibles.isEmpty()) {
            notifyCollections(newCollectibles);
        }
        this.hasUnseen = hasUnseen;
    }

    private static void notifyCollections(List<Collectible> newCollectibles) {
        Minecraft minecraft = Minecraft.getInstance();
        for (Collectible newCollectible : newCollectibles) {
            minecraft.getToasts().addToast(new CollectibleToast(newCollectible));
        }
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f));
    }

    public static void openScreen() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new CollectibleBasketScreen(minecraft.player.getInventory()));
        get().hasUnseen = false;
    }

    public boolean isEmpty() {
        return collectibles.isEmpty();
    }

    public boolean hasUnseen() {
        return hasUnseen;
    }
}
