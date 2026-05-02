package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import org.jspecify.annotations.Nullable;

/**
 * Provides data for custom walking sounds.
 * <p>
 * Used as an item component
 *
 * @see ExtraDataComponents
 */
public record WalkSound(
        Holder<SoundEvent> soundEvent,
        FloatProvider cooldown,
        FloatProvider volume,
        FloatProvider pitch,
        boolean playOtherSounds
) {
    private static final FloatProvider DEFAULT_COOLDOWN = ConstantFloat.of(1F);
    private static final FloatProvider DEFAULT_VOLUME = ConstantFloat.of(.15F);
    private static final FloatProvider DEFAULT_PITCH = ConstantFloat.of(1F);
    private static final boolean DEFAULT_PLAY_OTHER_SOUNDS = false;

    public static final Codec<WalkSound> CODEC = RecordCodecBuilder.create(i -> i.group(
            SoundEvent.CODEC.fieldOf("sound").forGetter(WalkSound::soundEvent),
            FloatProviders.CODEC.optionalFieldOf("cooldown", DEFAULT_COOLDOWN).forGetter(WalkSound::cooldown),
            FloatProviders.CODEC.optionalFieldOf("volume", DEFAULT_VOLUME).forGetter(WalkSound::volume),
            FloatProviders.CODEC.optionalFieldOf("pitch", DEFAULT_PITCH).forGetter(WalkSound::pitch),
            Codec.BOOL.optionalFieldOf("play_other_sounds", DEFAULT_PLAY_OTHER_SOUNDS).forGetter(WalkSound::playOtherSounds)
    ).apply(i, WalkSound::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WalkSound> STREAM_CODEC = StreamCodec.composite(
            SoundEvent.STREAM_CODEC, WalkSound::soundEvent,
            ByteBufCodecs.fromCodec(FloatProviders.CODEC), WalkSound::cooldown,
            ByteBufCodecs.fromCodec(FloatProviders.CODEC), WalkSound::volume,
            ByteBufCodecs.fromCodec(FloatProviders.CODEC), WalkSound::pitch,
            ByteBufCodecs.fromCodec(Codec.BOOL), WalkSound::playOtherSounds,
            WalkSound::new
    );

    public WalkSound(Holder<SoundEvent> soundEvent) {
        this(soundEvent, DEFAULT_COOLDOWN, DEFAULT_VOLUME, DEFAULT_PITCH, DEFAULT_PLAY_OTHER_SOUNDS);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private @Nullable Holder<SoundEvent> soundEvent = null;
        private FloatProvider cooldown = DEFAULT_COOLDOWN;
        private FloatProvider volume = DEFAULT_VOLUME;
        private FloatProvider pitch = DEFAULT_PITCH;
        private boolean playOtherSounds = DEFAULT_PLAY_OTHER_SOUNDS;

        public Builder soundEvent(Holder<SoundEvent> soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

        public Builder cooldown(FloatProvider cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public Builder cooldown(float cooldown) {
            return cooldown(ConstantFloat.of(cooldown));
        }

        public Builder volume(FloatProvider volume) {
            this.volume = volume;
            return this;
        }

        public Builder volume(float volume) {
            return volume(ConstantFloat.of(volume));
        }

        public Builder pitch(FloatProvider pitch) {
            this.pitch = pitch;
            return this;
        }

        public Builder pitch(float pitch) {
            return pitch(ConstantFloat.of(pitch));
        }

        public Builder playOtherSounds(boolean playOtherSounds) {
            this.playOtherSounds = playOtherSounds;
            return this;
        }

        public WalkSound build() {
            if (soundEvent == null) {
                throw new IllegalStateException("Sound event must be set");
            }
            return new WalkSound(soundEvent, cooldown, volume, pitch, playOtherSounds);
        }
    }
}
