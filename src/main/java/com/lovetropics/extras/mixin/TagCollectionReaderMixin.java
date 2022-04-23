package com.lovetropics.extras.mixin;

import com.lovetropics.extras.EverythingTag;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionReader;
import net.minecraft.util.ResourceLocation;
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

@Mixin(TagCollectionReader.class)
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
			Map<ResourceLocation, ITag.Builder> idToBuilderMap, CallbackInfoReturnable<ITagCollection<T>> ci,
			Map<ResourceLocation, ITag<T>> map, Function<ResourceLocation, ITag<T>> getTag, Function<ResourceLocation, T> getEntry
	) {
		if (this.name.equals("block")) {
			EverythingTag.addTo(map, ForgeRegistries.BLOCKS);
		} else if (this.name.equals("item")) {
			EverythingTag.addTo(map, ForgeRegistries.ITEMS);
		}
	}
}
