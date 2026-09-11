package com.lovetropics.extras;

import com.lovetropics.extras.client.command.NameTagModeCommand;
import com.lovetropics.extras.client.particle.ExtraParticles;
import com.lovetropics.extras.collectible.CollectibleCommand;
import com.lovetropics.extras.collectible.GenerateCollectibleCommand;
import com.lovetropics.extras.command.ExtraCommandArguments;
import com.lovetropics.extras.command.GenerateCommand;
import com.lovetropics.extras.command.HandCommand;
import com.lovetropics.extras.command.ListScoreboardCommand;
import com.lovetropics.extras.command.PoiCommand;
import com.lovetropics.extras.command.SetMaxPlayersCommand;
import com.lovetropics.extras.command.TpCommand;
import com.lovetropics.extras.command.WarpCommand;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.data.spawnitems.SpawnItemsCommand;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.entity.ExtraEntities;
import com.lovetropics.extras.entity.ExtraSerializers;
import com.lovetropics.extras.environmentattribute.EnvironmentAttributeCommand;
import com.lovetropics.extras.environmentattribute.ExtraAttributeTypes;
import com.lovetropics.extras.environmentattribute.ExtraEnvironmentAttributes;
import com.lovetropics.extras.model_modifer.ModelModifierCommand;
import com.lovetropics.extras.mounts.MountCommand;
import com.lovetropics.extras.placeholder.ExtraPlaceholders;
import com.lovetropics.extras.sounds.ExtraSounds;
import com.lovetropics.extras.techstack.VideoImporter;
import com.mojang.brigadier.CommandDispatcher;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.jspecify.annotations.Nullable;

import java.util.regex.Pattern;

@Mod("ltextras")
public class LTExtras {

    public static final String MODID = "ltextras";

    private static final Identifier TAB_ID = LTExtras.id("ltextras");
    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, TAB_ID);

    private static @Nullable Registrate REGISTRATE = null;

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
        ExtraPlaceholders.init();

        ExtraParticles.REGISTER.register(modBus);
        ExtraEffects.REGISTER.register(modBus);
        ExtraDataComponents.REGISTER.register(modBus);
        ExtraAttachments.REGISTER.register(modBus);
        ExtraSounds.REGISTER.register(modBus);
        ExtraSerializers.REGISTER.register(modBus);
        ExtraCommandArguments.REGISTER.register(modBus);
        ExtraAttributeTypes.register(modBus);
        ExtraEnvironmentAttributes.REGISTER.register(modBus);

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
        NeoForge.EVENT_BUS.addListener(VideoImporter.get()::onServerStarted);
        modBus.addListener(this::onModifyAttributes);

        ExtraLangKeys.init(registrate());
        registrate()
                .addDataGenerator(ProviderType.LANG, p -> {
                    p.add(ExtraEffects.FISH_EYE.get(), "Fish Eye");
                    p.add(ExtraEffects.PROPAGULED.get(), "Propaguled!");
                    p.add("toast.collectible.title", "New Collectible!");
                    p.add("toast.collectible.item", " + %s");

                    p.add("spawnitems.set_not_restorable", "The spawn item set %s cannot be restored!");
                    p.add("spawnitems.unknown_set", "Unknown spawn item set: %s");
                    p.add("spawnitems.restored_successfully", "Items restored!");

                    p.add("ltextras.friction", "Friction");

                    String keybindBase = "key." + MODID + ".";
                    p.add("key.category." + MODID + ".forklift", "Forklift Controls");
                    p.add(keybindBase + "forklift_raise", "Raise Forklift");
                    p.add(keybindBase + "forklift_lower", "Lower Forklift");
                    p.add(keybindBase + "forklift_drift", "Drift");
                    p.add(keybindBase + "eject_fork_riders", "Eject Riders");

                    TpCommand.addTranslations(p);
                    WarpCommand.addTranslations(p);

                    ExtraEffects.MODEL_EFFECTS.forEach((modifier, effect) ->
                            p.add(effect.value(), modifier.getEffectName()));
                })
                .addDataGenerator(ProviderType.BLOCK_TAGS, block -> {
                    block.tag(ExtraTags.Blocks.PLUMBERS_TNT_EXPLODES)
                            .add(BlockItemIds.MUD.block(), BlockItemIds.PACKED_MUD.block(), BlockItemIds.DIRT.block())
                            .add(TagEntry.optionalElement(Identifier.fromNamespaceAndPath("tropicraft", "mud")))
                            .add(TagEntry.optionalElement(Identifier.fromNamespaceAndPath("tropicraft", "mud_with_pianguas")))
                    ;
                })
                .addDataGenerator(ProviderType.ITEM_TAGS, item -> {
                    item.tag(ExtraTags.Items.HONIES)
                            .add(ItemIds.HONEY_BOTTLE)
                    ;
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
        ListScoreboardCommand.register(dispatcher);
        CollectibleCommand.register(dispatcher, buildContext);
        GenerateCollectibleCommand.register(dispatcher, buildContext);
        SpawnItemsCommand.register(dispatcher);
        TpCommand.register(dispatcher);
        EnvironmentAttributeCommand.register(dispatcher, buildContext);
        WarpCommand.register(dispatcher);
        PoiCommand.register(dispatcher, buildContext);
        ModelModifierCommand.register(dispatcher);
        MountCommand.register(dispatcher, buildContext);
        HandCommand.register(dispatcher);
    }

    private void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        NameTagModeCommand.register(dispatcher);
    }

    private void onModifyAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityTypes.PLAYER, FRICTION);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
