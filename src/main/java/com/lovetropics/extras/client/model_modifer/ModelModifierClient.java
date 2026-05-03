package com.lovetropics.extras.client.model_modifer;

import com.google.common.reflect.TypeToken;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.model_modifer.types.CompositeTypeApplier;
import com.lovetropics.extras.client.model_modifer.types.NoShadowApplier;
import com.lovetropics.extras.client.model_modifer.types.OperationApplier;
import com.lovetropics.extras.client.model_modifer.types.FabulousWalkApplier;
import com.lovetropics.extras.client.model_modifer.types.FlailWalkApplier;
import com.lovetropics.extras.client.model_modifer.types.HopWalkApplier;
import com.lovetropics.extras.client.model_modifer.types.HoveringWalkApplier;
import com.lovetropics.extras.client.model_modifer.types.LongArmsApplier;
import com.lovetropics.extras.client.model_modifer.types.OffsetApplier;
import com.lovetropics.extras.client.model_modifer.types.ScaleApplier;
import com.lovetropics.extras.client.model_modifer.types.ShuffleWalkApplier;
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
import java.util.function.Consumer;

@EventBusSubscriber(Dist.CLIENT)
public class ModelModifierClient {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<ModelModifierType<?>, ModelApplier<?>> CLIENT_MODEL_DATA = new HashMap<>();

    public static void init() {
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.SCALE.get(), new ScaleApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.FABULOUS.get(), new FabulousWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.FLAIL.get(), new FlailWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.HOVERING.get(), new HoveringWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.SHUFFLE.get(), new ShuffleWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.UPSIDEDOWN.get(), new UpsidedownApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.SHRUGGY_ARMS.get(), new ShuffleWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.ENDER_ARMS.get(), new LongArmsApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.HOP_WALK.get(), new HopWalkApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.CONSTANT.get(), new OperationApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.COMPOSITE.get(), new CompositeTypeApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.OFFSET.get(), new OffsetApplier());
        CLIENT_MODEL_DATA.put(ExtraModelModifierTypes.NO_SHADOW.get(), new NoShadowApplier());

        ExtraModelModifierTypes.REGISTER.getEntries().forEach(entry -> {
            if (!CLIENT_MODEL_DATA.containsKey(entry.get())) {
                LOGGER.error("No model applier registered for model modifier type: {}", entry.getKey().identifier());
            }
        });
    }

    public static final ContextKey<List<ModelModifier<?>>> MODIFIERS = new ContextKey<>(LTExtras.id("modifiers"));

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
            modifiers.add(new OffsetType.Modifier(0.0f, adjustHeight * 20, 0.0f));
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

    public static class Applier {

        public static <T extends ModelModifier<?>> void applyFor(T modifier, Consumer<ModelApplier<T>> consumer) {
            ModelApplier<T> applier = (ModelApplier<T>) CLIENT_MODEL_DATA.get(modifier.type());
            if (applier == null) {
                throw new IllegalStateException("No model applier registered for type: " + modifier.type());
            }
            consumer.accept(applier);
        }

        public static <T extends ModelModifier<?>> void extract(T modifier, LivingEntity livingEntity, LivingEntityRenderState state) {
            applyFor(modifier, applier -> applier.extractRenderState(modifier, livingEntity, state));
        }

        public static <T extends ModelModifier<?>> void applyToModel(T modifier, LivingEntityRenderState state, EntityModel<?> model) {
            applyFor(modifier, applier -> applier.applyToModel(modifier, state, model));
        }

        public static <T extends ModelModifier<?>> void applyToTransforms(T modifier, PoseStack poseStack, LivingEntityRenderState state) {
            applyFor(modifier, applier -> applier.applyToTransforms(modifier, poseStack, state));
        }
    }
}
