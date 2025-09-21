package com.lovetropics.extras.mixin.placeholder;

import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Transformation.class)
public class TransformationMixin {
    @Mutable
    @Shadow
    @Final
    public static Codec<Transformation> CODEC;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lcom/mojang/math/Transformation;CODEC:Lcom/mojang/serialization/Codec;"))
    private static void initialize(CallbackInfo ci) {
        // Allow simpler format
        Codec<Vector3f> scaleCodec = Codec.withAlternative(ExtraCodecs.VECTOR3F, Codec.FLOAT, Vector3f::new);
        CODEC = RecordCodecBuilder.create(i -> i.group(
                ExtraCodecs.VECTOR3F.optionalFieldOf("translation", new Vector3f()).forGetter(Transformation::getTranslation),
                ExtraCodecs.QUATERNIONF.optionalFieldOf("left_rotation", new Quaternionf()).forGetter(Transformation::getLeftRotation),
                scaleCodec.fieldOf("scale").forGetter(Transformation::getScale),
                ExtraCodecs.QUATERNIONF.optionalFieldOf("right_rotation", new Quaternionf()).forGetter(Transformation::getRightRotation)
        ).apply(i, Transformation::new));
    }
}
