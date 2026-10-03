package com.lovetropics.extras.client.model_modifer.types;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.AnimationType;
import net.minecraft.SharedConstants;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.entity.animation.json.AnimationLoader;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

/// LTExtras
/// FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
public class AnimationApplier implements ModelApplier<AnimationType> {
    private final LoadingCache<AnimationKey, Optional<KeyframeAnimation>> bakedAnimation = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(1))
            .build(new CacheLoader<>() {
                @Override
                public Optional<KeyframeAnimation> load(AnimationKey key) {
                    AnimationDefinition definition = AnimationLoader.INSTANCE.getAnimationHolder(key.id()).getOrNull();
                    if (definition == null) {
                        return Optional.empty();
                    }
                    Function<String, @Nullable ModelPart> partLookup = key.root().createPartLookup();
                    for (String partName : definition.boneAnimations().keySet()) {
                        if (partLookup.apply(partName) == null) {
                            return Optional.empty();
                        }
                    }
                    return Optional.of(definition.bake(key.root()));
                }
            });

    @Override
    public void applyToModel(AnimationType type, LivingEntityRenderState state, EntityModel<?> model) {
        getOrBakeAnimation(type, model).ifPresent(animation -> {
            long millisSinceStart = (long) ((double) state.ageInTicks * SharedConstants.MILLIS_PER_TICK);
            animation.apply(millisSinceStart, 1.0f);
        });
    }

    private Optional<KeyframeAnimation> getOrBakeAnimation(AnimationType type, EntityModel<?> model) {
        return bakedAnimation.getUnchecked(new AnimationKey(type.id(), model.root()));
    }

    private record AnimationKey(Identifier id, ModelPart root) {
    }
}
