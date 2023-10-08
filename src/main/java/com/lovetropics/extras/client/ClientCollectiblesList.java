package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.screen.container.CollectibleBasketScreen;
import com.lovetropics.extras.collectible.Collectible;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ClientCollectiblesList {
    private static ClientCollectiblesList instance;

    private List<Collectible> collectibles = List.of();
    private List<ItemStack> itemStacks = List.of();

    public static ClientCollectiblesList get() {
        return Objects.requireNonNull(instance, "Cannot get collectibles list, not currently in a world");
    }

    @SubscribeEvent
    public static void onLogIn(final ClientPlayerNetworkEvent.LoggingIn event) {
        instance = new ClientCollectiblesList();
    }

    @SubscribeEvent
    public static void onLogOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        instance = null;
    }

    public List<Collectible> collectibles() {
        return collectibles;
    }

    public List<ItemStack> itemStacks() {
        return itemStacks;
    }

    public void update(final List<Collectible> collectibles, final boolean silent) {
        final List<Collectible> newCollectibles = collectibles.stream().filter(c -> !this.collectibles.contains(c)).toList();
        this.collectibles = List.copyOf(collectibles);
        itemStacks = collectibles.stream().map(Collectible::createItemStack).toList();
        if (!silent && !newCollectibles.isEmpty()) {
            notifyCollections(newCollectibles);
        }
    }

    private static void notifyCollections(final List<Collectible> newCollectibles) {
        final Minecraft minecraft = Minecraft.getInstance();
        for (final Collectible newCollectible : newCollectibles) {
            minecraft.getToasts().addToast(new CollectibleToast(newCollectible));
        }
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f));
    }

    public static void openScreen(final Player player) {
        Minecraft.getInstance().setScreen(new CollectibleBasketScreen(player.getInventory()));
    }

    public boolean isEmpty() {
        return collectibles.isEmpty();
    }
}
