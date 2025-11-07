package com.lovetropics.extras.client.model_modifer;

import com.google.common.reflect.TypeToken;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.model_modifer.types.FlailWalkModifier;
import com.lovetropics.extras.client.model_modifer.types.HoveringWalkModifier;
import com.lovetropics.extras.client.model_modifer.types.ShuffleWalkModifier;
import com.lovetropics.extras.client.model_modifer.types.UpsidedownModifier;
import com.lovetropics.extras.model_modifer.ModelModifierStore;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class ModelModifierClient {

    private static final Map<ModelModifierType, ModelModifier> CLIENT_MODEL_DATA = Map.of(
            ModelModifierType.DEFAULT, ModelModifier.NO_OP,
            ModelModifierType.FLAIL, new FlailWalkModifier(),
            ModelModifierType.HOVERING, new HoveringWalkModifier(),
            ModelModifierType.SHUFFLE, new ShuffleWalkModifier(),
            ModelModifierType.UPSIDEDOWN, new UpsidedownModifier()
    );

    public static final ContextKey<List<ModelModifier>> MODIFIERS = new ContextKey<>(LTExtras.location("modifiers"));

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>> token = new TypeToken<>() {};
        event.registerEntityModifier(token, (livingEntity, livingEntityRenderState) -> {
            if (livingEntityRenderState instanceof HumanoidRenderState renderState) {
                List<ModelModifier> modifiers = new ArrayList<>();

                addModifierFromStack(modifiers, renderState.headEquipment);
                addModifierFromStack(modifiers, renderState.chestEquipment);
                addModifierFromStack(modifiers, renderState.legsEquipment);
                addModifierFromStack(modifiers, renderState.feetEquipment);

                List<ModelModifierType> modelModifierTypes = ModelModifierStore.getOrDefault(livingEntity).appliedModifiers();
                for (ModelModifierType modelModifierType : modelModifierTypes) {
                    ModelModifier modifier = CLIENT_MODEL_DATA.get(modelModifierType);
                    if (modifier != null) {
                        modifiers.add(modifier);
                    }
                }

                livingEntityRenderState.setRenderData(MODIFIERS, modifiers);

                for (ModelModifier modifier : modifiers) {
                    modifier.preApply(livingEntity, renderState);
                }
            }

        });
    }

    private static void addModifierFromStack(List<ModelModifier> modifiers, ItemStack stack) {
        ModelModifierType modelModifierType = stack.get(ExtraDataComponents.WALK_ANIMATION);
        if (modelModifierType != null) {
            ModelModifier modifier = CLIENT_MODEL_DATA.get(modelModifierType);
            if (modifier != null) {
                modifiers.add(modifier);
            }
        }
    }

}
