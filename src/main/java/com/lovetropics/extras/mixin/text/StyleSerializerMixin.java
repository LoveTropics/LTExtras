package com.lovetropics.extras.mixin.text;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.text.ExtraStyle;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(Style.Serializer.class)
public class StyleSerializerMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"))
    private static MapCodec<Style> createCodec(Function<?, ?> builder, Operation<MapCodec<Style>> original) {
        return ExtraStyle.wrapCodec(original.call(builder));
    }
}
