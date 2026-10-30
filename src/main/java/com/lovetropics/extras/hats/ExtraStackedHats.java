package com.lovetropics.extras.hats;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraItems;
import com.lovetropics.lib.item.StackedHat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ExtraStackedHats
{
    public static void init() {
        StackedHat.bindAccess(new StackedHat.Access() {
            @Override
            public ItemStack create(List<StackedHat> hats) {
                ItemStack item = new ItemStack(ExtraItems.STACK_OF_HATS.get());
                item.set(ExtraDataComponents.HAT_STACK, List.copyOf(hats));
                return item;
            }

            @Override
            public void set(ItemStack item, List<StackedHat> hats) {
                item.set(ExtraDataComponents.HAT_STACK, List.copyOf(hats));
            }

            @Override
            public List<StackedHat> get(ItemStack item) {
                return item.getOrDefault(ExtraDataComponents.HAT_STACK, List.of());
            }

            @Override
            public List<StackedHat> getPlayersHats(Player serverPlayer) {
                StackedHatStore store = StackedHatStore.getOrDefault(serverPlayer);
                return store.stackedHats();
            }

            @Override
            public void setPlayersHats(Player serverPlayer, List<StackedHat> hats) {
                StackedHatStore.setHats(serverPlayer, hats);
            }

            @Override
            public void addHatToPlayer(Player player, StackedHat hat) {
                StackedHatStore.addHat(player, hat);
            }

            @Override
            public void removeHatFromPlayer(Player player, StackedHat hat) {
                StackedHatStore.removeHat(player, hat);
            }
        });
    }
}
