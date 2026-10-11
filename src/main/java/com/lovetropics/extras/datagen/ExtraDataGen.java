package com.lovetropics.extras.datagen;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ExtraModelModifiers;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.types.AnimationType;
import com.lovetropics.extras.model_modifer.types.CompositeType;
import com.lovetropics.extras.model_modifer.types.HopWalkType;
import com.lovetropics.extras.model_modifer.types.OperationType;
import com.lovetropics.extras.model_modifer.types.ScaleType;
import com.lovetropics.extras.model_modifer.types.data.Operation;
import com.lovetropics.extras.registry.ExtraRegistries;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(Dist.CLIENT)
public class ExtraDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(new RegistrySetBuilder()
                .add(ExtraRegistries.MODEL_MODIFIER, ExtraDataGen::generateModelModifiers));
        event.createProvider(AttachmentTypeTags::new);
    }

    private static void generateModelModifiers(BootstrapContext<ModelModifier> context) {
        context.register(ExtraModelModifiers.FABULOUS, ExtraModelModifierTypes.FABULOUS.get());
        context.register(ExtraModelModifiers.FLAIL, ExtraModelModifierTypes.FLAIL.get());
        context.register(ExtraModelModifiers.SHUFFLE, ExtraModelModifierTypes.SHUFFLE.get());
        context.register(ExtraModelModifiers.UPSIDEDOWN, ExtraModelModifierTypes.UPSIDEDOWN.get());
        context.register(ExtraModelModifiers.SHRUGGY_ARMS, ExtraModelModifierTypes.SHRUGGY_ARMS.get());
        context.register(ExtraModelModifiers.ENDER_ARMS, ExtraModelModifierTypes.ENDER_ARMS.get());
        context.register(ExtraModelModifiers.HOP_WALK, new HopWalkType());

        context.register(ExtraModelModifiers.SHRUNK, new ScaleType(1.0f, 0.8f, 1.0f));
        context.register(ExtraModelModifiers.ENLARGED, new ScaleType(1.0f, 1.2f, 1.0f));

        context.register(ExtraModelModifiers.STIFF_LEGS, OperationType.Builder.builder(Operation.SET)
                .part(PartNames.LEFT_LEG, builder -> builder.xRot(0))
                .part(PartNames.RIGHT_LEG, builder -> builder.xRot(0))
                .build());

        context.register(ExtraModelModifiers.HOVERING, CompositeType.Builder.builder()
                .add(ExtraModelModifierTypes.HOVERING.get())
                .add(OperationType.Builder.builder(Operation.SET)
                        .parts(List.of(PartNames.LEFT_LEG, PartNames.RIGHT_LEG, PartNames.LEFT_ARM, PartNames.RIGHT_ARM),
                                builder -> builder.xRot(0).yRot(0))
                        .build())
                .build());

        context.register(ExtraModelModifiers.SMALL_ARMS, OperationType.Builder.builder(Operation.REMOVE)
                .parts(List.of(PartNames.LEFT_ARM, PartNames.RIGHT_ARM), builder -> builder.y(-3.25f))
                .build());

        context.register(ExtraModelModifiers.T_POSE, CompositeType.Builder.builder()
                .add(OperationType.Builder.builder(Operation.SET)
                        .part(PartNames.LEFT_ARM, builder -> builder.rotation(0, 0, 270))
                        .part(PartNames.RIGHT_ARM, builder -> builder.rotation(0, 0, 90))
                        .build())
                .build());

        context.register(ExtraModelModifiers.PANCAKE, CompositeType.Builder.builder()
                .add(new ScaleType(1, 0.05f, 1))
                .add(ExtraModelModifierTypes.NO_SHADOW.get())
                .build());

        context.register(ExtraModelModifiers.DANACE, CompositeType.Builder.builder()
                .add(new AnimationType(LTExtras.id("dance")))
                .build());

        context.register(ExtraModelModifiers.ZIPLINE, OperationType.Builder.builder(Operation.SET)
                .part(PartNames.LEFT_ARM, builder -> builder
                        .rotation(0, 0, 175)
                        .yScale(1.5f)
                        .build())
                .part(PartNames.RIGHT_ARM, builder -> builder
                        .rotation(0, 0, 185)
                        .yScale(1.5f)
                        .build())
                .build());
    }

    public static class AttachmentTypeTags extends TagsProvider<AttachmentType<?>> {

        public static final TagKey<AttachmentType<?>> COPY_RENDER_ATTACHMENTS = TagKey.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Identifier.fromNamespaceAndPath("peekaboo", "copy_render_attachments"));

        protected AttachmentTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, NeoForgeRegistries.Keys.ATTACHMENT_TYPES, lookupProvider, LTExtras.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            tag(COPY_RENDER_ATTACHMENTS)
                    .add(ExtraAttachments.MODEL_MODIFIERS.getKey());
        }
    }
}
