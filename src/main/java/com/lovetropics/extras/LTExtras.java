package com.lovetropics.extras;

import com.lovetropics.extras.client.particle.ExtraParticles;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.NonNullLazyValue;

import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("ltextras")
public class LTExtras {
	
	public static final String MODID = "ltextras";
	
	public static final ItemGroup ITEM_GROUP = new ItemGroup(MODID) {
		
		@Override
		public ItemStack createIcon() {
			return ExtraBlocks.BUOY.asStack();
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

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modBus.addListener(this::registerItemColors);
		});
		
		registrate()
			.addDataGenerator(ProviderType.LANG, p -> p.add(ITEM_GROUP, "LTExtras"));
	}
	
    @OnlyIn(Dist.CLIENT)
    private void registerItemColors(ColorHandlerEvent.Item evt) {
        evt.getItemColors().register((stack, index) -> index == 0 ? Fluids.WATER.getAttributes().getColor() : -1, ExtraBlocks.WATER_BARRIER.get());
    }
}
