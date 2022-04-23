package com.lovetropics.extras;

import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class EverythingTag<T extends IForgeRegistryEntry<T>> implements ITag.INamedTag<T> {
	public static final ResourceLocation ID = new ResourceLocation(LTExtras.MODID, "everything");

	private final IForgeRegistry<T> registry;
	private List<T> elements;

	public EverythingTag(IForgeRegistry<T> registry) {
		this.registry = registry;
	}

	@SuppressWarnings("unchecked")
	public static <T> void addTo(Map<ResourceLocation, ITag<T>> map, IForgeRegistry<?> registry) {
		map.put(ID, new EverythingTag(registry));
	}

	@Override
	public boolean contains(T entry) {
		return true;
	}

	@Override
	public List<T> getValues() {
		List<T> elements = this.elements;
		if (elements == null) {
			this.elements = elements = new ArrayList<>(this.registry.getValues());
		}
		return elements;
	}

	@Override
	public ResourceLocation getName() {
		return ID;
	}
}
