package com.lovetropics.extras.client;

import com.google.common.reflect.TypeToken;
import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.client.block.DisplayBlockRender;
import com.lovetropics.extras.client.block.WordBoxBlockEntityRenderer;
import com.lovetropics.extras.client.entity.model.AmazonRiverDolphinModel;
import com.lovetropics.extras.client.entity.model.ForkliftModel;
import com.lovetropics.extras.client.entity.model.GlassFrogModel;
import com.lovetropics.extras.client.entity.model.HighHeelsModel;
import com.lovetropics.extras.client.entity.model.RaveKoaModel;
import com.lovetropics.extras.client.entity.model.SpinningSignModel;
import com.lovetropics.extras.client.entity.model.WaterCoolerModel;
import com.lovetropics.extras.client.entity.renderer.layers.CustomSingleBootLayer;
import com.lovetropics.extras.client.entity.state.HoniedShulkerRenderState;
import com.lovetropics.extras.client.keybinds.ForkliftKeybinds;
import com.lovetropics.extras.client.model_modifer.ModelModifierClient;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.effect.PropaguledEffect;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientRegisterEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ExtraBlocks.DISPLAY_BLOCK_ENTITY.get(), DisplayBlockRender::new);
        event.registerBlockEntityRenderer(ExtraBlocks.WORD_BOX_BLOCK_ENTITY.get(), WordBoxBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(FallingBlockRenderer.class, WordBoxBlockEntityRenderer::updateFallingBlockRenderState);
        event.registerEntityModifier(ShulkerRenderer.class, HoniedShulkerRenderState::updateHoniedRenderState);
        event.registerEntityModifier(new TypeToken<LivingEntityRenderer<?, ?, ?>>() {}, CustomSingleBootLayer::updateBootRenderState);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : event.getEntityTypes()) {
            EntityRenderer<?, ?> renderer = event.getRenderer(entityType);
            if (renderer instanceof HumanoidMobRenderer<?, ?, ?> humanoidMobRenderer) {
                humanoidMobRenderer.addLayer(new CustomSingleBootLayer<>((RenderLayerParent) renderer));
            }
        }
        for(var skin : event.getSkins()) {
            var renderer = event.getPlayerRenderer(skin);
            if(renderer != null) {
                renderer.addLayer(new CustomSingleBootLayer<>(renderer));
            }
        }
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RaveKoaModel.LAYER_LOCATION, RaveKoaModel::createBodyLayer);
        event.registerLayerDefinition(HighHeelsModel.LAYER_LOCATION, HighHeelsModel::createLayer);
        event.registerLayerDefinition(ForkliftModel.LAYER_LOCATION, ForkliftModel::createBodyLayer);
        event.registerLayerDefinition(WaterCoolerModel.LAYER_LOCATION, WaterCoolerModel::createBodyLayer);
        event.registerLayerDefinition(SpinningSignModel.LAYER_LOCATION, SpinningSignModel::createBodyLayer);
        event.registerLayerDefinition(AmazonRiverDolphinModel.LAYER, AmazonRiverDolphinModel::createBodyLayer);
        event.registerLayerDefinition(AmazonRiverDolphinModel.BABY_LAYER, () -> AmazonRiverDolphinModel.createBodyLayer().apply(AmazonRiverDolphinModel.BABY_TRANSFORMER));
        event.registerLayerDefinition(GlassFrogModel.LAYER, GlassFrogModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        ClientPlayerSensorEffects.registerGuiLayers(event);
        ClientPlayerForkliftHUD.registerGuiLayers(event);
    }

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        ForkliftKeybinds.init();
        ModelModifierClient.init();
    }

    @SubscribeEvent
    public static void registerMobEffectExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new PropaguledEffect.ClientExtensions(), ExtraEffects.PROPAGULED);
    }
}
