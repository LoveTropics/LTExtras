package com.lovetropics.extras;

import com.lovetropics.extras.client.particle.ExtraParticles;
import com.lovetropics.extras.collectible.CollectibleCommand;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.command.GenerateCommand;
import com.lovetropics.extras.command.SetMaxPlayersCommand;
import com.lovetropics.extras.data.spawnitems.SpawnItemsCommand;
import com.lovetropics.extras.data.spawnitems.SpawnItemsStore;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.entity.ExtraEntities;
import com.lovetropics.extras.network.LTExtrasNetwork;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkConstants;

import java.util.regex.Pattern;

@Mod("ltextras")
public class LTExtras {

	public static final String MODID = "ltextras";

    private static final ResourceLocation TAB_ID = new ResourceLocation(MODID, "ltextras");

    private static final NonNullLazy<Registrate> REGISTRATE = NonNullLazy.of(() -> Registrate.create(MODID).defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, TAB_ID)));

	public static final Capability<CollectibleStore> COLLECTIBLE_STORE = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<SpawnItemsStore> SPAWN_ITEMS_STORE = CapabilityManager.get(new CapabilityToken<>() {});

	public static Registrate registrate() {
		return REGISTRATE.get();
	}

	public LTExtras() {
		// Compatible with all versions that match the semver (excluding the qualifier e.g. "-beta+42")
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(LTExtras::getCompatVersion, (s, v) -> LTExtras.isCompatibleVersion(s)));

		ExtraBlocks.init();
		ExtraItems.init();
		ExtraEntities.init();

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		ExtraParticles.REGISTER.register(modBus);
		ExtraEffects.REGISTER.register(modBus);

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modBus.addListener(this::clientSetup);
			modBus.addListener(this::registerItemColors);
		});

		MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        registrate()
                .addDataGenerator(ProviderType.LANG, p -> {
                    p.add(ExtraEffects.FISH_EYE.get(), "Fish Eye");
					p.add("toast.collectible.title", "New Collectible!");
					p.add("toast.collectible.item", " + %s");

					p.add("spawnitems.set_not_restorable", "The spawn item set %s cannot be restored!");
					p.add("spawnitems.unknown_set", "Unknown spawn item set: %s");
					p.add("spawnitems.restored_successfully", "Items restored!");
                })
                .generic(TAB_ID.getPath(), Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
                        .title(registrate().addLang("itemGroup", TAB_ID, "LTExtras"))
                        .icon(() -> ExtraBlocks.BUOY.asStack())
                        .build()
                ).build();

		LTExtrasNetwork.register();

		// Mark WorldEdit as only required on the server
		ModList.get().getModContainerById("worldedit").ifPresent(worldedit -> {
			ModLoadingContext.get().setActiveContainer(worldedit);
			ModLoadingContext.get().registerExtensionPoint(
				IExtensionPoint.DisplayTest.class,
				() -> new IExtensionPoint.DisplayTest(
					() -> NetworkConstants.IGNORESERVERONLY,
					(a, b) -> true
				)
			);
		});
	}

	private static final Pattern QUALIFIER = Pattern.compile("-\\w+\\+\\d+");
	public static String getCompatVersion() {
		return getCompatVersion(ModList.get().getModContainerById(MODID).orElseThrow(IllegalStateException::new).getModInfo().getVersion().toString());
	}
	private static String getCompatVersion(String fullVersion) {
		return QUALIFIER.matcher(fullVersion).replaceAll("");
	}
	public static boolean isCompatibleVersion(String version) {
		return getCompatVersion().equals(getCompatVersion(version));
	}

	private void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		CommandBuildContext buildContext = event.getBuildContext();
		SetMaxPlayersCommand.register(dispatcher);
		GenerateCommand.register(dispatcher);
		CollectibleCommand.register(dispatcher, buildContext);
		SpawnItemsCommand.register(dispatcher);
	}

	@OnlyIn(Dist.CLIENT)
	private void clientSetup(FMLClientSetupEvent event) {
		ForgeConfig.CLIENT.alwaysSetupTerrainOffThread.set(true);
			((ForgeConfigSpec) ObfuscationReflectionHelper.getPrivateValue(ForgeConfig.class, null, "clientSpec")).save();
	}

	@OnlyIn(Dist.CLIENT)
	private void registerItemColors(RegisterColorHandlersEvent.Item evt) {
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
}
