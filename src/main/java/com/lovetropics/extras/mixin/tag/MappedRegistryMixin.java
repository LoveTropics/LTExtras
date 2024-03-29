package com.lovetropics.extras.mixin.tag;

import com.lovetropics.extras.EverythingTag;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {
    @Shadow
    public abstract ResourceKey<? extends Registry<T>> key();

    @Shadow
    public abstract Stream<Holder.Reference<T>> holders();

    @Shadow
    protected abstract HolderSet.Named<T> createTag(TagKey<T> key);

    @Inject(method = "bindTags", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", ordinal = 2, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void bindTags(final Map<TagKey<T>, List<Holder<T>>> newTags, final CallbackInfo ci, final Map<Holder.Reference<T>, List<TagKey<T>>> holderToTag, final Set<TagKey<T>> undefinedTags, final Map<TagKey<T>, HolderSet.Named<T>> tmpTags) {
        final TagKey<T> everythingTag = TagKey.create(key(), EverythingTag.ID);
        tmpTags.computeIfAbsent(everythingTag, this::createTag).bind(holders().collect(Collectors.toList()));
        holders().forEach(holder -> holderToTag.get(holder).add(everythingTag));
    }
}
