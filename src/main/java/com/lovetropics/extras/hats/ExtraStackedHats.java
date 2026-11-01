package com.lovetropics.extras.hats;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraItems;
import com.lovetropics.lib.item.StackedHat;
import net.minecraft.world.entity.Entity;
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
            public List<StackedHat> getEntityHats(Entity entity) {
                StackedHatStore store = StackedHatStore.getOrDefault(entity);
                return store.stackedHats();
            }

            @Override
            public void setEntityHats(Entity entity, List<StackedHat> hats) {
                StackedHatStore.setHats(entity, hats);
            }

            @Override
            public void addHatToEntity(Entity entity, StackedHat hat) {
                StackedHatStore.addHat(entity, hat);
            }

            @Override
            public void removeHatFromEntity(Entity entity, StackedHat hat) {
                StackedHatStore.removeHat(entity, hat);
            }
        });
    }
}
