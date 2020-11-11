package com.lovetropics.extras;

import net.minecraft.tags.Tag;

public interface ExtendableTagCollection<T> {
	void addTag(Tag<T> tag);
}
