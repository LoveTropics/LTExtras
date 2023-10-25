package com.lovetropics.extras.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ImageData(Optional<Component> name, ResourceLocation texture, float width, float height) {
    public static final Codec<ImageData> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.COMPONENT.optionalFieldOf("name").forGetter(ImageData::name),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ImageData::texture),
            Codec.FLOAT.fieldOf("width").forGetter(ImageData::width),
            Codec.FLOAT.fieldOf("height").forGetter(ImageData::height)
    ).apply(i, ImageData::new));

    private static final String TAG_IMAGE = "image";

    public ImageData(final Component name, final ResourceLocation texture, final float width, final float height) {
        this(Optional.of(name), texture, width, height);
    }

    public static Optional<ImageData> get(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_IMAGE)) {
            return ImageData.CODEC.parse(NbtOps.INSTANCE, tag.get(TAG_IMAGE)).result();
        }
        return Optional.empty();
    }

    public static void set(final ItemStack stack, final ImageData data) {
        stack.getOrCreateTag().put(TAG_IMAGE, Util.getOrThrow(ImageData.CODEC.encodeStart(NbtOps.INSTANCE, data), IllegalStateException::new));
    }
}
