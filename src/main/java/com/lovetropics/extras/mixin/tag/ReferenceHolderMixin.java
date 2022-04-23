package com.lovetropics.extras.mixin.tag;

import com.lovetropics.extras.EverythingTag;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Set;

@Mixin(Holder.Reference.class)
public class ReferenceHolderMixin<T> {
	@Shadow
	@Final
	private Registry<T> registry;

	@Redirect(method = "bindTags", at = @At(value = "INVOKE", target = "Ljava/util/Set;copyOf(Ljava/util/Collection;)Ljava/util/Set;"))
	private Set<TagKey<T>> bindTags(final Collection<TagKey<T>> tags) {
		final ReferenceOpenHashSet<TagKey<T>> boundTags = new ReferenceOpenHashSet<>(tags);
		boundTags.add(TagKey.create(registry.key(), EverythingTag.ID));
		return boundTags;
	}
}
