package com.lovetropics.extras.client.model_modifer.types;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.model_modifer.ModelApplier;
import com.lovetropics.extras.model_modifer.types.AnimationType;
import com.lovetropics.extras.model_modifer.types.UnitType;
import com.mojang.logging.LogUtils;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.AnimationState;
import net.neoforged.neoforge.client.entity.animation.json.AnimationHolder;
import net.neoforged.neoforge.client.entity.animation.json.AnimationLoader;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 * THIS IS MOSTLY A PROVE OF CONCEPT, THERE MOST LIKELY A BETTER WAY TO DO THIS, BUT THIS WORKS FOR NOW
 */
public class AnimationApplier implements ModelApplier<AnimationType> {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final WeakHashMap<EntityModel<?>, AnimationState> animationStates = new WeakHashMap<>();
    private final WeakHashMap<EntityModel<?>, KeyframeAnimation> entityAnimationStates = new WeakHashMap<>();
    private final Map<Identifier, ContextKey<Integer>> ANIMATION_KEYS = new HashMap<>();

    @Override
    public void applyToModel(AnimationType type, LivingEntityRenderState state, EntityModel<?> model) {
        AnimationDefinition definition = AnimationLoader.INSTANCE.getAnimationHolder(type.id()).getOrNull();
        if (definition == null) {
            return;
        } else if (!this.canApplyTo(definition, model)) {
            return;
        }
        AnimationState animationState1 = animationStates.computeIfAbsent(model, _ -> new AnimationState());
        animationState1.startIfStopped(state.getRenderDataOrDefault(getAnimationKey(type.id()), 0));
        KeyframeAnimation animation = entityAnimationStates.computeIfAbsent(model, entityModel -> definition.bake(entityModel.root()));

        animation.apply(animationState1, state.ageInTicks);
    }

    private ContextKey<Integer> getAnimationKey(Identifier id) {
        return ANIMATION_KEYS.computeIfAbsent(id, (identifier) -> new ContextKey<>(LTExtras.id(identifier.getNamespace() + "/" + identifier.getPath())));
    }

    private boolean canApplyTo(AnimationDefinition definition, EntityModel<?> model) {
        for (Map.Entry<String, List<AnimationChannel>> stringListEntry : definition.boneAnimations().entrySet()) {
            ModelPart root = model.root();
            if (!root.hasChild(stringListEntry.getKey())) {
                return false;
            }
        }
        return true;
    }
}
