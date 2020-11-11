package com.lovetropics.extras;

import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;

public final class EverythingTag<T extends IForgeRegistryEntry<T>> extends Tag<T> {
	public static final ResourceLocation ID = new ResourceLocation(LTExtras.MODID, "everything");

	private final IForgeRegistry<T> registry;

	public EverythingTag(IForgeRegistry<T> registry) {
		super(ID);
		this.registry = registry;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IForgeRegistryEntry<T>> void addTo(TagCollection<T> collection, IForgeRegistry<T> registry) {
		((ExtendableTagCollection<T>) collection).addTag(new EverythingTag<>(registry));
	}

	@Override
	public boolean contains(T entry) {
		return true;
	}

	@Override
	public Collection<T> getAllElements() {
		return this.registry.getValues();
	}
}
