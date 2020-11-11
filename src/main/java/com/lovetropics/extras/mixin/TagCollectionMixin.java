package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtendableTagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(TagCollection.class)
public class TagCollectionMixin<T> implements ExtendableTagCollection<T> {
	private final List<Tag<T>> extend = new ArrayList<>();

	@Inject(method = "toImmutable", at = @At("HEAD"))
	private void setTags(Map<ResourceLocation, Tag<T>> tags, CallbackInfo ci) {
		for (Tag<T> tag : this.extend) {
			tags.put(tag.getId(), tag);
		}
	}

	@Override
	public void addTag(Tag<T> tag) {
		this.extend.add(tag);
	}
}
