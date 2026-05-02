package com.lovetropics.extras.client.model_modifer;

import com.google.common.reflect.TypeToken;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.model_modifer.types.ConstantModelApplier;
import com.lovetropics.extras.client.model_modifer.types.FabulousWalkApplier;
import com.lovetropics.extras.client.model_modifer.types.FlailWalkApplier;
import com.lovetropics.extras.client.model_modifer.types.HopWalkApplier;
import com.lovetropics.extras.client.model_modifer.types.HoveringWalkApplier;
import com.lovetropics.extras.client.model_modifer.types.LongArmsApplier;
import com.lovetropics.extras.client.model_modifer.types.ScaleModelApplier;
import com.lovetropics.extras.client.model_modifer.types.ShuffleWalkApplier;
import com.lovetropics.extras.client.model_modifer.types.StiffLegsApplier;
import com.lovetropics.extras.client.model_modifer.types.UpsidedownApplier;
import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierStore;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.lovetropics.extras.model_modifer.types.OffsetType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Holder;
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
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class ModelModifierClient {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<ModelModifierType<?>, ModelApplier<?>> CLIENT_MODEL_DATA = new HashMap<>();

    public static void init() {
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.SCALE.get(), new ScaleModelApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.FABULOUS.get(), new FabulousWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.FLAIL.get(), new FlailWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.HOVERING.get(), new HoveringWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.SHUFFLE.get(), new ShuffleWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.UPSIDEDOWN.get(), new UpsidedownApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.SHRUGGY_ARMS.get(), new ShuffleWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.ENDER_ARMS.get(), new LongArmsApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.STIFF_LEGS.get(), new StiffLegsApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.HOP_WALK.get(), new HopWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.CONSTANT.get(), new ConstantModelApplier());

        ExtraModelModifierTypes.REGISTER.getEntries().forEach(entry -> {
            if (!CLIENT_MODEL_DATA.containsKey(entry.get())) {
                LOGGER.warn("No model applier registered for model modifier type: {}", entry.getKey().identifier());
            }
        });
    }

    public static final ContextKey<List<ModelModifier<?>>> MODIFIERS = new ContextKey<>(LTExtras.location("modifiers"));

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>> token = new TypeToken<>() {};

        List<ModelModifier<?>> scratchModifiers = new ArrayList<>();
        event.registerEntityModifier(token, (entity, renderState) -> {
            for (EquipmentSlot slot : EquipmentSlot.VALUES) {
                ItemStack itemStack = entity.getItemBySlot(slot);
                if (entity.getEquipmentSlotForItem(itemStack) == slot) {
                    addModifiersFromStack(scratchModifiers, itemStack);
                }
            }

            for (Holder<ModelModifier<?>> modelModifierType : ModelModifierStore.getOrDefault(entity).appliedModifiers()) {
                scratchModifiers.add(modelModifierType.value());
            }

            if (!scratchModifiers.isEmpty()) {
                List<ModelModifier<?>> modifiers = List.copyOf(scratchModifiers);
                renderState.setRenderData(MODIFIERS, modifiers);
                for (ModelModifier<?> modifier : modifiers) {
                    Applier.extract(modifier, entity, renderState);
                }
            }

            scratchModifiers.clear();
        });
    }



    private static void addModifiersFromStack(List<ModelModifier<?>> modifiers, ItemStack stack) {
        List<Holder<ModelModifier<?>>> modifierTypes = stack.getOrDefault(ExtraDataComponents.MODEL_MODIFIER, List.of());
        for (Holder<ModelModifier<?>> modifierType : modifierTypes) {
            modifiers.add(modifierType.value());
        }
        float adjustHeight = stack.getOrDefault(ExtraDataComponents.ADJUST_HEIGHT, 0.0f);
        if (adjustHeight != 0.0F) {
            modifiers.add(new OffsetType.Data(0.0f, adjustHeight * 20, 0.0f));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?, ?> event) {
        List<ModelModifier<?>> modifiers = event.getRenderState().getRenderData(MODIFIERS);
        if (modifiers != null) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            for (ModelModifier<?> modifier : modifiers) {
                Applier.applyToTransforms(modifier, poseStack, event.getRenderState());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?, ?> event) {
        List<ModelModifier<?>> modifiers = event.getRenderState().getRenderData(MODIFIERS);
        if (modifiers != null) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.popPose();
        }
    }

    public static void applyToModel(LivingEntityRenderState renderState, EntityModel<?> model) {
        List<ModelModifier<?>> renderData = renderState.getRenderData(MODIFIERS);
        if (renderData != null) {
            for (ModelModifier<?> modifier : renderData) {
                Applier.applyToModel(modifier, renderState, model);
            }
        }
    }

    static class Applier {
        static <T extends ModelModifier<?>> ModelApplier<T> forType(ModelModifierType<T> type) {
            ModelApplier<T> applier = (ModelApplier<T>) CLIENT_MODEL_DATA.get(type);
            if (applier == null) {
                throw new IllegalStateException("No model applier registered for type: " + type);
            }
            return applier;
        }

        static <T extends ModelModifier<?>> void extract(ModelModifier<T> modifier, LivingEntity livingEntity, LivingEntityRenderState state) {
            forType(modifier.type()).extractRenderState(modifier.data(), livingEntity, state);
        }

        static <T extends ModelModifier<?>> void applyToModel(ModelModifier<T> modifier, LivingEntityRenderState state, EntityModel<?> model) {
            forType(modifier.type()).applyToModel(modifier.data(), state, model);
        }

        static <T extends ModelModifier<?>> void applyToTransforms(ModelModifier<T> modifier, PoseStack poseStack, LivingEntityRenderState state) {
            forType(modifier.type()).applyToTransforms(modifier.data(), poseStack, state);
        }
    }
}
