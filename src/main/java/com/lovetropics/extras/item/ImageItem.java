package com.lovetropics.extras.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ImageItem extends Item {
    public static final List<Data> PRESETS = List.of(
    );

    private static final String TAG_IMAGE = "image";

    public ImageItem(final Properties properties) {
        super(properties);
    }

    public static Optional<Data> get(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag != null && stack.getItem() instanceof ImageItem && tag.contains(TAG_IMAGE)) {
            return Data.CODEC.parse(NbtOps.INSTANCE, tag.get(TAG_IMAGE)).result();
        }
        return Optional.empty();
    }

    public static void set(final ItemStack stack, final Data data) {
        stack.getOrCreateTag().put(TAG_IMAGE, Util.getOrThrow(Data.CODEC.encodeStart(NbtOps.INSTANCE, data), IllegalStateException::new));
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> lines, final TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        get(stack).flatMap(Data::name).ifPresent(name -> lines.add(name.copy().withStyle(ChatFormatting.GREEN)));
    }

    public record Data(Optional<Component> name, ResourceLocation texture, float width, float height) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
                ExtraCodecs.COMPONENT.optionalFieldOf("name").forGetter(Data::name),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Data::texture),
                Codec.FLOAT.fieldOf("width").forGetter(Data::width),
                Codec.FLOAT.fieldOf("height").forGetter(Data::height)
        ).apply(i, Data::new));

        public Data(final Component name, final ResourceLocation texture, final float width, final float height) {
            this(Optional.of(name), texture, width, height);
        }
    }
}
