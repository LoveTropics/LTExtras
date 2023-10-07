package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.collectible.Collectible;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
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

    public void update(final List<Collectible> collectibles) {
        this.collectibles = List.copyOf(collectibles);
        itemStacks = collectibles.stream().map(Collectible::createItemStack).toList();
    }

    public boolean isEmpty() {
        return collectibles.isEmpty();
    }
}
