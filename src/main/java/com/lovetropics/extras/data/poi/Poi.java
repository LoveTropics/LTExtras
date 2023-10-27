package com.lovetropics.extras.data.poi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.Objects;

public final class Poi {
    public static final Codec<Poi> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("name").forGetter(Poi::name),
            ExtraCodecs.COMPONENT.fieldOf("description").forGetter(Poi::description),
            ResourceLocation.CODEC.fieldOf("resourceLocation").forGetter(Poi::resourceLocation),
            GlobalPos.CODEC.fieldOf("blockPos").forGetter(Poi::globalPos),
            Codec.BOOL.fieldOf("enabled").forGetter(Poi::enabled)
    ).apply(i, Poi::new));

    private final String name;
    private final Component description;
    private final ResourceLocation resourceLocation;
    private final GlobalPos globalPos;
    private boolean enabled;

    public Poi(
            String name,
            Component description,
            ResourceLocation resourceLocation,
            GlobalPos globalPos,
            boolean enabled) {
        this.name = name;
        this.description = description;
        this.resourceLocation = resourceLocation;
        this.globalPos = globalPos;
        this.enabled = enabled;
    }

    //Use only the name for equals&hashCode. Maybe tiny bit risky but dupe handling is free
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poi poi = (Poi) o;
        return Objects.equals(name, poi.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String name() {
        return name;
    }

    public Component description() {
        return description;
    }

    public ResourceLocation resourceLocation() {
        return resourceLocation;
    }

    public GlobalPos globalPos() {
        return globalPos;
    }

    public boolean enabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "Poi[" +
                "name=" + name + ", " +
                "description=" + description + ", " +
                "resourceLocation=" + resourceLocation + ", " +
                "globalPos=" + globalPos + ", " +
                "enabled=" + enabled + ']';
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
