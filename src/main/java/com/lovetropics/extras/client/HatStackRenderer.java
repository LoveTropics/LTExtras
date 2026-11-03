package com.lovetropics.extras.client;

import com.google.common.reflect.TypeToken;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.hats.StackedHatStore;
import com.lovetropics.lib.item.StackedHat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class HatStackRenderer {
    public static final ContextKey<StackRenderState> HAT_STACK = new ContextKey<>(LTExtras.id("hat_stack"));

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        ItemModelResolver itemModelResolver = Minecraft.getInstance().getItemModelResolver();
        event.registerEntityModifier(new TypeToken<LivingEntityRenderer<?, ?, ?>>() {
        }, (entity, state) -> extract(entity, state, itemModelResolver));
    }

    private static void extract(LivingEntity entity, LivingEntityRenderState state, ItemModelResolver itemModelResolver) {
        ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
        List<StackedHat> itemHats = headItem.getOrDefault(ExtraDataComponents.HAT_STACK, List.of());
        List<StackedHat> attachmentHats = StackedHatStore.getHats(entity);
        List<HatRenderState> itemStates = new ArrayList<>(itemHats.size() + attachmentHats.size());

        for (StackedHat hat : itemHats) {
            itemStates.add(getStateFromStackedHat(hat, entity, itemModelResolver));
        }

        // Render hats from attachments on top of the hats from the head item
        for (StackedHat hat : attachmentHats) {
            itemStates.add(getStateFromStackedHat(hat, entity, itemModelResolver));
        }

        state.setRenderData(HAT_STACK, new StackRenderState(itemStates, !itemHats.isEmpty()));
    }

    private static HatRenderState getStateFromStackedHat(StackedHat hat, LivingEntity entity, ItemModelResolver itemModelResolver) {
        ItemStackRenderState itemState = new ItemStackRenderState();
        itemModelResolver.updateForLiving(itemState, hat.item().create(), ItemDisplayContext.HEAD, entity);
        return new HatRenderState(
                itemState,
                hat.width(),
                hat.height(),
                hat.bounciness()
        );
    }

    /// @return `true` if the head item should be hidden
    public static boolean submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int outlineColor, LivingEntityRenderState state) {
        StackRenderState hatStack = state.getRenderData(HAT_STACK);
        if (hatStack == null || hatStack.stack.isEmpty()) {
            return false;
        }

        float bounce = Mth.sin(state.walkAnimationPos) * state.walkAnimationSpeed * 0.0625f;
        Quaternionf wobble = Axis.ZP.rotationDegrees(Mth.sin(state.walkAnimationPos * 0.5f) * state.walkAnimationSpeed * 2.0f);
        Quaternionf bend = Axis.XP.rotationDegrees(state.walkAnimationSpeed * 2.0f);

        poseStack.pushPose();
        poseStack.translate(0.0f, 0.3f, 0.0f);

        for (HatRenderState hat : hatStack.stack) {
            AABB boundingBox = hat.item.getModelBoundingBox();

            poseStack.mulPose(wobble);
            poseStack.mulPose(bend);

            float squash = (bounce * hat.bounciness) + 1.0f;
            float yScale = hat.height * squash;

            // Position each hat consistently by its base - essentially ignoring the y of the transform
            // This is particularly important because full blocks are set to fully cover the head, while top hats are set on top
            poseStack.pushPose();
            poseStack.scale(hat.width, yScale, hat.width);
            poseStack.translate(0.0f, -boundingBox.minY, 0.0f);
            hat.item.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, outlineColor);
            poseStack.popPose();

            poseStack.translate(0.0f, boundingBox.getYsize() * yScale, 0.0f);
        }

        poseStack.popPose();
        return hatStack.hideItem;
    }

    private static final DeferredHolder<EntityType<?>, ?> GIBNUT = DeferredHolder.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("tropicraft", "gibnut"));
    private static final DeferredHolder<EntityType<?>, ?> TROPIBEE = DeferredHolder.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("tropicraft", "tropibee"));

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.AddLayers event) {
        if (event.getRenderer(EntityTypes.CHICKEN) instanceof ChickenRenderer chicken) {
            chicken.addLayer(new CustomMobHatLayer<>(chicken, (poseStack, model) -> {
                model.root().translateAndRotate(poseStack);
                model.root().getChild(PartNames.HEAD).translateAndRotate(poseStack);
                poseStack.translate(0.0f, -0.215f, 0.0f);
                float scale = 0.325f;
                poseStack.scale(scale, -scale, -scale);
            }));
        }

        addCustomMobHatLayerUntyped(event, GIBNUT, (poseStack, model) -> {
            ModelPart root = model.root();
            root.translateAndRotate(poseStack);
            ModelPart body = root.getChild("body_base");
            body.translateAndRotate(poseStack);
            body.getChild("head_base").translateAndRotate(poseStack);
            poseStack.translate(0.0f, 0.07f, -0.125f);
            float scale = 0.325f;
            poseStack.scale(scale, -scale, -scale);
        });

        addCustomMobHatLayerUntyped(event, TROPIBEE, (poseStack, model) -> {
            ModelPart root = model.root();
            root.translateAndRotate(poseStack);
            ModelPart bone = root.getChild("bone");
            bone.translateAndRotate(poseStack);
            bone.getChild("body").translateAndRotate(poseStack);
            poseStack.translate(0.0f, -0.275f, 0.0f);
            float scale = 0.5f;
            poseStack.scale(scale, -scale, -scale);
        });
    }

    @SuppressWarnings("unchecked")
    private static <S extends LivingEntityRenderState> void addCustomMobHatLayerUntyped(EntityRenderersEvent.AddLayers event, Holder<EntityType<?>> entityType, HeadAttachment<EntityModel<?>> headAttachment) {
        if (entityType.isBound() && event.getRenderer(entityType.value()) instanceof LivingEntityRenderer<?, ?, ?> renderer) {
            if (renderer instanceof LivingEntityRenderer) {
                addCustomMobHatLayer((LivingEntityRenderer<?, S, ?>) renderer, headAttachment);
            }
        }
    }

    private static <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void addCustomMobHatLayer(LivingEntityRenderer<?, S, M> renderer, HeadAttachment<? super M> headAttachment) {
        renderer.addLayer(new CustomMobHatLayer<>(renderer, headAttachment));
    }

    public record StackRenderState(
            List<HatRenderState> stack,
            boolean hideItem
    ) {
    }

    private record HatRenderState(ItemStackRenderState item, float width, float height, float bounciness) {
    }

    private static class CustomMobHatLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
        private final HeadAttachment<? super M> headAttachment;

        public CustomMobHatLayer(RenderLayerParent<S, M> renderer, HeadAttachment<? super M> headAttachment) {
            super(renderer);
            this.headAttachment = headAttachment;
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot) {
            HatStackRenderer.StackRenderState stackRenderState = state.getRenderData(HatStackRenderer.HAT_STACK);
            if (state.headItem.isEmpty() && (stackRenderState == null || stackRenderState.stack.isEmpty())) {
                return;
            }

            poseStack.pushPose();

            M parentModel = getParentModel();
            headAttachment.translateToHead(poseStack, parentModel);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));

            if (!HatStackRenderer.submit(poseStack, submitNodeCollector, lightCoords, state.outlineColor, state)) {
                state.headItem.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            }

            poseStack.popPose();
        }
    }

    private interface HeadAttachment<M extends EntityModel<?>> {
        void translateToHead(PoseStack poseStack, M model);
    }
}
