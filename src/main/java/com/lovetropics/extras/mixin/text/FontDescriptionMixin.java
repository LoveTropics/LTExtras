package com.lovetropics.extras.mixin.text;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.text.ExtraStyleFontDescription;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.FontDescription;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(FontDescription.class)
public interface FontDescriptionMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;flatComapMap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static Codec<FontDescription> createCodec(Codec<FontDescription> instance, Function<?, ?> _to, Function<?, ?> from, Operation<Codec<FontDescription>> original) {
        Codec<FontDescription> codec = original.call(instance, _to, from);
        return ExtraStyleFontDescription.wrapCodec(codec);
    }
}
