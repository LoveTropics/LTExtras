package com.lovetropics.extras;

import com.lovetropics.extras.client.command.RenderPlayerNameTagCommand;
import com.lovetropics.extras.client.entity.model.RaveKoaModel;
import com.lovetropics.extras.client.particle.ExtraParticles;
import com.lovetropics.extras.collectible.CollectibleCommand;
import com.lovetropics.extras.command.GenerateCommand;
import com.lovetropics.extras.command.PoiCommand;
import com.lovetropics.extras.command.SetMaxPlayersCommand;
import com.lovetropics.extras.command.TpCommand;
import com.lovetropics.extras.command.WarpCommand;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.data.spawnitems.SpawnItemsCommand;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.entity.ExtraEntities;
import com.lovetropics.extras.item.ExtraItemProperties;
import com.lovetropics.extras.world_effect.WorldEffectCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.regex.Pattern;

@Mod("ltextras")
public class LTExtras {

	public static final String MODID = "ltextras";

    private static final ResourceLocation TAB_ID = ResourceLocation.fromNamespaceAndPath(MODID, "ltextras");
	public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, TAB_ID);

    private static Registrate REGISTRATE = null;

	public static Registrate registrate() {
		if (REGISTRATE == null) {
			REGISTRATE = Registrate.create(MODID).defaultCreativeTab(TAB_KEY);
		}

		return REGISTRATE;
	}

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

		ExtraLangKeys.init(registrate());
		registrate()
                .addDataGenerator(ProviderType.LANG, p -> {
                    p.add(ExtraEffects.FISH_EYE.get(), "Fish Eye");
					p.add("toast.collectible.title", "New Collectible!");
					p.add("toast.collectible.item", " + %s");

					p.add("spawnitems.set_not_restorable", "The spawn item set %s cannot be restored!");
					p.add("spawnitems.unknown_set", "Unknown spawn item set: %s");
					p.add("spawnitems.restored_successfully", "Items restored!");

					TpCommand.addTranslations(p);
					WarpCommand.addTranslations(p);
					RenderPlayerNameTagCommand.addTranslations(p);
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
		RenderPlayerNameTagCommand.register(dispatcher);
	}

	public static ResourceLocation location(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
	public static class ClientSetup {
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			ExtraItemProperties.register();
		}

		@SubscribeEvent
		public static void registerItemColors(RegisterColorHandlersEvent.Item evt) {
			evt.getItemColors().register((stack, index) -> index == 0 ? 0x3f76e4 : -1,
					ExtraBlocks.WATER_BARRIER.get(),
					ExtraBlocks.FAKE_WATER.get());
			evt.getItemColors().register((stack, index) -> {
				BlockState blockstate = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
				return evt.getBlockColors().getColor(blockstate, null, null, index);
			}, ExtraBlocks.SUBMERGED_LILY_PAD.asItem());
			evt.getItemColors().register((stack, index) -> 9551190,
					ExtraBlocks.GRASS_GRASS.get());
		}

		@SubscribeEvent
		public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(RaveKoaModel.LAYER_LOCATION, RaveKoaModel::createBodyLayer);
		}
	}
}
