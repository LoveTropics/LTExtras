package com.lovetropics.extras;

import com.lovetropics.extras.client.particle.ExtraParticles;
import com.lovetropics.extras.command.GenerateCommand;
import com.lovetropics.extras.command.SetMaxPlayersCommand;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.mojang.brigadier.CommandDispatcher;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
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

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mod("ltextras")
public class LTExtras {

	public static final String MODID = "ltextras";

	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MODID) {

		@Override
		public ItemStack makeIcon() {
			return ExtraBlocks.BUOY.asStack();
		}

		@Override
		public void fillItemList(NonNullList<ItemStack> stacks) {
			super.fillItemList(stacks);
			// TODO this is a bit inefficient but does it matter?
			// Fixes random item order when things are added to existing worlds
			final List<Item> order = registrate()
					.getAll(Item.class)
					.stream()
					.map(RegistryEntry::get)
					.collect(Collectors.toList());
			stacks.sort(Comparator.comparingInt(i -> order.indexOf(i.getItem())));
		}
	};

	private static NonNullLazy<Registrate> registrate = NonNullLazy.of(() ->
		Registrate.create(MODID)
			.creativeModeTab(() -> ITEM_GROUP));

	public static Registrate registrate() {
		return registrate.get();
	}

	public LTExtras() {
		// Compatible with all versions that match the semver (excluding the qualifier e.g. "-beta+42")
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(LTExtras::getCompatVersion, (s, v) -> LTExtras.isCompatibleVersion(s)));

		ExtraBlocks.init();

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
				p.add(ITEM_GROUP, "LTExtras");
				p.add(ExtraEffects.FISH_EYE.get(), "Fish Eye");
			});

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

		LTExtrasNetwork.register();
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
		SetMaxPlayersCommand.register(dispatcher);
		GenerateCommand.register(dispatcher);
	}

	@OnlyIn(Dist.CLIENT)
	private void clientSetup(FMLClientSetupEvent event) {
		ForgeConfig.CLIENT.alwaysSetupTerrainOffThread.set(true);
			((ForgeConfigSpec) ObfuscationReflectionHelper.getPrivateValue(ForgeConfig.class, null, "clientSpec")).save();
	}

	@OnlyIn(Dist.CLIENT)
	private void registerItemColors(ColorHandlerEvent.Item evt) {
		evt.getItemColors().register((stack, index) -> index == 0 ? Fluids.WATER.getAttributes().getColor() : -1,
				ExtraBlocks.WATER_BARRIER.get(),
				ExtraBlocks.FAKE_WATER.get());
	}
}
