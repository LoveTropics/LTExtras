package com.lovetropics.extras.model_modifer.types.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;

import java.util.Optional;

public record ModelPartData(
        Optional<Float> x,
        Optional<Float> y,
        Optional<Float> z,
        Optional<Float> xRot,
        Optional<Float> yRot,
        Optional<Float> zRot,
        Optional<Float> xScale,
        Optional<Float> yScale,
        Optional<Float> zScale
) {

    public static Codec<Float> FLOAT_DEGREES = Codec.FLOAT.xmap(f -> (float) Math.toRadians(f), f -> (float) Math.toDegrees(f));

    public static final Codec<ModelPartData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("x").forGetter(ModelPartData::x),
            Codec.FLOAT.optionalFieldOf("y").forGetter(ModelPartData::y),
            Codec.FLOAT.optionalFieldOf("z").forGetter(ModelPartData::z),
            FLOAT_DEGREES.optionalFieldOf("xRot").forGetter(ModelPartData::xRot),
            FLOAT_DEGREES.optionalFieldOf("yRot").forGetter(ModelPartData::yRot),
            FLOAT_DEGREES.optionalFieldOf("zRot").forGetter(ModelPartData::zRot),
            Codec.FLOAT.optionalFieldOf("xScale").forGetter(ModelPartData::xScale),
            Codec.FLOAT.optionalFieldOf("yScale").forGetter(ModelPartData::yScale),
            Codec.FLOAT.optionalFieldOf("zScale").forGetter(ModelPartData::zScale)
    ).apply(instance, ModelPartData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModelPartData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::x,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::y,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::z,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::xRot,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::yRot,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::zRot,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::xScale,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::yScale,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::zScale,
            ModelPartData::new);


    @NullUnmarked
    public static class Builder {

        private Float x = null;
        private Float y = null;
        private Float z = null;
        private Float xRot = null;
        private Float yRot = null;
        private Float zRot = null;
        private Float xScale = null;
        private Float yScale = null;
        private Float zScale = null;

        public static Builder builder() {
            return new Builder();
        }

        public Builder x(float x) {
            this.x = x;
            return this;
        }

        public Builder y(float y) {
            this.y = y;
            return this;
        }

        public Builder z(float z) {
            this.z = z;
            return this;
        }

        public Builder position(float x, float y, float z) {
            return this.x(x).y(y).z(z);
        }

        public Builder xRot(float xRot) {
            this.xRot = (float) Math.toRadians(xRot);
            return this;
        }

        public Builder yRot(float yRot) {
            this.yRot = (float) Math.toRadians(yRot);
            return this;
        }

        public Builder zRot(float zRot) {
            this.zRot = (float) Math.toRadians(zRot);
            return this;
        }

        public Builder rotation(float xRot, float yRot, float zRot) {
            return this.xRot(xRot).yRot(yRot).zRot(zRot);
        }

        public Builder xScale(float xScale) {
            this.xScale = xScale;
            return this;
        }

        public Builder yScale(float yScale) {
            this.yScale = yScale;
            return this;
        }

        public Builder zScale(float zScale) {
            this.zScale = zScale;
            return this;
        }

        public Builder scale(float xScale, float yScale, float zScale) {
            return this.xScale(xScale).yScale(yScale).zScale(zScale);
        }

        public ModelPartData build() {
            return new ModelPartData(
                    Optional.ofNullable(x),
                    Optional.ofNullable(y),
                    Optional.ofNullable(z),
                    Optional.ofNullable(xRot),
                    Optional.ofNullable(yRot),
                    Optional.ofNullable(zRot),
                    Optional.ofNullable(xScale),
                    Optional.ofNullable(yScale),
                    Optional.ofNullable(zScale)
            );
        }
    }
}
