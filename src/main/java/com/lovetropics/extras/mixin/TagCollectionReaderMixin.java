package com.lovetropics.extras.mixin;

import com.lovetropics.extras.EverythingTag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.function.Function;

@Mixin(TagLoader.class)
public class TagCollectionReaderMixin<T> {
	@Shadow
	@Final
	private String name;

	@Inject(
			method = "load",
			at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void buildTagCollection(
			Map<ResourceLocation, Tag.Builder> idToBuilderMap, CallbackInfoReturnable<TagCollection<T>> ci,
			Map<ResourceLocation, Tag<T>> map, Function<ResourceLocation, Tag<T>> getTag, Function<ResourceLocation, T> getEntry
	) {
		if (this.name.equals("block")) {
			EverythingTag.addTo(map, ForgeRegistries.BLOCKS);
		} else if (this.name.equals("item")) {
			EverythingTag.addTo(map, ForgeRegistries.ITEMS);
		}
	}
}
