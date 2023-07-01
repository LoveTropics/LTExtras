package com.lovetropics.extras.mixin.tag;

import com.lovetropics.extras.EverythingTag;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Final;
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

@Mixin(targets = "net/minecraftforge/registries/NamespacedWrapper")
public abstract class NamespacedWrapperMixin<T> {
    @Shadow
    @Final
    private ForgeRegistry<T> delegate;

    @Shadow
    protected abstract HolderSet.Named<T> createTag(TagKey<T> name);

    @Shadow
    public abstract Stream<Holder.Reference<T>> holders();

    @Inject(method = "bindTags", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", ordinal = 2, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void bindTags(final Map<TagKey<T>, List<Holder<T>>> newTags, final CallbackInfo ci, final Map<Holder.Reference<T>, List<TagKey<T>>> holderToTag, final Set<TagKey<T>> undefinedTags, final Map<TagKey<T>, HolderSet.Named<T>> tmpTags, final Set<TagKey<T>> defaultedTags) {
        final TagKey<T> everythingTag = TagKey.create(delegate.getRegistryKey(), EverythingTag.ID);
        tmpTags.computeIfAbsent(everythingTag, this::createTag).bind(holders().collect(Collectors.toList()));
        holders().forEach(holder -> holderToTag.get(holder).add(everythingTag));
    }
}
