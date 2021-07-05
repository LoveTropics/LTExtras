package com.lovetropics.extras;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.lovetropics.extras.client.particle.ExtraParticles;
import com.lovetropics.extras.command.GenerateCommand;
import com.lovetropics.extras.command.SetMaxPlayersCommand;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.mojang.brigadier.CommandDispatcher;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.command.CommandSource;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod("ltextras")
public class LTExtras {

	public static final String MODID = "ltextras";

	public static final ItemGroup ITEM_GROUP = new ItemGroup(MODID) {

		@Override
		public ItemStack createIcon() {
			return ExtraBlocks.BUOY.asStack();
		}

		@Override
		public void fill(NonNullList<ItemStack> stacks) {
			super.fill(stacks);
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

    private static NonNullLazyValue<Registrate> registrate = new NonNullLazyValue<>(() -> 
    	Registrate.create(MODID)
			.itemGroup(() -> ITEM_GROUP));

    public static Registrate registrate() {
    	return registrate.get();
    }

	public LTExtras() {
    	// Compatible with all versions that match the semver (excluding the qualifier e.g. "-beta+42")
    	ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(LTExtras::getCompatVersion, (s, v) -> LTExtras.isCompatibleVersion(s)));

		ExtraBlocks.init();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		ExtraParticles.REGISTER.register(modBus);

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modBus.addListener(this::clientSetup);
			modBus.addListener(this::registerItemColors);
		});

		MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

		registrate()
			.addDataGenerator(ProviderType.LANG, p -> p.add(ITEM_GROUP, "LTExtras"));

        // Mark WorldEdit as only required on the server
		ModList.get().getModContainerById("worldedit").ifPresent(worldedit -> {
			Supplier<?> extension = ObfuscationReflectionHelper.getPrivateValue(ModContainer.class, worldedit, "contextExtension");
			ModLoadingContext.get().setActiveContainer(worldedit, extension.get());
	        ModLoadingContext.get().registerExtensionPoint(
	            ExtensionPoint.DISPLAYTEST,
	            () -> Pair.of(
	                () -> FMLNetworkConstants.IGNORESERVERONLY,
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
		CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
		SetMaxPlayersCommand.register(dispatcher);
		GenerateCommand.register(dispatcher);
	}

	@OnlyIn(Dist.CLIENT)
	private void clientSetup(FMLClientSetupEvent event) {
	}

    @OnlyIn(Dist.CLIENT)
    private void registerItemColors(ColorHandlerEvent.Item evt) {
        evt.getItemColors().register((stack, index) -> index == 0 ? Fluids.WATER.getAttributes().getColor() : -1,
        		ExtraBlocks.WATER_BARRIER.get(),
        		ExtraBlocks.FAKE_WATER.get());
    }
}
