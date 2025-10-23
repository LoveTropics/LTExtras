package com.lovetropics.extras;

import com.lovetropics.extras.client.ClientPlayerSensorEffects;
import com.lovetropics.extras.client.command.NameTagModeCommand;
import com.lovetropics.extras.client.entity.model.RaveKoaModel;
import com.lovetropics.extras.client.particle.ExtraParticles;
import com.lovetropics.extras.collectible.CollectibleCommand;
import com.lovetropics.extras.command.GenerateCommand;
import com.lovetropics.extras.command.PoiCommand;
import com.lovetropics.extras.command.SetMaxPlayersCommand;
import com.lovetropics.extras.command.TpCommand;
import com.lovetropics.extras.command.WarpCommand;
import com.lovetropics.extras.data.GenericFusionModelProvider;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.data.spawnitems.SpawnItemsCommand;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.entity.ExtraEntities;
import com.lovetropics.extras.world_effect.WorldEffectCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

@Mod("ltextras")
public class LTExtras {

    public static final String MODID = "ltextras";

    private static final ResourceLocation TAB_ID = LTExtras.location("ltextras");
    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, TAB_ID);

    @Nullable
    private static Registrate REGISTRATE = null;

    public static Registrate registrate() {
        if (REGISTRATE == null) {
            REGISTRATE = Registrate.create(MODID).defaultCreativeTab(TAB_KEY);
        }

        return REGISTRATE;
    }

    public static final Holder<Attribute> FRICTION = registrate().simple(
            "friction",
            Registries.ATTRIBUTE,
            () -> new RangedAttribute("ltextras.friction", 1D, 0D, 1024D).setSyncable(true)
    );

    public LTExtras(IEventBus modBus, ModContainer container) {
        ExtraBlocks.init();
        ExtraItems.init();
        ExtraEntities.init();

        ExtraParticles.REGISTER.register(modBus);
        ExtraEffects.REGISTER.register(modBus);
        ExtraDataComponents.REGISTER.register(modBus);
        ExtraAttachments.REGISTER.register(modBus);

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
        modBus.addListener(this::onModifyAttributes);
        modBus.addListener(GenericFusionModelProvider::onGatherData);

        ExtraLangKeys.init(registrate());
        registrate()
                .addDataGenerator(ProviderType.LANG, p -> {
                    p.add(ExtraEffects.FISH_EYE.get(), "Fish Eye");
                    p.add("toast.collectible.title", "New Collectible!");
                    p.add("toast.collectible.item", " + %s");

                    p.add("spawnitems.set_not_restorable", "The spawn item set %s cannot be restored!");
                    p.add("spawnitems.unknown_set", "Unknown spawn item set: %s");
                    p.add("spawnitems.restored_successfully", "Items restored!");

                    p.add("ltextras.friction", "Friction");

                    TpCommand.addTranslations(p);
                    WarpCommand.addTranslations(p);
                })
                .generic(TAB_ID.getPath(), Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
                        .title(registrate().addLang("itemGroup", TAB_ID, "LTExtras"))
                        .icon(() -> ExtraBlocks.BUOY.asStack())
                        .build()
                ).build();

        container.registerConfig(ModConfig.Type.COMMON, ExtrasConfig.COMMON_CONFIG);
        container.registerConfig(ModConfig.Type.CLIENT, ExtrasConfig.CLIENT_CONFIG);
    }

    private static final Pattern QUALIFIER = Pattern.compile("-\\w+\\+\\d+");

    public static String getCompatVersion() {
        return getCompatVersion(ModList.get().getModContainerById(MODID).orElseThrow(IllegalStateException::new).getModInfo().getVersion().toString());
    }

    private static String getCompatVersion(String fullVersion) {
        return QUALIFIER.matcher(fullVersion).replaceAll("");
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();
        SetMaxPlayersCommand.register(dispatcher);
        GenerateCommand.register(dispatcher);
        CollectibleCommand.register(dispatcher, buildContext);
        SpawnItemsCommand.register(dispatcher);
        TpCommand.register(dispatcher);
        WorldEffectCommand.register(dispatcher);
        WarpCommand.register(dispatcher);
        PoiCommand.register(dispatcher, buildContext);
    }

    private void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        NameTagModeCommand.register(dispatcher);
    }

    private void onModifyAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, FRICTION);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
    public static class ClientSetup {
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(RaveKoaModel.LAYER_LOCATION, RaveKoaModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            ClientPlayerSensorEffects.registerGuiLayers(event);
        }
    }
}
