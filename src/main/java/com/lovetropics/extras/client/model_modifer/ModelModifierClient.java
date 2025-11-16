package com.lovetropics.extras.client.model_modifer;

import com.google.common.reflect.TypeToken;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.model_modifer.types.FabulousWalkModifier;
import com.lovetropics.extras.client.model_modifer.types.FlailWalkModifier;
import com.lovetropics.extras.client.model_modifer.types.HopWalkModifier;
import com.lovetropics.extras.client.model_modifer.types.HoveringWalkModifier;
import com.lovetropics.extras.client.model_modifer.types.LongArmsModifier;
import com.lovetropics.extras.client.model_modifer.types.OffsetModifier;
import com.lovetropics.extras.client.model_modifer.types.ScaleModifier;
import com.lovetropics.extras.client.model_modifer.types.ShuffleWalkModifier;
import com.lovetropics.extras.client.model_modifer.types.SmallArmsModifier;
import com.lovetropics.extras.client.model_modifer.types.StiffLegsModifier;
import com.lovetropics.extras.client.model_modifer.types.UpsidedownModifier;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.model_modifer.ModelModifierStore;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class ModelModifierClient {
    private static final Map<ModelModifierType, ModelModifier> CLIENT_MODEL_DATA = new HashMap<>();

    static {
        CLIENT_MODEL_DATA.put(ModelModifierType.DEFAULT, ModelModifier.NO_OP);
        CLIENT_MODEL_DATA.put(ModelModifierType.FABULOUS, new FabulousWalkModifier());
        CLIENT_MODEL_DATA.put(ModelModifierType.FLAIL, new FlailWalkModifier());
        CLIENT_MODEL_DATA.put(ModelModifierType.HOVERING, new HoveringWalkModifier());
        CLIENT_MODEL_DATA.put(ModelModifierType.SHUFFLE, new ShuffleWalkModifier());
        CLIENT_MODEL_DATA.put(ModelModifierType.UPSIDEDOWN, new UpsidedownModifier());
        CLIENT_MODEL_DATA.put(ModelModifierType.SHRUNK, new ScaleModifier(1.0f, 0.8f, 1.0f));
        CLIENT_MODEL_DATA.put(ModelModifierType.ENLARGED, new ScaleModifier(1.0f, 1.2f, 1.0f));
        CLIENT_MODEL_DATA.put(ModelModifierType.SMALL_ARMS, new SmallArmsModifier());
        CLIENT_MODEL_DATA.put(ModelModifierType.LONG_ARMS, new LongArmsModifier());
        CLIENT_MODEL_DATA.put(ModelModifierType.STIFF_LEGS, new StiffLegsModifier());
        CLIENT_MODEL_DATA.put(ModelModifierType.HOP_WALK, new HopWalkModifier());
    }

    public static final ContextKey<List<ModelModifier>> MODIFIERS = new ContextKey<>(LTExtras.location("modifiers"));

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>> token = new TypeToken<>() {};

        List<ModelModifier> scratchModifiers = new ArrayList<>();
        event.registerEntityModifier(token, (entity, renderState) -> {
            for (EquipmentSlot slot : EquipmentSlot.VALUES) {
                ItemStack itemStack = entity.getItemBySlot(slot);
                if (entity.getEquipmentSlotForItem(itemStack) == slot) {
                    addModifiersFromStack(scratchModifiers, itemStack);
                }
            }

            for (ModelModifierType modelModifierType : ModelModifierStore.getOrDefault(entity).appliedModifiers()) {
                ModelModifier modifier = CLIENT_MODEL_DATA.get(modelModifierType);
                if (modifier != null) {
                    scratchModifiers.add(modifier);
                }
            }

            if (!scratchModifiers.isEmpty()) {
                List<ModelModifier> modifiers = List.copyOf(scratchModifiers);
                renderState.setRenderData(MODIFIERS, modifiers);
                for (ModelModifier modifier : modifiers) {
                    modifier.extractRenderState(entity, renderState);
                }
            }

            scratchModifiers.clear();
        });
    }

    private static void addModifiersFromStack(List<ModelModifier> modifiers, ItemStack stack) {
        List<ModelModifierType> modifierTypes = stack.getOrDefault(ExtraDataComponents.WALK_ANIMATION, List.of());
        for (ModelModifierType modifierType : modifierTypes) {
            ModelModifier modifier = CLIENT_MODEL_DATA.get(modifierType);
            if (modifier != null) {
                modifiers.add(modifier);
            }
        }
        float adjustHeight = stack.getOrDefault(ExtraDataComponents.ADJUST_HEIGHT, 0.0f);
        if (adjustHeight != 0.0F) {
            modifiers.add(new OffsetModifier(0.0f, adjustHeight * 20, 0.0f));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?, ?> event) {
        List<ModelModifier> modifiers = event.getRenderState().getRenderData(MODIFIERS);
        if (modifiers != null) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            for (ModelModifier modifier : modifiers) {
                modifier.applyToTransforms(poseStack, event.getRenderState());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?, ?> event) {
        List<ModelModifier> modifiers = event.getRenderState().getRenderData(MODIFIERS);
        if (modifiers != null) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.popPose();
        }
    }

    public static void applyToModel(LivingEntityRenderState renderState, EntityModel<?> model) {
        List<ModelModifier> renderData = renderState.getRenderData(MODIFIERS);
        if (renderData != null) {
            for (ModelModifier modifier : renderData) {
                modifier.applyToModel(renderState, model);
            }
        }
    }
}
