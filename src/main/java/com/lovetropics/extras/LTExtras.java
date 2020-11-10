package com.lovetropics.extras;

import com.lovetropics.extras.client.particle.ExtraParticles;
import com.lovetropics.extras.client.renderer.dummy.DummyPlayerEntityRenderer;
import com.lovetropics.extras.entity.DummyPlayerEntity;
import com.lovetropics.extras.entity.UpdateDummyTexturesMessage;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mod("ltextras")
public class LTExtras {

	public static final String MODID = "ltextras";
	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(MODID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	static {
		CHANNEL.registerMessage(0, UpdateDummyTexturesMessage.class, UpdateDummyTexturesMessage::toBytes, UpdateDummyTexturesMessage::fromBytes, UpdateDummyTexturesMessage::handle);
	}

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
		ExtraBlocks.init();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		ExtraParticles.REGISTER.register(modBus);
		DummyPlayerEntity.register();

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modBus.addListener(this::clientSetup);
			modBus.addListener(this::registerItemColors);
		});

		registrate()
			.addDataGenerator(ProviderType.LANG, p -> p.add(ITEM_GROUP, "LTExtras"));
	}

	@OnlyIn(Dist.CLIENT)
	private void clientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(DummyPlayerEntity.DUMMY_PLAYER.get(), DummyPlayerEntityRenderer::new);
	}

    @OnlyIn(Dist.CLIENT)
    private void registerItemColors(ColorHandlerEvent.Item evt) {
        evt.getItemColors().register((stack, index) -> index == 0 ? Fluids.WATER.getAttributes().getColor() : -1,
        		ExtraBlocks.WATER_BARRIER.get(),
        		ExtraBlocks.FAKE_WATER.get());
    }
}
