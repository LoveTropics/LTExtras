package com.lovetropics.extras;

import com.google.common.collect.Sets;
import com.lovetropics.extras.client.command.NameTagModeCommand;
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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
		dispatcher.register(Commands.literal("test").executes(context -> {
			context.getSource().sendSystemMessage(Component.literal("" + Block.BLOCK_STATE_REGISTRY.size()));
			List<Map.Entry<Block, List<BlockState>>> sorted = StreamSupport.stream(Block.BLOCK_STATE_REGISTRY.spliterator(), false)
					.collect(Collectors.groupingBy(BlockBehaviour.BlockStateBase::getBlock))
					.entrySet()
					.stream()
//					.sorted(Comparator.comparing(e -> -e.getValue().size()))
					.toList();
			Object2IntOpenHashMap<String> wordToCount = new Object2IntOpenHashMap<>();
			for (Map.Entry<Block, List<BlockState>> entry : sorted) {
				String[] parts = entry.getKey().builtInRegistryHolder().key().location().getPath().split("_");
				for (String part : Sets.newHashSet(parts)) {
					wordToCount.addTo(part, entry.getValue().size());
				}
				wordToCount.addTo(entry.getKey().builtInRegistryHolder().key().location().getNamespace(), entry.getValue().size());
			}
			List<Object2IntMap.Entry<String>> list = wordToCount.object2IntEntrySet().stream()
					.sorted(Comparator.comparing(e -> -e.getIntValue()))
					.toList();
			int i = 0;
			for (Object2IntMap.Entry<String> entry : list) {
				context.getSource().sendSystemMessage(Component.literal(entry.getKey() + ": " + entry.getIntValue()));
				if (i++ > 50) {
					break;
				}
			}
//			int i = 0;
//			for (Map.Entry<Block, List<BlockState>> entry : sorted) {
//				context.getSource().sendSystemMessage(Component.literal(entry.getKey() + ": " + entry.getValue().size()));
//				if (i++ > 500) {
//					break;
//				}
//			}
			return 1;
		}));
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
