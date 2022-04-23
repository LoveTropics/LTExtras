package com.lovetropics.extras;

import com.google.common.base.Preconditions;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface NamedSupplier<T extends IForgeRegistryEntry<T>> extends NonNullSupplier<T> {
    static <T extends IForgeRegistryEntry<T>> NamedSupplier<T> of(T entry) {
        return NamedSupplier.of(entry.getRegistryName(), () -> entry);
    }

    static <T extends IForgeRegistryEntry<T>> NamedSupplier<T> of(RegistryObject<T> object) {
        return NamedSupplier.of(object.getId(), object::get);
    }

    static <T extends IForgeRegistryEntry<T>> NamedSupplier<T> of(IForgeRegistry<T> registry, ResourceLocation id) {
        return NamedSupplier.of(id, () -> {
            T value = registry.getValue(id);
            return Preconditions.checkNotNull(value, "missing value for " + id + " in " + registry.getRegistryName());
        });
    }

    static <T extends IForgeRegistryEntry<T>> NamedSupplier<T> of(RegistryEntry<T> entry) {
        return NamedSupplier.of(entry.getId(), entry);
    }

    static <T extends IForgeRegistryEntry<T>> NamedSupplier<T> of(ResourceLocation id, NonNullSupplier<T> supplier) {
        return new NamedSupplier<T>() {
            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public T get() {
                return supplier.get();
            }

            @Override
            public int hashCode() {
                return id.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof NamedSupplier) {
                    ResourceLocation otherId = ((NamedSupplier<?>) obj).getId();
                    return this.getId().equals(otherId);
                }
                return false;
            }
        };
    }

    ResourceLocation getId();

    @Override
    T get();
}
