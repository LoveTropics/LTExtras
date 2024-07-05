package com.lovetropics.extras.data.poi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Poi {
    public static final Codec<Poi> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("name").forGetter(Poi::name),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Poi::description),
            ResourceLocation.CODEC.fieldOf("resourceLocation").forGetter(Poi::resourceLocation),
            GlobalPos.CODEC.fieldOf("blockPos").forGetter(Poi::globalPos),
            Codec.BOOL.fieldOf("enabled").forGetter(Poi::enabled)
    ).apply(i, Poi::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Poi> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(50), Poi::name,
            ComponentSerialization.STREAM_CODEC, Poi::description,
            ResourceLocation.STREAM_CODEC, Poi::resourceLocation,
            GlobalPos.STREAM_CODEC, Poi::globalPos,
            ByteBufCodecs.BOOL, Poi::enabled,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), Poi::faces,
            Poi::new
    );

    private final String name;
    private final Component description;
    private final ResourceLocation resourceLocation;
    private final GlobalPos globalPos;
    private boolean enabled;
    private List<UUID> faces;

    public Poi(
            String name,
            Component description,
            ResourceLocation resourceLocation,
            GlobalPos globalPos,
            boolean enabled,
            List<UUID> faces) {
        this.name = name;
        this.description = description;
        this.resourceLocation = resourceLocation;
        this.globalPos = globalPos;
        this.enabled = enabled;
        this.faces = new ArrayList<>(faces);
    }

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
        this.faces = new ArrayList<>();
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

    public List<UUID> faces() {
        return List.copyOf(faces);
    }

    public boolean hasFaces() {
        return !faces.isEmpty();
    }

    public Poi clearFaces() {
        faces.clear();
        return this;
    }

    public void addFace(UUID face) {
        faces.add(face);
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

    public void removeFace(final UUID face) {
        faces.remove(face);
    }
}
