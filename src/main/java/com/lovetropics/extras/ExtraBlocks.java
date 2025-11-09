package com.lovetropics.extras;

import com.lovetropics.extras.block.AdjustableLampBlock;
import com.lovetropics.extras.block.BoringEndRodBlock;
import com.lovetropics.extras.block.CeilingCarpetBlock;
import com.lovetropics.extras.block.CheckpointBlock;
import com.lovetropics.extras.block.ConveyorBeltBlock;
import com.lovetropics.extras.block.CurtainBlock;
import com.lovetropics.extras.block.CustomSeagrassBlock;
import com.lovetropics.extras.block.CustomSugarCaneBlock;
import com.lovetropics.extras.block.CustomTallSeagrassBlock;
import com.lovetropics.extras.block.DisplayBlock;
import com.lovetropics.extras.block.FakeWaterBlock;
import com.lovetropics.extras.block.GirderBlock;
import com.lovetropics.extras.block.GlowSticksBlock;
import com.lovetropics.extras.block.ImposterCoralBlock;
import com.lovetropics.extras.block.JumpPadBlock;
import com.lovetropics.extras.block.LightweightBarrierBlock;
import com.lovetropics.extras.block.MobControllerBlock;
import com.lovetropics.extras.block.PanelBlock;
import com.lovetropics.extras.block.PapyrusStemBlock;
import com.lovetropics.extras.block.PapyrusUmbelBlock;
import com.lovetropics.extras.block.ParticleEmitterBlock;
import com.lovetropics.extras.block.PassableBarrierBlock;
import com.lovetropics.extras.block.PassableNoPlaceBarrierBlock;
import com.lovetropics.extras.block.PianguasBlock;
import com.lovetropics.extras.block.PlumbersTntBlock;
import com.lovetropics.extras.block.ReedsBlock;
import com.lovetropics.extras.block.RoleBarrierBlock;
import com.lovetropics.extras.block.RopeBlock;
import com.lovetropics.extras.block.ScientificNameBlock;
import com.lovetropics.extras.block.SeatBlock;
import com.lovetropics.extras.block.SpeedyBlock;
import com.lovetropics.extras.block.SpeedySlabBlock;
import com.lovetropics.extras.block.SpeedyZone;
import com.lovetropics.extras.block.SubmergedLilyBlock;
import com.lovetropics.extras.block.TeleportPadBlock;
import com.lovetropics.extras.block.ThornStemBlock;
import com.lovetropics.extras.block.WarehouseRoadBoosterBlock;
import com.lovetropics.extras.block.WaterBarrierBlock;
import com.lovetropics.extras.block.entity.DisplayBlockEntity;
import com.lovetropics.extras.block.entity.JumpPadBlockEntity;
import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.block.entity.ParticleEmitterBlockEntity;
import com.lovetropics.extras.block.entity.TeleportPadBlockEntity;
import com.lovetropics.extras.data.ImposterBlockTemplate;
import com.lovetropics.extras.mixin.BlockPropertiesMixin;
import com.lovetropics.lib.block.CustomShapeBlock;
import com.mojang.math.Quadrant;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.ScaffoldingBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseCoralFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.*;
import static net.minecraft.client.data.models.blockstates.MultiVariantGenerator.dispatch;

public class ExtraBlocks {

    public static final Registrate REGISTRATE = LTExtras.registrate();

    // One-off custom blocks

    public static final BlockEntry<WaterBarrierBlock> WATER_BARRIER = REGISTRATE.block("water_barrier", WaterBarrierBlock::new)
            .initialProperties(() -> Blocks.BARRIER)
            .properties(p -> p.noLootTable())
            .blockstate(() -> (ctx, prov) -> prov.createAirLikeBlock(ctx.get(), ResourceLocation.withDefaultNamespace("item/barrier")))
            .item()
            .model(() -> Models::generateWaterBarrierItem)
            .build()
            .register();

    public static final BlockEntry<LightweightBarrierBlock> LIGHTWEIGHT_BARRIER = REGISTRATE.block("lightweight_barrier", LightweightBarrierBlock::new)
            .initialProperties(() -> Blocks.BARRIER)
            .properties(p -> p.strength(0.0F, 3.6e6f).noLootTable())
            .blockstate(() -> (ctx, prov) -> prov.createAirLikeBlock(ctx.get(), ResourceLocation.withDefaultNamespace("item/barrier")))
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatItem(ctx.get(), Items.BARRIER, ModelTemplates.FLAT_ITEM))
            .build()
            .register();

    public static final BlockEntry<RoleBarrierBlock> ROLE_BARRIER = REGISTRATE.block("role_barrier", RoleBarrierBlock::new)
            .lang("Role Barrier")
            .initialProperties(() -> Blocks.BARRIER)
            .properties(p -> p.noLootTable())
            .blockstate(() -> (ctx, prov)
                    -> prov.createAirLikeBlock(ctx.get(), ResourceLocation.withDefaultNamespace("item/barrier")))
            .simpleBlockEntity(RoleBarrierBlock.RoleBarrierBE::new)
            .item()
            .model(() -> (ctx, prov)
                    -> prov.itemModelOutput.accept(ctx.get(), ItemModelUtils.tintedModel(prov.createFlatItemModel(Items.BARRIER, ModelTemplates.FLAT_ITEM), ItemModelUtils.constantTint(0x008000))))
            .build()
            .register();

    public static final BlockEntry<PassableBarrierBlock> PASSABLE_BARRIER = REGISTRATE.block("passable_barrier", PassableBarrierBlock::new)
            .initialProperties(() -> Blocks.BARRIER)
            .properties(p -> p.noLootTable())
            .blockstate(() -> (ctx, prov)
                    -> prov.createAirLikeBlock(ctx.get(), ResourceLocation.withDefaultNamespace("item/barrier")))
            .item()
            .model(() -> (ctx, prov)
                    -> prov.generateFlatItem(ctx.get(), Items.BARRIER, ModelTemplates.FLAT_ITEM))
            .build()
            .register();

    public static final BlockEntry<PassableNoPlaceBarrierBlock> PASSABLE_NO_PLACE_BARRIER = REGISTRATE.block("passable_no_place_barrier", PassableNoPlaceBarrierBlock::new)
            .initialProperties(() -> Blocks.BARRIER)
            .properties(p -> p.noLootTable().explosionResistance(0.0f))
            .blockstate(() -> (ctx, prov)
                    -> prov.createAirLikeBlock(ctx.get(), ResourceLocation.withDefaultNamespace("item/barrier")))
            .item()
            .model(() -> (ctx, prov)
                    -> prov.generateFlatItem(ctx.get(), Items.BARRIER, ModelTemplates.FLAT_ITEM))
            .build()
            .register();

    public static final BlockEntry<FakeWaterBlock> FAKE_WATER = REGISTRATE.block("fake_water", FakeWaterBlock::new)
            .initialProperties(() -> Blocks.BARRIER)
            .properties(p -> p.noLootTable())
            .blockstate(() -> (ctx, prov) ->
                    prov.createAirLikeBlock(ctx.get(), ResourceLocation.withDefaultNamespace("block/water_still"))
            )
            .item()
            .model(() -> Models::generateFakeWaterItem)
            .build()
            .register();

    public static final BlockEntry<CustomShapeBlock> BUOY = REGISTRATE.block("buoy", p -> new CustomShapeBlock(
                    Shapes.or(
                            Block.box(2, 0, 2, 14, 3, 14),
                            Block.box(3, 3, 3, 13, 14, 13)),
                    p))
            .initialProperties(() -> Blocks.BEACON)
            .blockstate(() -> (ctx, prov) -> prov.create(ctx.get(), ModelLocationUtils.getModelLocation(ctx.get())))
            .item(PlaceOnWaterBlockItem::new).build()
            .register();

    public static final BlockEntry<PanelBlock> GLASS_PANEL = REGISTRATE.block("glass_panel", PanelBlock::new)
            .initialProperties(() -> Blocks.GLASS)
            .blockstate(() -> (ctx, prov) -> {
                TextureMapping textures = TextureMapping.defaultTexture(ModelLocationUtils.getModelLocation(Blocks.GLASS));
                MultiVariant variant = plainVariant(ModelTemplates.TRAPDOOR_TOP.create(ctx.get(), textures, prov.modelOutput));
                prov.blockStateOutput.accept(dispatch(ctx.get(), variant).with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));
            })
            .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .item()
            .model(() -> (ctx, prov) -> {
                TextureMapping textures = TextureMapping.defaultTexture(ModelLocationUtils.getModelLocation(Blocks.GLASS));
                prov.generateWithTemplate(ctx.get(), ModelTemplates.TRAPDOOR_BOTTOM, textures);
            })
            .build()
            .register();

    public static final BlockEntry<GirderBlock> STEEL_GIRDER = steelGirder("");
    public static final BlockEntry<GirderBlock> RUSTING_STEEL_GIRDER = steelGirder("rusting");
    public static final BlockEntry<GirderBlock> RUSTED_STEEL_GIRDER = steelGirder("rusted");

    private static BlockEntry<GirderBlock> steelGirder(String name) {
        return REGISTRATE.block((name.isEmpty() ? name : (name + "_")) + "steel_girder", p -> new GirderBlock(ExtraTags.Blocks.STEEL_GIRDERS, p))
                .initialProperties(() -> Blocks.IRON_BARS)
                .tag(ExtraTags.Blocks.STEEL_GIRDERS)
                .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .tag(BlockTags.NEEDS_IRON_TOOL)
                .blockstate(() -> Models::generateSteelGirder)
                .simpleItem()
                .register();
    }

    public static final BlockEntry<CheckpointBlock> CHECKPOINT = REGISTRATE.block("checkpoint", CheckpointBlock::new)
            .initialProperties(() -> Blocks.BEDROCK)
            .properties(p -> p.noLootTable().noOcclusion())
            .blockstate(() -> (ctx, prov) -> prov.createAirLikeBlock(ctx.get(), Items.STRUCTURE_VOID))
            .item()
            .model(() -> (ctx, prov) ->
                    prov.generateFlatItem(ctx.get(), ResourceLocation.withDefaultNamespace("item/structure_void"))
            )
            .build()
            .register();

    public static final BlockEntry<ScaffoldingBlock> METAL_SCAFFOLDING = REGISTRATE.block("metal_scaffolding", p -> (ScaffoldingBlock) new ScaffoldingBlock(p) {
                @Override
                public boolean isScaffolding(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
                    return true;
                }
            })
            .initialProperties(() -> Blocks.SCAFFOLDING)
            .blockstate(() -> Models::generateScaffolding)
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .tag(BlockTags.CLIMBABLE)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .item(ScaffoldingBlockItem::new)
            .model(() -> (ctx, prov) -> prov.generateBlockItem(ctx.get(), "_stable"))
            .build()
            .register();

    public static final BlockEntry<IronBarsBlock> RUSTY_IRON_BARS = REGISTRATE.block("rusty_iron_bars", p -> (IronBarsBlock) new IronBarsBlock(p) {
            })
            .initialProperties(() -> Blocks.IRON_BARS)
            .blockstate(() -> Models::barsBlock)
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatBlockItem(ctx.get()))
            .build()
            .register();

    public static final BlockEntry<LadderBlock> METAL_LADDER = ladder("metal_ladder", "metal_ladder").register();
    public static final BlockEntry<LadderBlock> RUSTY_METAL_LADDER = ladder("rusty_metal_ladder", "rusty_metal_ladder").register();
    public static final BlockEntry<LadderBlock> FAST_METAL_LADDER = ladder("fast_metal_ladder", "metal_ladder").tag(ExtraTags.Blocks.CLIMBABLE_FAST).register();
    public static final BlockEntry<LadderBlock> FAST_RUSTY_METAL_LADDER = ladder("fast_rusty_metal_ladder", "rusty_metal_ladder").tag(ExtraTags.Blocks.CLIMBABLE_FAST).register();

    private static final BlockBuilder<LadderBlock, Registrate> ladder(String name, String texture) {
        return REGISTRATE.block(name, p -> (LadderBlock) new LadderBlock(p) {
                }).initialProperties(() -> Blocks.IRON_BARS)
                .tag(BlockTags.CLIMBABLE)
                .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .tag(BlockTags.NEEDS_IRON_TOOL)
                .blockstate(() -> (ctx, prov) -> Models.generateLadder(ctx, prov, texture))
                .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
                .item()
                .model(() -> (ctx, prov) -> prov.generateFlatItem(ctx.get(), prov.modLoc("block/" + texture)))
                .build();
    }

    public static final BlockEntry<Block> RUSTY_PAINTED_METAL = REGISTRATE.block("rusty_painted_metal", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .simpleItem()
            .register();

    public static final BlockEntry<MobControllerBlock> MOB_CONTROLLER = REGISTRATE.block("mob_controller", MobControllerBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .simpleItem()
            .blockEntity(MobControllerBlockEntity::new)
            .build()
            .register();

    public static final BlockEntityEntry<MobControllerBlockEntity> MOB_CONTROLLER_BE = BlockEntityEntry.cast(MOB_CONTROLLER.getSibling(Registries.BLOCK_ENTITY_TYPE));

    public static final BlockEntry<ParticleEmitterBlock> PARTICLE_EMITTER = REGISTRATE.block("particle_emitter", ParticleEmitterBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .simpleItem()
            .blockEntity(ParticleEmitterBlockEntity::new)
            .build()
            .register();

    public static final BlockEntry<ConveyorBeltBlock> CONVEYOR_BELT_BLOCK = REGISTRATE.block("conveyor_belt", ConveyorBeltBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .blockstate(() -> Models::generateRotatedHorizontalBlock)
            .simpleItem()
            .register();

    public static final BlockEntityEntry<ParticleEmitterBlockEntity> PARTICLE_EMITTER_BE = BlockEntityEntry.cast(PARTICLE_EMITTER.getSibling(Registries.BLOCK_ENTITY_TYPE));

    // TODO: It's the same as the imposter block, remove and datafix
    public static final BlockEntry<Block> BLACK_CONCRETE_POWDER_FAKE = REGISTRATE.block("black_concrete_powder_fake", Block::new)
            .initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.sound(SoundType.SAND))
            .item()
            .model(() -> (ctx, prov) -> Models.generateBlockItem(ctx, prov, Blocks.BLACK_CONCRETE_POWDER))
            .build()
            .blockstate(() -> (ctx, prov) -> {
                Variant variant = plainModel(ModelLocationUtils.getModelLocation(Blocks.BLACK_CONCRETE_POWDER));
                prov.blockStateOutput.accept(dispatch(ctx.get(), createRotatedVariants(variant)));
            })
            .register();

    public static final BlockEntry<StainedGlassBlock> SMOOTH_LIGHT_GRAY_STAINED_GLASS = REGISTRATE.block("smooth_light_gray_stained_glass", p -> new StainedGlassBlock(DyeColor.LIGHT_GRAY, p))
            .initialProperties(() -> Blocks.LIGHT_GRAY_STAINED_GLASS)
            .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
            .addLayer(() -> () -> ChunkSectionLayer.TRANSLUCENT)
            .simpleItem()
            .register();

    public static final Set<BlockEntry<StainedGlassBlock>> EDGELESS_GLASS_BLOCKS = Stream.of(DyeColor.values())
            .map(ExtraBlocks::edgelessGlass)
            .collect(Collectors.toSet());

    private static BlockEntry<StainedGlassBlock> edgelessGlass(DyeColor dyeColor) {
        final String color = dyeColor.getName();
        return REGISTRATE.block("edgeless_" + color + "_stained_glass", p -> new StainedGlassBlock(dyeColor, p))
                .initialProperties(() -> Blocks.GLASS)
                .blockstate(() -> (ctx, prov) ->
                        prov.generate(ctx.get(), TexturedModel.CUBE.updateTexture(textures -> textures.put(TextureSlot.ALL, prov.modLoc("block/edgeless/edgeless_" + color + "_stained_glass"))))
                )
                .addLayer(() -> () -> ChunkSectionLayer.TRANSLUCENT)
                .simpleItem()
                .register();
    }

    // TODO: Duplicate with Tropicraft, remove and datafix!
    public static final BlockEntry<ReedsBlock> REEDS = REGISTRATE.block("reeds", ReedsBlock::new)
            .initialProperties(() -> Blocks.SUGAR_CANE)
            .properties(p -> p.noLootTable())
            .blockstate(() -> Models::generateReeds)
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatItem(ctx.get(), prov.modLoc("block/" + ctx.getName() + "_top_tall")))
            .build()
            .register();

    public static final BlockEntry<CustomSugarCaneBlock> SUGAR_CANE = REGISTRATE.block("sugar_cane", CustomSugarCaneBlock::new)
            .initialProperties(() -> Blocks.SUGAR_CANE)
            .blockstate(() -> Models::generateSugarCane)
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .defaultLoot()
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatItem(ctx.get(), prov.modLoc("block/" + ctx.getName() + "_top")))
            .build()
            .register();

    public static final BlockEntry<PianguasBlock> PIANGUAS = REGISTRATE.block("pianguas", PianguasBlock::new)
            .properties(p -> p.mapColor(MapColor.STONE).noCollission().instabreak().instrument(NoteBlockInstrument.BASEDRUM))
            .blockstate(() -> (ctx, prov) -> {
                MultiPartGenerator builder = MultiPartGenerator.multiPart(ctx.get());
                ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(ctx.get());
                MultiVariant baseVariant = plainVariant(modelLocation);
                PianguasBlock.ATTACHMENTS.forEach((direction, property) -> {
                    if (direction.getAxis().isHorizontal()) {
                        int rotationY = (((int) direction.toYRot()) + 180) % 360;
                        builder.with(
                                BlockModelGenerators.condition().term(property, true),
                                baseVariant.with(VariantMutator.Y_ROT.withValue(Quadrant.parseJson(rotationY)).then(BlockModelGenerators.UV_LOCK))
                        );
                    } else {
                        int rotationX = direction == Direction.DOWN ? 90 : 270;
                        builder.with(
                                BlockModelGenerators.condition().term(property, true),
                                baseVariant.with(VariantMutator.X_ROT.withValue(Quadrant.parseJson(rotationX)).then(BlockModelGenerators.UV_LOCK))
                        );
                    }
                });
                prov.blockStateOutput.accept(builder);
            })
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatBlockItem(ctx.get()))
            .build()
            .register();

    public static final BlockEntry<RopeBlock> OLD_ROPE = rope("old_rope").register();
    public static final BlockEntry<RopeBlock> PARACORD = rope("paracord").tag(ExtraTags.Blocks.CLIMBABLE_VERY_FAST).register();

    private static BlockBuilder<RopeBlock, Registrate> rope(String name) {
        return REGISTRATE.block(name, RopeBlock::new)
                .properties(p -> p.mapColor(MapColor.WOOL).instabreak().noCollission().sound(SoundType.WOOL).ignitedByLava())
                .blockstate(() -> Models::generateRope)
                .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
                .tag(BlockTags.CLIMBABLE)
                .item()
                .model(() -> (ctx, prov) -> prov.itemModelOutput.accept(ctx.get(), ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ctx.get(), TextureMapping.layer0(prov.modLoc("block/" + name + "_knot")), prov.modelOutput))))
                .build();
    }

    private static final List<DyeColor> GLOW_STICKS_DYES = Arrays.stream(DyeColor.values())
            .filter(color -> color != DyeColor.BLACK && color != DyeColor.LIGHT_GRAY && color != DyeColor.GRAY)
            .collect(Collectors.toList());

    public static final Map<DyeColor, BlockEntry<GlowSticksBlock>> GLOW_STICKS = GLOW_STICKS_DYES.stream()
            .collect(Collectors.toMap(Function.identity(), dyeColor -> {
                String dyeName = dyeColor.getSerializedName();
                String name = dyeName + "_glow_sticks";
                return REGISTRATE.block(name, GlowSticksBlock::new)
                        .properties(p -> p.instabreak().noCollission().sound(SoundType.GLASS).noOcclusion().instrument(NoteBlockInstrument.HAT)
                                .lightLevel(value -> 6)
                        )
                        .blockstate(() -> (ctx, prov) -> {
                            ResourceLocation model = prov.getBuilder()
                                    .transformTemplate(template -> template.parent(prov.modLoc("block/glow_sticks"))
                                            .renderType(prov.mcLoc("translucent")))
                                    .texture(Models.GLOW_STICKS, prov.modLoc("block/glow_sticks/" + dyeName))
                                    .build(ctx.get());
                            prov.blockStateOutput.accept(dispatch(ctx.get(), createRotatedVariants(plainModel(model))));
                        })
                        .simpleItem()
                        .register();
            }));

    public static final BlockEntry<VineBlock> INFERTILE_VINE = REGISTRATE.block("infertile_vine", VineBlock::new)
            .initialProperties(() -> Blocks.VINE)
            // Mixin annoyance, accessor setters can't return self
            .properties(p -> {
                ((BlockPropertiesMixin) p).setIsRandomlyTicking(false);
                return p;
            })
            .tag(BlockTags.CLIMBABLE)
            .blockstate(() -> Models::generateInfertileVine)
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .color(() -> () -> (state, reader, pos, color) -> reader != null && pos != null
                    ? BiomeColors.getAverageFoliageColor(reader, pos)
                    : FoliageColor.FOLIAGE_DEFAULT)
            .item()
            .model(() -> Models::generateInfertileVineItem)
            .build()
            .register();

    public static final BlockEntry<DoorBlock> HEAVY_SPRUCE_DOOR = REGISTRATE.block("heavy_spruce_door", p -> new DoorBlock(BlockSetType.IRON, p))
            .initialProperties(() -> Blocks.SPRUCE_DOOR)
            .blockstate(() -> (ctx, prov) ->
                    prov.generateDoorBlock(ctx.get(), prov.blockTexture(Blocks.SPRUCE_DOOR, "_bottom"), prov.blockTexture(Blocks.SPRUCE_DOOR, "_top"))
            )
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .tag(BlockTags.MINEABLE_WITH_AXE)
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatItem(ctx.get(), Items.SPRUCE_DOOR, ModelTemplates.FLAT_ITEM))
            .build()
            .register();

    public static final BlockEntry<ThornStemBlock> THORN_STEM = REGISTRATE.block("thorn_stem", ThornStemBlock::new)
            .initialProperties(() -> Blocks.ACACIA_LEAVES)
            .properties(p -> p.noOcclusion().isRedstoneConductor((state, world, pos) -> false))
            .blockstate(() -> (ctx, prov) -> {
                MultiVariant core = plainVariant(prov.modLoc("block/thorn_stem")).with(BlockModelGenerators.UV_LOCK);
                MultiVariant connection = plainVariant(prov.modLoc("block/thorn_stem_connection"));
                MultiPartGenerator builder = MultiPartGenerator.multiPart(ctx.get())
                        .with(core);
                PipeBlock.PROPERTY_BY_DIRECTION.forEach((direction, value) -> {
                    VariantMutator partMutator = BlockModelGenerators.UV_LOCK;
                    if (direction.getAxis().isHorizontal()) {
                        int angleY = (int) direction.toYRot() % 360;
                        partMutator = partMutator.then(VariantMutator.Y_ROT.withValue(Quadrant.parseJson(angleY)))
                                .then(BlockModelGenerators.X_ROT_90);
                    } else {
                        if (direction == Direction.UP) {
                            partMutator = partMutator.then(BlockModelGenerators.X_ROT_180);
                        }
                    }
                    builder.with(BlockModelGenerators.condition().term(value, true), connection.with(partMutator));
                });
                prov.blockStateOutput.accept(builder);
            })
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .simpleItem()
            .register();

    public static final BlockEntry<BoringEndRodBlock> BORING_END_ROD = REGISTRATE.block("boring_end_rod", BoringEndRodBlock::new)
            .initialProperties(() -> Blocks.END_ROD)
            .defaultBlockstate()
            .blockstate(() -> (ctx, prov) -> Models.generateRodBlock(ctx, prov))
            .item()
            .model(() -> (ctx, prov) -> prov.createWithExistingModel(ctx.get(), prov.mcLoc("block/end_rod")))
            .build()
            .register();

    public static final BlockEntry<Block> RECYCLING_TABLE = REGISTRATE.block("recycling_table", Block::new)
            .initialProperties(() -> Blocks.CRAFTING_TABLE)
            .blockstate(() -> (ctx, prov) ->
                    prov.createTrivialBlock(ctx.get(), TexturedModel.CUBE_TOP.updateTexture(textureMapping -> {
                        textureMapping.put(TextureSlot.SIDE, prov.modLoc("block/recycling_table_side"));
                        textureMapping.put(TextureSlot.TOP, prov.modLoc("block/recycling_table_top"));
                    })))
            .simpleItem()
            .lang("Recycling Table")
            .defaultLoot()
            .register();

    public static final BlockEntry<Block> LIME_BLOCK = REGISTRATE.block("lime_block", Block::new)
            .initialProperties(() -> Blocks.MELON)
            .properties(p -> p.sound(SoundType.SLIME_BLOCK))
            .blockstate(() -> (ctx, prov) ->
                    prov.createTrivialBlock(ctx.get(), TexturedModel.CUBE_TOP.updateTexture(textureMapping -> {
                        textureMapping.put(TextureSlot.SIDE, prov.modLoc("block/lime_side"));
                        textureMapping.put(TextureSlot.TOP, prov.modLoc("block/lime_top"));
                    })))
            .lang("Block of Lime")
            .simpleItem()
            .recipe((ctx, prov) -> {
                DataIngredient lime = DataIngredient.tag(prov.itemLookup().getOrThrow(ExtraTags.Items.LIME));
                ShapelessRecipeBuilder.shapeless(prov.itemLookup(), RecipeCategory.FOOD, ctx.get().asItem())
                        .requires(lime.toVanilla(), 9)
                        .unlockedBy("has_lime", prov.has(ExtraTags.Items.LIME))
                        .save(prov);
            })
            .defaultLoot()
            .register();

    public static final BlockEntry<SlabBlock> SLICED_LIME = REGISTRATE.block("sliced_lime", SlabBlock::new)
            .initialProperties(LIME_BLOCK)
            .blockstate(() -> (ctx, prov) -> {
                ResourceLocation side = prov.modLoc("block/lime_side");
                ResourceLocation end = prov.modLoc("block/lime_top");
                ResourceLocation inside = prov.modLoc("block/lime_inside");
                TextureMapping bottomTextures = new TextureMapping()
                        .put(TextureSlot.SIDE, side)
                        .put(TextureSlot.BOTTOM, end)
                        .put(TextureSlot.TOP, inside);
                TextureMapping topTextures = new TextureMapping()
                        .put(TextureSlot.SIDE, side)
                        .put(TextureSlot.BOTTOM, inside)
                        .put(TextureSlot.TOP, end);
                prov.generateSlabBlock(ctx.get(),
                        plainVariant(ModelTemplates.SLAB_BOTTOM.create(ctx.get(), bottomTextures, prov.modelOutput)),
                        plainVariant(ModelTemplates.SLAB_TOP.create(ctx.get(), topTextures, prov.modelOutput)),
                        plainVariant(prov.modLoc("block/lime_block"))
                );
            })
            .loot((loot, block) -> loot.add(block, loot.createSlabItemTable(block)))
            .simpleItem()
            .recipe((ctx, prov) ->
                    prov.slab(DataIngredient.items((NonNullSupplier<? extends ItemLike>) LIME_BLOCK), RecipeCategory.BUILDING_BLOCKS, ctx, "lime_slab", true)
            )
            .register();

    public static final BlockEntry<Block> FULL_PATH = REGISTRATE
            .block("full_path", Block::new)
            .initialProperties(() -> Blocks.DIRT_PATH)
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .blockstate(() -> (ctx, prov) ->
                    prov.createTrivialBlock(ctx.get(), TexturedModel.CUBE.updateTexture(textureMapping -> {
                        textureMapping.put(TextureSlot.ALL, prov.mcLoc("block/dirt_path_top"));
                    })))
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> PACKED_MUD_SLAB = REGISTRATE
            .block( "packed_mud_slab", SlabBlock::new)
            .tag(BlockTags.STAIRS)
            .blockstate(() -> Models.slabBlock(Blocks.PACKED_MUD.builtInRegistryHolder(), Models.TextureType.normal()))
            .item()
            .tag(ItemTags.STAIRS)
            .build()
            .register();

    // Fusion dependent connected texture blocks - models generated by GenericFusionModelProvider

    public static final BlockEntry<Block> RED_SHIPPING_CONTAINER = REGISTRATE
            .block("red_shipping_container", Block::new)
            .initialProperties(() -> Blocks.BEDROCK)
            .properties(BlockBehaviour.Properties::noLootTable)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> YELLOW_SHIPPING_CONTAINER = REGISTRATE
            .block("yellow_shipping_container", Block::new)
            .initialProperties(() -> Blocks.BEDROCK)
            .properties(BlockBehaviour.Properties::noLootTable)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> BLUE_SHIPPING_CONTAINER = REGISTRATE
            .block("blue_shipping_container", Block::new)
            .initialProperties(() -> Blocks.BEDROCK)
            .properties(BlockBehaviour.Properties::noLootTable)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> GREEN_SHIPPING_CONTAINER = REGISTRATE
            .block("green_shipping_container", Block::new)
            .initialProperties(() -> Blocks.BEDROCK)
            .properties(BlockBehaviour.Properties::noLootTable)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> WAREHOUSE_ROAD = REGISTRATE
            .block("warehouse_road", Block::new)
            .initialProperties(() -> Blocks.BEDROCK)
            .properties(BlockBehaviour.Properties::noLootTable)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> WAREHOUSE_FLOOR = REGISTRATE
            .block("warehouse_floor", Block::new)
            .initialProperties(() -> Blocks.BEDROCK)
            .properties(BlockBehaviour.Properties::noLootTable)
            .simpleItem()
            .register();

    public static final BlockEntry<CarpetBlock> WAREHOUSE_PARKING_MARKING = REGISTRATE
            .block("warehouse_parking_marking", CarpetBlock::new)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(BlockBehaviour.Properties::noCollission)
            .properties(BlockBehaviour.Properties::noLootTable)
            .properties(BlockBehaviour.Properties::noTerrainParticles)
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatBlockItem(ctx.get()))
            .build()
            .register();

    public static final BlockEntry<WarehouseRoadBoosterBlock> WAREHOUSE_ROAD_BOOSTER = REGISTRATE
            .block("warehouse_road_booster", WarehouseRoadBoosterBlock::new)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(BlockBehaviour.Properties::noCollission)
            .properties(BlockBehaviour.Properties::noLootTable)
            .properties(BlockBehaviour.Properties::noTerrainParticles)
            .blockstate(() -> Models::generateWarehouseRoadBoosterBlock)
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatBlockItem(ctx.get()))
            .build()
            .register();

    // Speedy blocks

    private static final VoxelShape PATH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

    private static final TemplateBuilder<SpeedyBlock, BlockFactory<SpeedyBlock>> SPEEDY_BLOCK_TEMPLATES = new TemplateBuilder<SpeedyBlock, BlockFactory<SpeedyBlock>>()
            .add(Blocks.QUARTZ_BLOCK, SpeedyBlock::opaque)
            .add(Blocks.STONE_BRICKS, SpeedyBlock::opaque)
            .add(Blocks.CRACKED_STONE_BRICKS, SpeedyBlock::opaque)
            .add(Blocks.CRACKED_STONE_BRICKS, SpeedyBlock::opaque)
            .add(Blocks.SMOOTH_STONE, SpeedyBlock::opaque)
            .add(Blocks.GRAVEL, SpeedyBlock::opaque)
            .add(Blocks.DIRT_PATH, p -> SpeedyBlock.transparent(PATH_SHAPE, p))
            .add(Blocks.BLACK_CONCRETE_POWDER, SpeedyBlock::opaque)
            .add(Blocks.PACKED_MUD, SpeedyBlock::opaque)
            .add(Blocks.MUD_BRICKS, SpeedyBlock::opaque)
            .add(Blocks.ROOTED_DIRT, SpeedyBlock::opaque)
            .add(ResourceLocation.fromNamespaceAndPath("tropicraft", "chunk"), SpeedyBlock::opaque)
            .add(FULL_PATH, SpeedyBlock::opaque);

    public static final Map<Holder<Block>, BlockEntry<? extends SpeedyBlock>> SPEEDY_BLOCKS = SPEEDY_BLOCK_TEMPLATES
            .build((object, factory) -> REGISTRATE
                    .block("speedy_" + getName(object), factory)
                    .initialProperties(object::value)
                    .blockstate(() -> {
                        MultiVariant model = plainVariant(getId(object).withPrefix("block/"));
                        return (ctx, prov) -> prov.blockStateOutput.accept(createSimpleBlock(ctx.get(), model));
                    })
                    .simpleItem()
                    .register()
            );

    public static final BlockEntry<SpeedyBlock> SPEEDY_BROWN_MUSHROOM_BLOCK = REGISTRATE
            .block("speedy_brown_mushroom_block", SpeedyBlock::opaque)
            .initialProperties(() -> Blocks.BROWN_MUSHROOM_BLOCK)
            .blockstate(() -> (ctx, prov) -> {
                prov.createTrivialBlock(ctx.get(), TexturedModel.CUBE.updateTexture(textureMapping -> {
                    textureMapping.put(TextureSlot.ALL, prov.mcLoc("block/brown_mushroom_block"));
                }));
            })
            .simpleItem()
            .register();

    public static final BlockEntry<SpeedySlabBlock> SPEEDY_SPRUCE_SLAB = speedySlab(Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
    public static final BlockEntry<SpeedySlabBlock> SPEEDY_MUD_BRICKS_SLAB = speedySlab(Blocks.MUD_BRICK_SLAB, Blocks.MUD_BRICKS);
    public static final BlockEntry<SpeedySlabBlock> SPEEDY_PACKED_MUD_SLAB = speedySlab(LTExtras.location("packed_mud_slab"), () -> PACKED_MUD_SLAB.get(), Blocks.PACKED_MUD);

    private static BlockEntry<SpeedySlabBlock> speedySlab(Block slab, Block fullBlock) {
        return speedySlab(getId(slab.builtInRegistryHolder()), () -> slab, fullBlock);
    }

    private static BlockEntry<SpeedySlabBlock> speedySlab(ResourceLocation id, NonNullSupplier<Block> slab, Block fullBlock) {
        return REGISTRATE
                .block("speedy_" + id.getPath(), SpeedySlabBlock::new)
                .initialProperties(slab)
                .blockstate(() -> (ctx, prov) -> {
                    ResourceLocation donorModel = ModelLocationUtils.getModelLocation(slab.get());
                    MultiVariant modelVariant = plainVariant(ModelLocationUtils.getModelLocation(fullBlock));
                    MultiVariant sideVariant = plainVariant(donorModel);
                    MultiVariant topVariant = plainVariant(donorModel.withSuffix("_top"));
                    prov.blockStateOutput.accept(
                            BlockModelGenerators.createSlab(ctx.get(), sideVariant, topVariant, modelVariant)
                    );
                })
                .item()
                .model(() -> (ctx, prov) -> {
                    prov.createWithExistingModel(ctx.get(), id.withPrefix("block/"));
                })
                .build()
                .register();
    }

    public static final BlockEntry<SpeedyZone> SPEEDY_ZONE = REGISTRATE.block("speedy_zone", SpeedyZone::new)
            .blockstate(() -> (ctx, prov) -> prov.createAirLikeBlock(ctx.get(), prov.mcLoc("item/sugar")))
            .properties(p -> p.noOcclusion().noCollission().strength(-1.0F, 3600000.0F))
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatItem(ctx.get(), Items.SUGAR, ModelTemplates.FLAT_ITEM))
            .build()
            .register();

    // Imposter blocks

    public static final BlockEntry<Block> DELIGHTED_OBSIDIAN = REGISTRATE.block("delighted_obsidian", Block::new)
            .initialProperties(() -> Blocks.CRYING_OBSIDIAN)
            .properties(p -> p.lightLevel(value -> 0))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
            .blockstate(() -> (ctx, prov) -> prov.blockStateOutput.accept(createSimpleBlock(ctx.get(), plainVariant(ModelLocationUtils.getModelLocation(Blocks.CRYING_OBSIDIAN)))))
            .simpleItem()
            .register();

    private static final List<Block> CONCRETE_POWDERS = List.of(
            Blocks.WHITE_CONCRETE_POWDER,
            Blocks.ORANGE_CONCRETE_POWDER,
            Blocks.MAGENTA_CONCRETE_POWDER,
            Blocks.LIGHT_BLUE_CONCRETE_POWDER,
            Blocks.YELLOW_CONCRETE_POWDER,
            Blocks.LIME_CONCRETE_POWDER,
            Blocks.PINK_CONCRETE_POWDER,
            Blocks.GRAY_CONCRETE_POWDER,
            Blocks.LIGHT_GRAY_CONCRETE_POWDER,
            Blocks.CYAN_CONCRETE_POWDER,
            Blocks.PURPLE_CONCRETE_POWDER,
            Blocks.BLUE_CONCRETE_POWDER,
            Blocks.BROWN_CONCRETE_POWDER,
            Blocks.GREEN_CONCRETE_POWDER,
            Blocks.RED_CONCRETE_POWDER,
            Blocks.BLACK_CONCRETE_POWDER
    );

    private static final List<Block> CONCRETES = List.of(
            Blocks.WHITE_CONCRETE,
            Blocks.ORANGE_CONCRETE,
            Blocks.MAGENTA_CONCRETE,
            Blocks.LIGHT_BLUE_CONCRETE,
            Blocks.YELLOW_CONCRETE,
            Blocks.LIME_CONCRETE,
            Blocks.PINK_CONCRETE,
            Blocks.GRAY_CONCRETE,
            Blocks.LIGHT_GRAY_CONCRETE,
            Blocks.CYAN_CONCRETE,
            Blocks.PURPLE_CONCRETE,
            Blocks.BLUE_CONCRETE,
            Blocks.BROWN_CONCRETE,
            Blocks.GREEN_CONCRETE,
            Blocks.RED_CONCRETE,
            Blocks.BLACK_CONCRETE
    );

    private static final List<Block> TERRACOTTA_BLOCKS = List.of(
            Blocks.TERRACOTTA,
            Blocks.WHITE_TERRACOTTA,
            Blocks.ORANGE_TERRACOTTA,
            Blocks.MAGENTA_TERRACOTTA,
            Blocks.LIGHT_BLUE_TERRACOTTA,
            Blocks.YELLOW_TERRACOTTA,
            Blocks.LIME_TERRACOTTA,
            Blocks.PINK_TERRACOTTA,
            Blocks.GRAY_TERRACOTTA,
            Blocks.LIGHT_GRAY_TERRACOTTA,
            Blocks.CYAN_TERRACOTTA,
            Blocks.PURPLE_TERRACOTTA,
            Blocks.BLUE_TERRACOTTA,
            Blocks.BROWN_TERRACOTTA,
            Blocks.GREEN_TERRACOTTA,
            Blocks.RED_TERRACOTTA,
            Blocks.BLACK_TERRACOTTA,
            Blocks.WHITE_GLAZED_TERRACOTTA,
            Blocks.ORANGE_GLAZED_TERRACOTTA,
            Blocks.MAGENTA_GLAZED_TERRACOTTA,
            Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Blocks.YELLOW_GLAZED_TERRACOTTA,
            Blocks.LIME_GLAZED_TERRACOTTA,
            Blocks.PINK_GLAZED_TERRACOTTA,
            Blocks.GRAY_GLAZED_TERRACOTTA,
            Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
            Blocks.CYAN_GLAZED_TERRACOTTA,
            Blocks.PURPLE_GLAZED_TERRACOTTA,
            Blocks.BLUE_GLAZED_TERRACOTTA,
            Blocks.BROWN_GLAZED_TERRACOTTA,
            Blocks.GREEN_GLAZED_TERRACOTTA,
            Blocks.RED_GLAZED_TERRACOTTA,
            Blocks.BLACK_GLAZED_TERRACOTTA
    );

    private static final TemplateBuilder<Block, ImposterBlockTemplate> IMPOSTER_BLOCK_TEMPLATES = new TemplateBuilder<Block, ImposterBlockTemplate>()
            .add(Blocks.BRAIN_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
            .add(Blocks.BUBBLE_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
            .add(Blocks.HORN_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
            .add(Blocks.FIRE_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
            .add(Blocks.TUBE_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
            .add(CONCRETE_POWDERS, ImposterBlockTemplate.simpleCube())
            .add(Blocks.ICE, ImposterBlockTemplate.halfTransparentCube())
            .add(Blocks.BRAIN_CORAL, ImposterBlockTemplate.cross(ImposterCoralBlock::new))
            .add(Blocks.BUBBLE_CORAL, ImposterBlockTemplate.cross(ImposterCoralBlock::new))
            .add(Blocks.HORN_CORAL, ImposterBlockTemplate.cross(ImposterCoralBlock::new))
            .add(Blocks.FIRE_CORAL, ImposterBlockTemplate.cross(ImposterCoralBlock::new))
            .add(Blocks.TUBE_CORAL, ImposterBlockTemplate.cross(ImposterCoralBlock::new));

    public static final Map<Holder<Block>, BlockEntry<? extends Block>> IMPOSTER_BLOCKS = IMPOSTER_BLOCK_TEMPLATES
            .build((object, template) -> {
                        BlockBuilder<? extends Block, Registrate> block = REGISTRATE
                                .block("imposter_" + getName(object), template.factory)
                                .initialProperties(object::value);
                        return template.model.apply(block, getId(object)).register();
                    }
            );

    // Custom stairs/fences/walls/etc

    private static final TemplateBuilder<StairBlock, Models.TextureType> STAIR_TEMPLATES = new TemplateBuilder<StairBlock, Models.TextureType>()
            .add(Blocks.GOLD_BLOCK, Models.TextureType.normal())
            .add(Blocks.CRACKED_STONE_BRICKS, Models.TextureType.normal())
            .add(CONCRETE_POWDERS, Models.TextureType.normal())
            .add(CONCRETES, Models.TextureType.normal())
            .add(RUSTY_PAINTED_METAL, Models.TextureType.normal())
            .add(Blocks.MOSS_BLOCK, Models.TextureType.normal())
            .add(TERRACOTTA_BLOCKS, Models.TextureType.normal())
            .add(Blocks.CALCITE, Models.TextureType.normal())
            .add(Blocks.SOUL_SOIL, Models.TextureType.normal())
            .add(Blocks.TUFF, Models.TextureType.normal())
            .add(Blocks.HONEY_BLOCK, Models.TextureType.sideTopSuffix())
            .add(Blocks.HONEYCOMB_BLOCK, Models.TextureType.normal());

    private static final TemplateBuilder<SlabBlock, Models.TextureType> SLAB_TEMPLATES = new TemplateBuilder<SlabBlock, Models.TextureType>()
            .add(Blocks.GOLD_BLOCK, Models.TextureType.normal())
            .add(Blocks.CRACKED_STONE_BRICKS, Models.TextureType.normal())
            .add(CONCRETE_POWDERS, Models.TextureType.normal())
            .add(CONCRETES, Models.TextureType.normal())
            .add(RUSTY_PAINTED_METAL, Models.TextureType.normal())
            .add(Blocks.MOSS_BLOCK, Models.TextureType.normal())
            .add(TERRACOTTA_BLOCKS, Models.TextureType.normal())
            .add(Blocks.CALCITE, Models.TextureType.normal())
            .add(Blocks.SOUL_SOIL, Models.TextureType.normal())
            .add(Blocks.TUFF, Models.TextureType.normal())
            .add(Blocks.HONEY_BLOCK, Models.TextureType.sideTopSuffix())
            .add(Blocks.HONEYCOMB_BLOCK, Models.TextureType.normal());

    private static final TemplateBuilder<FenceBlock, Models.TextureType> FENCE_TEMPLATES = new TemplateBuilder<FenceBlock, Models.TextureType>()
            .add(Blocks.GOLD_BLOCK, Models.TextureType.normal())
            .add(Blocks.QUARTZ_BLOCK, Models.TextureType.sideTopSuffix())
            .add(Blocks.STONE, Models.TextureType.normal())
            .add(Blocks.STONE_BRICKS, Models.TextureType.normal())
            .add(Blocks.CRACKED_STONE_BRICKS, Models.TextureType.normal())
            .add(RUSTY_PAINTED_METAL, Models.TextureType.normal());

    private static final TemplateBuilder<WallBlock, Models.TextureType> WALL_TEMPLATES = new TemplateBuilder<WallBlock, Models.TextureType>()
            .add(Blocks.GOLD_BLOCK, Models.TextureType.normal())
            .add(Blocks.QUARTZ_BLOCK, Models.TextureType.sideTopSuffix())
            .add(Blocks.STONE, Models.TextureType.normal())
            .add(Blocks.CRACKED_STONE_BRICKS, Models.TextureType.normal())
            .add(Blocks.POLISHED_ANDESITE, Models.TextureType.normal())
            .add(Blocks.POLISHED_GRANITE, Models.TextureType.normal())
            .add(Blocks.POLISHED_DIORITE, Models.TextureType.normal())
            .add(Blocks.CALCITE, Models.TextureType.normal())
            .add(Blocks.SOUL_SOIL, Models.TextureType.normal())
            .add(Blocks.TUFF, Models.TextureType.normal())
            .add(Blocks.SMOOTH_QUARTZ, Models.TextureType.allWithSuffix(Blocks.QUARTZ_BLOCK, "bottom"))
            .add(RUSTY_PAINTED_METAL, Models.TextureType.normal())
            .add(TERRACOTTA_BLOCKS, Models.TextureType.normal())
            .add(CONCRETES, Models.TextureType.normal());

    public static final Map<Holder<Block>, BlockEntry<? extends StairBlock>> STAIRS = STAIR_TEMPLATES
            .build((object, textureType) -> REGISTRATE
                    .block(getName(object) + "_stairs", p -> new StairBlock(object.value().defaultBlockState(), p))
                    .initialProperties(object::value)
                    .tag(BlockTags.STAIRS)
                    .blockstate(() -> Models.stairsBlock(object, textureType))
                    .item()
                    .tag(ItemTags.STAIRS)
                    .build()
                    .register()
            );

    public static final Map<Holder<Block>, BlockEntry<? extends SlabBlock>> SLABS = SLAB_TEMPLATES
            .build((object, textureType) -> REGISTRATE
                    .block(getName(object) + "_slab", SlabBlock::new)
                    .initialProperties(object::value)
                    .tag(BlockTags.STAIRS)
                    .blockstate(() -> Models.slabBlock(object, textureType))
                    .item()
                    .tag(ItemTags.STAIRS)
                    .build()
                    .register()
            );

    public static final Map<Holder<Block>, BlockEntry<? extends FenceBlock>> FENCES = FENCE_TEMPLATES
            .build((block, textureType) -> REGISTRATE
                    .block(getName(block) + "_fence", FenceBlock::new)
                    .initialProperties(block::value)
                    .tag(BlockTags.FENCES)
                    .blockstate(() -> Models.fenceBlock(block, textureType))
                    .item()
                    .tag(ItemTags.FENCES)
                    .model(() -> (ctx, prov) -> {
                        ResourceLocation model = ModelTemplates.FENCE_INVENTORY.create(ctx.get(), TextureMapping.defaultTexture(Models.getMainTexture(block, textureType)), prov.modelOutput);
                        prov.createWithExistingModel(ctx.get(), model);
                    })
                    .build()
                    .register()
            );

    public static final Map<Holder<Block>, BlockEntry<? extends WallBlock>> WALLS = WALL_TEMPLATES
            .build((block, textureType) -> REGISTRATE
                    .block(getName(block) + "_wall", WallBlock::new)
                    .initialProperties(block::value)
                    .tag(BlockTags.WALLS)
                    .blockstate(() -> Models.wallBlock(block, textureType))
                    .item()
                    .tag(ItemTags.WALLS)
                    .model(() -> (ctx, prov) -> {
                        TextureMapping textures = TextureMapping.cube(Models.getMainTexture(block, textureType));
                        ResourceLocation itemModel = ModelTemplates.WALL_INVENTORY.create(ctx.get(), textures, prov.modelOutput);
                        prov.createWithExistingModel(ctx.get(), itemModel);
                    })
                    .build()
                    .register()
            );

    public static final BlockEntry<Block> GREEN_ANEMONE = anemoneBlock("green_anemone");
    public static final BlockEntry<Block> PURPLE_ANEMONE = anemoneBlock("purple_anemone");

    private static BlockEntry<Block> anemoneBlock(String name) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.MELON)
                .blockstate(() -> (ctx, prov) ->
                        prov.createTrivialBlock(ctx.get(), TexturedModel.COLUMN)
                )
                .simpleItem()
                .register();
    }

    public static final BlockEntry<BaseCoralFanBlock> ANEMONE_TENTACLES = REGISTRATE.block("anemone_tentacles", BaseCoralFanBlock::new)
            .initialProperties(() -> Blocks.BRAIN_CORAL_FAN)
            .blockstate(() -> (ctx, prov) ->
                    prov.createTrivialBlock(ctx.get(), TexturedModel.CORAL_FAN)
            )
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatBlockItem(ctx.get()))
            .build()
            .register();

    // Seagrasses

    private static BlockEntry<CustomSeagrassBlock> seagrass(String blockName) {
        return seagrass(blockName, null);
    }

    private static BlockEntry<CustomSeagrassBlock> seagrass(String blockName, @Nullable Supplier<Supplier<? extends TallSeagrassBlock>> tall) {
        return REGISTRATE.block(blockName, p -> new CustomSeagrassBlock(p, RegistrateLangProvider.toEnglishName(blockName), tall))
                .lang("Seagrass")
                .initialProperties(() -> Blocks.SEAGRASS)
                .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
                .blockstate(() -> (ctx, prov) ->
                        prov.createTrivialBlock(ctx.get(), TexturedModel.SEAGRASS)
                )
                .item()
                .model(() -> (ctx, prov) -> prov.generateFlatBlockItem(ctx.get()))
                .build()
                .register();
    }

    public static final BlockEntry<CustomSeagrassBlock> CYMODOCEA_ROTUNDATA = seagrass("cymodocea_rotundata");
    public static final BlockEntry<CustomSeagrassBlock> CYMODOCEA_SERRULATA = seagrass("cymodocea_serrulata");

    public static final BlockEntry<CustomSeagrassBlock> ENHALUS_ACOROIDES = seagrass("enhalus_acoroides", () -> ExtraBlocks.TALL_ENHALUS_ACOROIDES);
    public static final BlockEntry<CustomTallSeagrassBlock> TALL_ENHALUS_ACOROIDES = tallSeagrass(ENHALUS_ACOROIDES).register();
    public static final BlockEntry<Block> MATTED_ENHALUS_ACOROIDES = mattedSeagrassBlock("enhalus_acoroides");
    public static final BlockEntry<Block> ENHALUS_ACOROIDES_BLOCK = seagrassBlock("enhalus_acoroides");

    public static final BlockEntry<CustomSeagrassBlock> HALODULE_PINIFOLIA = seagrass("halodule_pinifolia");

    public static final BlockEntry<CustomSeagrassBlock> HALODULE_UNINERVIS = seagrass("halodule_uninervis", () -> ExtraBlocks.TALL_HALODULE_UNINERVIS);
    public static final BlockEntry<CustomTallSeagrassBlock> TALL_HALODULE_UNINERVIS = tallSeagrass(HALODULE_UNINERVIS).register();
    public static final BlockEntry<Block> MATTED_HALODULE_UNINERVIS = mattedSeagrassBlock("halodule_uninervis");
    public static final BlockEntry<Block> HALODULE_UNINERVIS_BLOCK = seagrassBlock("halodule_uninervis");

    public static final BlockEntry<CustomSeagrassBlock> HALOPHILA_OVALIS = seagrass("halophila_ovalis");

    public static final BlockEntry<CustomSeagrassBlock> HALOPHILA_SPINULOSA = seagrass("halophila_spinulosa", () -> ExtraBlocks.TALL_HALOPHILA_SPINULOSA);
    public static final BlockEntry<CustomTallSeagrassBlock> TALL_HALOPHILA_SPINULOSA = tallSeagrass(HALOPHILA_SPINULOSA).register();
    public static final BlockEntry<Block> MATTED_HALOPHILA_SPINULOSA = mattedSeagrassBlock("halophila_spinulosa");
    public static final BlockEntry<Block> HALOPHILA_SPINULOSA_BLOCK = seagrassBlock("halophila_spinulosa");

    public static final BlockEntry<CustomSeagrassBlock> SYRINGODIUM_ISOETIFOLIUM = seagrass("syringodium_isoetifolium");
    public static final BlockEntry<Block> MATTED_SYRINGODIUM_ISOETIFOLIUM = mattedSeagrassBlock("syringodium_isoetifolium");
    public static final BlockEntry<Block> SYRINGODIUM_ISOETIFOLIUM_BLOCK = seagrassBlock("syringodium_isoetifolium");

    public static final BlockEntry<CustomSeagrassBlock> THALASSIA_HEMPRICHII = seagrass("thalassia_hemprichii");

    public static final BlockEntry<CustomSeagrassBlock> THALASSODENDRON_CILIATUM = seagrass("thalassodendron_ciliatum", () -> ExtraBlocks.TALL_THALASSODENDRON_CILIATUM);
    public static final BlockEntry<CustomTallSeagrassBlock> TALL_THALASSODENDRON_CILIATUM = tallSeagrass(THALASSODENDRON_CILIATUM).register();
    public static final BlockEntry<Block> MATTED_THALASSODENDRON_CILIATUM = mattedSeagrassBlock("thalassodendron_ciliatum");
    public static final BlockEntry<Block> THALASSODENDRON_CILIATUM_BLOCk = seagrassBlock("thalassodendron_ciliatum");

    public static final BlockEntry<PapyrusStemBlock> PAPYRUS_STEM = REGISTRATE.block("papyrus_stem", PapyrusStemBlock::new)
            .initialProperties(() -> Blocks.SUGAR_CANE)
            .properties(p -> p.sound(SoundType.WOOD))
            .blockstate(() -> Models::generatePapyrusStem)
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .item()
            .model(() -> (ctx, prov) ->
                    prov.generateFlatItem(ctx.get(), prov.modLoc("block/papyrus/plain_papyrus_stem")))
            .build()
            .register();

    public static final BlockEntry<PapyrusUmbelBlock> PAPYRUS_UMBEL = REGISTRATE.block("papyrus_umbel", PapyrusUmbelBlock::new)
            .initialProperties(() -> Blocks.SUGAR_CANE)
            .properties(p -> p.sound(SoundType.FLOWERING_AZALEA))
            .blockstate(() -> ImposterBlockTemplate.Model::generatePapyrusUmbel)
            .item()
            .model(() -> (ctx, prov) ->
                    prov.generateFlatItem(ctx.get(), prov.modLoc("block/papyrus/plain_papyrus_umbel")))
            .build()
            .register();

    public static final BlockEntry<SubmergedLilyBlock> SUBMERGED_LILY_PAD = REGISTRATE.block("submerged_lily_pad", SubmergedLilyBlock::new)
            .lang("Submerged Lily Pad")
            .initialProperties(() -> Blocks.LILY_PAD)
            .color(() -> () -> (state, level, pos, index) -> level != null && pos != null ? 2129968 : 7455580)
            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
            .blockstate(() -> Models::generateLilyPad)
            .item((submergedLilyBlock, properties) -> new BlockItem(submergedLilyBlock, properties) {
                @Override
                public InteractionResult useOn(UseOnContext ctx) {
                    return InteractionResult.PASS;
                }

                @Override
                public InteractionResult place(BlockPlaceContext pContext) {
                    return pContext.getLevel().getFluidState(pContext.getClickedPos()).is(Fluids.WATER) ? super.place(pContext) : InteractionResult.FAIL;
                }

                @Override
                public InteractionResult use(Level leve, Player player, InteractionHand hand) {
                    BlockHitResult fluidHit = getPlayerPOVHitResult(leve, player, ClipContext.Fluid.SOURCE_ONLY);
                    return super.useOn(new UseOnContext(player, hand, fluidHit));
                }
            })
            .model(() -> Models::generateSubmergedLilyPadItem)
            .build()
            .register();

    public static final BlockEntry<Block> GRASS_GRASS = REGISTRATE.block("grass_grass", Block::new)
            .initialProperties(() -> Blocks.GRASS_BLOCK)
            .blockstate(() -> (ctx, prov) -> {
                prov.create(ctx.get(), prov.getBuilder()
                        .transformTemplate(template -> {
                            template.parent(prov.mcLoc("block/grass_block"));
                            template.element(elementBuilder ->
                                    elementBuilder.from(0, 0, 0).to(16, 16, 16)
                                            .allFaces((direction, faceBuilder) -> faceBuilder.texture(TextureSlot.TOP)
                                                    .uvs(0, 0, 16, 16).cullface(direction).tintindex(0)));
                        })
                        .texture(TextureSlot.DOWN, prov.mcLoc("block/grass_block_top"))
                        .texture(TextureSlot.UP, prov.mcLoc("block/grass_block_top"))
                        .texture(TextureSlot.NORTH, prov.mcLoc("block/grass_block_top"))
                        .texture(TextureSlot.SOUTH, prov.mcLoc("block/grass_block_top"))
                        .texture(TextureSlot.EAST, prov.mcLoc("block/grass_block_top"))
                        .texture(TextureSlot.WEST, prov.mcLoc("block/grass_block_top"))
                        .build(ctx.get()));
            })
            .color(() -> () -> (state, reader, pos, color) -> reader != null && pos != null
                    ? BiomeColors.getAverageGrassColor(reader, pos)
                    : FoliageColor.FOLIAGE_DEFAULT)
            .item()
            .model(() -> Models::generateGrassGrassItem)
            .build()
            .register();

    public static final BlockEntry<CurtainBlock> CURTAIN = REGISTRATE.block("curtain", p -> (CurtainBlock) new CurtainBlock(p) {
            })
            .initialProperties(() -> Blocks.GLASS_PANE)
            .tag(ExtraTags.Blocks.CREATE_MOVABLE_EMPTY_COLLIDER)
            .properties(p -> p.sound(SoundType.WOOL))
            .blockstate(() -> (ctx, prov) -> {
                ResourceLocation texture = prov.blockTexture(ctx.get());
                prov.generatePaneBlock(ctx.get(), texture, texture);
            })
            .addLayer(() -> () -> ChunkSectionLayer.SOLID)
            .item()
            .model(() -> (ctx, prov) -> prov.generateFlatBlockItem(ctx.get()))
            .build()
            .register();

    public static final BlockEntry<JumpPadBlock> JUMP_PAD = REGISTRATE.block("jump_pad", JumpPadBlock::new)
            .lang("Jump Pad")
            .initialProperties(() -> Blocks.STONE_SLAB)
            .blockstate(() -> (ctx, prov) -> {
                ResourceLocation modelBottom = createJumpPadModel(ctx, prov, false);
                ResourceLocation modelTop = createJumpPadModel(ctx, prov, true);
                ResourceLocation verticalModelBottom = prov.getBuilder().transformTemplate(template -> template.parent(modelBottom))
                        .texture(TextureSlot.TOP, prov.modLoc("block/jump_pad_top_vertical"))
                        .build(prov.modLoc("block/" + ctx.getName() + "_vertical"));
                ResourceLocation verticalModelTop = prov.getBuilder().transformTemplate(template -> template.parent(modelTop))
                        .texture(TextureSlot.TOP, prov.modLoc("block/jump_pad_top_vertical"))
                        .build(prov.modLoc("block/" + ctx.getName() + "_top_vertical"));
                prov.blockStateOutput.accept(dispatch(ctx.get())
                        .with(PropertyDispatch.initial(JumpPadBlock.FACING, JumpPadBlock.HALF).generate((direction, half) -> {
                            ResourceLocation selectedModel;
                            if (half == Half.TOP) {
                                selectedModel = direction == Direction.UP ? verticalModelTop : modelTop;
                            } else {
                                selectedModel = direction == Direction.UP ? verticalModelBottom : modelBottom;
                            }
                            return plainVariant(selectedModel).with(switch (direction) {
                                case Direction.EAST -> Y_ROT_90;
                                case Direction.SOUTH -> Y_ROT_180;
                                case Direction.WEST -> Y_ROT_270;
                                default -> NOP;
                            });
                        })));
            })
            .blockEntity(JumpPadBlockEntity::new)
            .build()
            .simpleItem()
            .register();

    public static final BlockEntry<TeleportPadBlock> TELEPORT_PAD = REGISTRATE.block("teleport_pad", TeleportPadBlock::new)
            .lang("Teleport Pad")
            .initialProperties(() -> Blocks.STONE_SLAB)
            .blockstate(() -> (ctx, prov) -> {
                ResourceLocation modelBottom = createTeleportPadModel(ctx, prov, false);
                ResourceLocation modelTop = createTeleportPadModel(ctx, prov, true);
                prov.blockStateOutput.accept(dispatch(ctx.get())
                        .with(PropertyDispatch.initial(TeleportPadBlock.HALF).generate(half -> {
                            ResourceLocation selectedModel = half == Half.TOP ? modelTop : modelBottom;
                            return plainVariant(selectedModel);
                        })));
            })
            .blockEntity(TeleportPadBlockEntity::new)
            .build()
            .simpleItem()
            .register();

    private static ResourceLocation createJumpPadModel(DataGenContext<Block, JumpPadBlock> ctx, RegistrateBlockModelGenerator prov, boolean top) {
        return prov.getBuilder()
                .transformTemplate(template -> {
                    template.parent(prov.mcLoc(top ? "block/slab_top" : "block/slab"));
                })
                .texture(TextureSlot.BOTTOM, prov.modLoc("block/jump_pad_bottom"))
                .texture(TextureSlot.SIDE, prov.modLoc("block/jump_pad_side"))
                .texture(TextureSlot.TOP, prov.modLoc("block/jump_pad_top"))
                .texture(TextureSlot.PARTICLE, prov.modLoc("block/jump_pad_side"))
                .build(prov.modLoc("block/" + (top ? ctx.getName() + "_top" : ctx.getName())));
    }

    private static ResourceLocation createTeleportPadModel(DataGenContext<Block, TeleportPadBlock> ctx, RegistrateBlockModelGenerator prov, boolean top) {
        return prov.getBuilder()
                .transformTemplate(template -> template.parent(prov.mcLoc(top ? "block/slab_top" : "block/slab")))
                .texture(TextureSlot.BOTTOM, prov.modLoc("block/teleport_pad_bottom"))
                .texture(TextureSlot.SIDE, prov.modLoc("block/teleport_pad_side"))
                .texture(TextureSlot.TOP, prov.modLoc("block/teleport_pad_top"))
                .texture(TextureSlot.PARTICLE, prov.modLoc("block/teleport_pad_side"))
                .build(prov.modLoc("block/" + (top ? ctx.getName() + "_top" : ctx.getName())));
    }

    public static final BlockEntityEntry<JumpPadBlockEntity> JUMP_PAD_ENTITY = BlockEntityEntry.cast(JUMP_PAD.getSibling(Registries.BLOCK_ENTITY_TYPE));
    public static final BlockEntityEntry<TeleportPadBlockEntity> TELEPORT_PAD_ENTITY = BlockEntityEntry.cast(TELEPORT_PAD.getSibling(Registries.BLOCK_ENTITY_TYPE));

    private static final TemplateBuilder<CeilingCarpetBlock, BlockFactory<CeilingCarpetBlock>> CEILING_CARPET_TEMPLATES = new TemplateBuilder<CeilingCarpetBlock, BlockFactory<CeilingCarpetBlock>>()
            .add(Blocks.SAND, CeilingCarpetBlock::new)
            .add(Blocks.GREEN_WOOL, CeilingCarpetBlock::new)
            .add(Blocks.MOSS_BLOCK, CeilingCarpetBlock::new);

    public static final Map<Holder<Block>, BlockEntry<? extends CeilingCarpetBlock>> CEILING_CARPET_BLOCKS = CEILING_CARPET_TEMPLATES
            .build((object, factory) -> REGISTRATE.block(getName(object) + "_ceiling_carpet", CeilingCarpetBlock::new)
                    .initialProperties(() -> Blocks.WHITE_CARPET)
                    .blockstate(() -> (ctx, prov) -> Models.generateCeilingCarpet(object, ctx, prov))
                    .simpleItem()
                    .register()
            );

    public static final Set<BlockEntry<SeatBlock>> SEAT_BLOCKS = Stream.of(DyeColor.values())
            .map(ExtraBlocks::seat)
            .collect(Collectors.toSet());

    private static BlockEntry<SeatBlock> seat(DyeColor dyeColor) {
        String color = dyeColor.getName();
        return REGISTRATE.block(dyeColor.getName() + "_seat", SeatBlock::new)
                .initialProperties(() -> Blocks.OAK_SLAB)
                .properties(p -> p.sound(SoundType.WOOL))
                .blockstate(() -> (ctx, prov) -> Models.generateSeat(ctx, prov, color))
                .color(() -> () -> (state, level, pos, index) -> dyeColor.getTextColor())
                .simpleItem()
                .register();
    }

    private static BlockEntry<Block> seagrassBlock(String name) {
        String scientificName = RegistrateLangProvider.toEnglishName(name);
        return REGISTRATE.<Block>block(name + "_block", properties -> new ScientificNameBlock(properties, scientificName))
                .initialProperties(() -> Blocks.SAND)
                .lang("Seagrass Block")
                .blockstate(() -> (ctx, prov) ->
                        prov.generateWithTemplate(ctx.get(), ModelTemplates.CUBE_ALL, TextureMapping.cube(prov.modLoc("block/matted_" + name + "_top")))
                )
                .simpleItem()
                .register();
    }

    private static BlockEntry<Block> mattedSeagrassBlock(String name) {
        String scientificName = RegistrateLangProvider.toEnglishName(name);
        return REGISTRATE.<Block>block("matted_" + name, properties -> new ScientificNameBlock(properties, scientificName))
                .initialProperties(() -> Blocks.SAND)
                .lang("Matted Seagrass Block")
                .blockstate(() -> (ctx, prov) ->
                        prov.generateWithTemplate(ctx.get(), ModelTemplates.CUBE_BOTTOM_TOP, TextureMapping.cubeBottomTop(ctx.get()).put(TextureSlot.BOTTOM, prov.modLoc("block/purified_sand")))
                )
                .simpleItem()
                .register();
    }

    private static BlockBuilder<CustomTallSeagrassBlock, Registrate> tallSeagrass(RegistryEntry<Block, ? extends SeagrassBlock> drop) {
        return REGISTRATE.block("tall_" + drop.getId().getPath(), p -> new CustomTallSeagrassBlock(p, drop))
                .initialProperties(drop)
                .loot((p, b) -> p.dropOther(b, drop.get()))
                .lang("Tall Seagrass")
                .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
                .blockstate(() -> Models::generateTallSeagrass);
    }

    public static final BlockEntry<AdjustableLampBlock> ADJUSTABLE_LAMP = REGISTRATE.block("adjustable_lamp", AdjustableLampBlock::new)
            .initialProperties(() -> Blocks.REDSTONE_LAMP)
            .properties(p -> p.lightLevel(AdjustableLampBlock.LIGHT_EMISSION))
            .blockstate(() -> (ctx, prov) -> {
                MultiVariant multivariant = plainVariant(TexturedModel.CUBE.create(Blocks.REDSTONE_LAMP, prov.modelOutput));
                MultiVariant multivariant1 = plainVariant(Models.createSuffixedVariant(Blocks.REDSTONE_LAMP, "_on", ModelTemplates.CUBE_ALL, TextureMapping::cube, prov.modelOutput));
                prov.blockStateOutput.accept(MultiVariantGenerator.dispatch(ctx.get()).with(createBooleanModelDispatch(BlockStateProperties.LIT, multivariant1, multivariant)));
            })
            .item().model(() -> (ctx, prov) -> Models.generateBlockItem(ctx, prov, Blocks.REDSTONE_LAMP)).build()
            .register();

    public static final BlockEntry<PlumbersTntBlock> PLUMBERS_TNT = REGISTRATE.block("plumbers_tnt", PlumbersTntBlock::new)
            .initialProperties(() -> Blocks.TNT)
            .lang("Plumbers' Tnt")
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE))
            .blockstate(() -> (ctx, prov) -> {
                prov.createTrivialBlock(ctx.get(), TexturedModel.CUBE_TOP_BOTTOM);
            })
            .item().model(() -> (ctx, prov) -> Models.generateBlockItem(ctx, prov, ctx.get().getBlock())).build()
            .register();

    public static final BlockEntry<DisplayBlock> DISPLAY_BLOCK = REGISTRATE.block("display_block", properties -> new DisplayBlock(properties))
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .simpleItem()
            .blockEntity(DisplayBlockEntity::new)
            .build()
            .register();

    public static final BlockEntityEntry<DisplayBlockEntity> DISPLAY_BLOCK_ENTITY = BlockEntityEntry.cast(DISPLAY_BLOCK.getSibling(Registries.BLOCK_ENTITY_TYPE));

    public static void init() {
    }

    private static String getName(Holder<?> holder) {
        return getId(holder).getPath();
    }

    private static ResourceLocation getId(Holder<?> holder) {
        return holder.unwrapKey().orElseThrow().location();
    }

    public static final class TemplateBuilder<T extends Block, P> {
        private final Map<Holder<Block>, P> templates = new Object2ObjectLinkedOpenHashMap<>();

        public TemplateBuilder<T, P> add(Block block, P parameter) {
            return add(block.builtInRegistryHolder(), parameter);
        }

        public TemplateBuilder<T, P> add(List<Block> blocks, P parameter) {
            for (Block block : blocks) {
                add(block, parameter);
            }
            return this;
        }

        public TemplateBuilder<T, P> add(ResourceLocation id, P parameter) {
            return add(DeferredHolder.create(Registries.BLOCK, id), parameter);
        }

        public TemplateBuilder<T, P> add(Holder<Block> block, P parameter) {
            String namespace = block.unwrapKey().orElseThrow().location().getNamespace();
            if (ModList.get().isLoaded(namespace)) {
                templates.put(block, parameter);
            } else {
                if (DatagenModLoader.isRunningDataGen()) {
                    throw new UnsupportedOperationException("All soft-dependent mods must be present for datagen! Missing: " + namespace);
                }
            }
            return this;
        }

        public Map<Holder<Block>, BlockEntry<? extends T>> build(
                BiFunction<Holder<Block>, P, BlockEntry<? extends T>> factory
        ) {
            return templates.entrySet().stream()
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            entry -> factory.apply(entry.getKey(), entry.getValue())
                    ));
        }
    }

    private static class Models {
        public static final TextureSlot BARS = TextureSlot.create("bars");
        public static final ModelTemplate IRON_BARS_POST = ModelTemplates.create("iron_bars_post", "_post", BARS, TextureSlot.PARTICLE);
        public static final ModelTemplate IRON_BARS_SIDE_ALT = ModelTemplates.create("iron_bars_side_alt", "_side_alt", BARS, TextureSlot.EDGE, TextureSlot.PARTICLE);
        public static final ModelTemplate IRON_BARS_SIDE = ModelTemplates.create("iron_bars_side", "_side", BARS, TextureSlot.EDGE, TextureSlot.PARTICLE);
        public static final ModelTemplate IRON_BARS_CAP_ALT = ModelTemplates.create("iron_bars_cap_alt", "_cap_alt", BARS, TextureSlot.EDGE, TextureSlot.PARTICLE);
        public static final ModelTemplate IRON_BARS_CAP = ModelTemplates.create("iron_bars_cap", "_cap", BARS, TextureSlot.EDGE, TextureSlot.PARTICLE);
        public static final ModelTemplate IRON_BARS_POST_ENDS = ModelTemplates.create("iron_bars_post_ends", "_post_ends", TextureSlot.EDGE, TextureSlot.PARTICLE);
        public static final TextureSlot GLOW_STICKS = TextureSlot.create("glow_sticks");
        public static final ModelTemplate SCAFFOLDING_STABLE = ModelTemplates.create("scaffolding_stable", "_stable", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.PARTICLE);
        public static final ModelTemplate SCAFFOLDING_UNSTABLE = ModelTemplates.create("scaffolding_unstable", "_unstable", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.PARTICLE);
        public static final ModelTemplate GIRDER = new ModelTemplate(
                Optional.of(
                        ModelLocationUtils.decorateBlockModelLocation("ltextras:girder_straight")
                ), Optional.empty(), TextureSlot.TEXTURE);
        public static final ModelTemplate LADDER = ModelTemplates.create("ladder", TextureSlot.TEXTURE);
        public static final ModelTemplate PAPYRUS_STEM = new ModelTemplate(Optional.of(
                ModelLocationUtils.decorateBlockModelLocation("ltextras:papyrus_stem")
        ), Optional.empty(), TextureSlot.ALL);

        private static ResourceLocation createSuffixedVariant(Block block, String suffix, ModelTemplate modelTemplate, Function<ResourceLocation, TextureMapping> textureMappingGetter, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
            return modelTemplate.createWithSuffix(block, suffix, textureMappingGetter.apply(TextureMapping.getBlockTexture(block, suffix)), modelOutput);
        }

        private static void generateBlockItem(DataGenContext<Item, ? extends Item> ctx, RegistrateItemModelGenerator prov, Block donorBlock) {
            prov.itemModelOutput.accept(ctx.get(), ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(donorBlock)));
        }

        private static void generateReeds(DataGenContext<Block, ReedsBlock> ctx, RegistrateBlockModelGenerator prov) {
            Function<String, Variant> models = Util.memoize(texture -> {
                ResourceLocation location = prov.modLoc("block/" + texture);
                return plainModel(ModelTemplates.CROP.create(location, TextureMapping.singleSlot(TextureSlot.CROP, location), prov.modelOutput));
            });
            prov.blockStateOutput.accept(MultiVariantGenerator.dispatch(ctx.get()).with(PropertyDispatch.initial(ReedsBlock.TYPE)
                    .generate(type -> variants(Arrays.stream(type.getTextures()).map(models).toArray(Variant[]::new)))
            ));
        }

        private static void generateSugarCane(DataGenContext<Block, CustomSugarCaneBlock> ctx, RegistrateBlockModelGenerator prov) {
            prov.blockStateOutput.accept(MultiVariantGenerator.dispatch(ctx.get()).with(PropertyDispatch.initial(CustomSugarCaneBlock.TYPE)
                    .generate(type -> {
                        ResourceLocation location = prov.modLoc("block/" + ctx.getName() + "_" + type.getSerializedName());
                        return plainVariant(ModelTemplates.CROP.create(location, TextureMapping.singleSlot(TextureSlot.CROP, location), prov.modelOutput));
                    })
            ));
        }

        private static void generateSeat(DataGenContext<Block, SeatBlock> ctx, RegistrateBlockModelGenerator prov, String color) {
            TextureMapping textures = new TextureMapping()
                    .put(TextureSlot.SIDE, prov.modLoc("block/seat/side_" + color))
                    .put(TextureSlot.TOP, prov.modLoc("block/seat/top_" + color))
                    .put(TextureSlot.BOTTOM, prov.mcLoc("block/oak_planks"));
            ResourceLocation model = ModelTemplates.SLAB_BOTTOM.create(ctx.get(), textures, prov.modelOutput);
            prov.blockStateOutput.accept(dispatch(ctx.get(), createRotatedVariants(plainModel(model))));
        }

        public static void generateSteelGirder(DataGenContext<Block, GirderBlock> ctx, RegistrateBlockModelGenerator prov) {
            ResourceLocation girderModel = GIRDER.create(ctx.get(), TextureMapping.defaultTexture(prov.modLoc("block/" + ctx.getName())), prov.modelOutput);

            MultiVariant variant = plainVariant(girderModel)
                    .with(UV_LOCK);

            MultiPartGenerator multiPartGenerator = MultiPartGenerator.multiPart(ctx.get())
                    .with(condition()
                                    .term(GirderBlock.PROPS.get(Direction.Axis.X), true)
                                    .build(),
                            variant.with(X_ROT_90.then(Y_ROT_90)))
                    .with(condition()
                                    .term(GirderBlock.PROPS.get(Direction.Axis.Y), true)
                                    .build(),
                            variant)
                    .with(condition()
                                    .term(GirderBlock.PROPS.get(Direction.Axis.Z), true)
                                    .build(),
                            variant.with(X_ROT_90))
                    .with(condition()
                                    .term(GirderBlock.PROPS.get(Direction.Axis.X), false)
                                    .term(GirderBlock.PROPS.get(Direction.Axis.Y), false)
                                    .term(GirderBlock.PROPS.get(Direction.Axis.Z), false)
                                    .build(),
                            variant.with(X_ROT_90.then(Y_ROT_90))
                                    .with(X_ROT_90));
            prov.blockStateOutput.accept(multiPartGenerator);
        }

        public static MultiVariant scaffoldingModel(DataGenContext<Block, ScaffoldingBlock> ctx, RegistrateBlockModelGenerator prov, ModelTemplate modelTemplate) {
            return plainVariant(modelTemplate.create(ctx.get(),
                    new TextureMapping()
                            .put(TextureSlot.BOTTOM, prov.modLoc("block/metal_scaffolding_bottom"))
                            .put(TextureSlot.SIDE, prov.modLoc("block/metal_scaffolding_side"))
                            .put(TextureSlot.TOP, prov.modLoc("block/metal_scaffolding_top"))
                            .put(TextureSlot.PARTICLE, prov.modLoc("block/metal_scaffolding_top")), prov.modelOutput));
        }

        public static void barsBlock(DataGenContext<Block, IronBarsBlock> ctx, RegistrateBlockModelGenerator prov) {
            MultiVariant cap = barsModel(prov, ctx, IRON_BARS_CAP);
            MultiVariant capAlt = barsModel(prov, ctx, IRON_BARS_CAP_ALT);
            MultiVariant side = barsModel(prov, ctx, IRON_BARS_SIDE);
            MultiVariant sideAlt = barsModel(prov, ctx, IRON_BARS_SIDE_ALT);
            MultiVariant postEnds = barsModel(prov, ctx, IRON_BARS_POST_ENDS);
            MultiVariant post = barsModel(prov, ctx, IRON_BARS_POST);
            prov.blockStateOutput.accept(
                    MultiPartGenerator.multiPart(ctx.get())
                            .with(postEnds)
                            .with(
                                    condition()
                                            .term(BlockStateProperties.NORTH, false)
                                            .term(BlockStateProperties.EAST, false)
                                            .term(BlockStateProperties.SOUTH, false)
                                            .term(BlockStateProperties.WEST, false),
                                    post
                            )
                            .with(
                                    condition()
                                            .term(BlockStateProperties.NORTH, true)
                                            .term(BlockStateProperties.EAST, false)
                                            .term(BlockStateProperties.SOUTH, false)
                                            .term(BlockStateProperties.WEST, false),
                                    cap
                            )
                            .with(
                                    condition()
                                            .term(BlockStateProperties.NORTH, false)
                                            .term(BlockStateProperties.EAST, true)
                                            .term(BlockStateProperties.SOUTH, false)
                                            .term(BlockStateProperties.WEST, false),
                                    cap.with(Y_ROT_90)
                            )
                            .with(
                                    condition()
                                            .term(BlockStateProperties.NORTH, false)
                                            .term(BlockStateProperties.EAST, false)
                                            .term(BlockStateProperties.SOUTH, true)
                                            .term(BlockStateProperties.WEST, false),
                                    capAlt
                            )
                            .with(
                                    condition()
                                            .term(BlockStateProperties.NORTH, false)
                                            .term(BlockStateProperties.EAST, false)
                                            .term(BlockStateProperties.SOUTH, false)
                                            .term(BlockStateProperties.WEST, true),
                                    capAlt.with(Y_ROT_90)
                            )
                            .with(condition().term(BlockStateProperties.NORTH, true), side)
                            .with(condition().term(BlockStateProperties.EAST, true), side.with(Y_ROT_90))
                            .with(condition().term(BlockStateProperties.SOUTH, true), sideAlt)
                            .with(condition().term(BlockStateProperties.WEST, true), sideAlt.with(Y_ROT_90))
            );
        }

        private static MultiVariant barsModel(RegistrateBlockModelGenerator prov, DataGenContext<Block, ?> ctx, ModelTemplate modelTemplate) {
            ResourceLocation tex = ModelLocationUtils.getModelLocation(ctx.get());
            return plainVariant(modelTemplate.create(ctx.get(),
                    new TextureMapping()
                            .put(BARS, tex)
                            .put(TextureSlot.EDGE, tex)
                            .put(TextureSlot.PARTICLE, tex), prov.modelOutput));
        }

        public static ResourceLocation getMainTexture(Holder<Block> block, TextureType texture) {
            return texture.getSideTexture(block);
        }

        public static <T extends StairBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockModelGenerator> stairsBlock(Holder<Block> object, TextureType textureType) {
            return (ctx, prov) -> {
                ResourceLocation side = textureType.getSideTexture(object);
                ResourceLocation top = textureType.getTopTexture(object);
                prov.generateStairsBlock(ctx.get(), side, top, top);
            };
        }

        public static <T extends SlabBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockModelGenerator> slabBlock(Holder<Block> object, TextureType textureType) {
            return (ctx, prov) -> {
                ResourceLocation model = textureType.getModel(object);
                ResourceLocation side = textureType.getSideTexture(object);
                ResourceLocation top = textureType.getTopTexture(object);
                prov.generateSlabBlock(ctx.get(), plainVariant(model), side, top, top);
            };
        }

        public static <T extends FenceBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockModelGenerator> fenceBlock(Holder<Block> object, TextureType textureType) {
            return (ctx, prov) -> {
                ResourceLocation topTexture = textureType.getTopTexture(object);
                prov.generateFenceBlock(ctx.get(), topTexture);
            };
        }

        public static <T extends WallBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockModelGenerator> wallBlock(Holder<Block> object, TextureType textureType) {
            return (ctx, prov) -> {
                ResourceLocation sideTexture = textureType.getSideTexture(object);
                prov.generateWallBlock(ctx.get(), sideTexture);
            };
        }

        public static void generateRodBlock(DataGenContext<Block, BoringEndRodBlock> ctx, RegistrateBlockModelGenerator prov) {
            prov.blockStateOutput
                    .accept(dispatch(ctx.get(),
                            plainVariant(ModelLocationUtils.getModelLocation(Blocks.END_ROD))).with(ROTATIONS_COLUMN_WITH_FACING));
        }

        private static void generateScaffolding(DataGenContext<Block, ScaffoldingBlock> ctx, RegistrateBlockModelGenerator prov) {
            prov.blockStateOutput.accept(
                    dispatch(ctx.get()).with(BlockModelGenerators.createBooleanModelDispatch(
                            ScaffoldingBlock.BOTTOM,
                            scaffoldingModel(ctx, prov, SCAFFOLDING_UNSTABLE),
                            scaffoldingModel(ctx, prov, SCAFFOLDING_STABLE)
                    ))
            );
        }

        private static void generateTallSeagrass(DataGenContext<Block, CustomTallSeagrassBlock> ctx, RegistrateBlockModelGenerator prov) {
            prov.createDoubleBlock(ctx.get(),
                    plainVariant(ModelTemplates.SEAGRASS.createWithSuffix(ctx.get(), "_top", TextureMapping.defaultTexture(TextureMapping.getBlockTexture(ctx.get(), "_top")), prov.modelOutput)),
                    plainVariant(ModelTemplates.SEAGRASS.createWithSuffix(ctx.get(), "_bottom", TextureMapping.defaultTexture(TextureMapping.getBlockTexture(ctx.get(), "_bottom")), prov.modelOutput))
            );
        }

        private static void generateGrassGrassItem(DataGenContext<Item, BlockItem> ctx, RegistrateItemModelGenerator prov) {
            prov.generateTintedModel(ctx.get(), prov.modLoc("block/grass_grass"), new Constant(0x91bd56));
        }

        private static void generateSubmergedLilyPadItem(DataGenContext<Item, ? extends Item> ctx, RegistrateItemModelGenerator prov) {
            ResourceLocation model = ModelTemplates.FLAT_ITEM.create(ctx.get(), TextureMapping.layer0(ResourceLocation.withDefaultNamespace("block/lily_pad")), prov.modelOutput);
            prov.generateTintedModel(ctx.get(), model, new Constant(0x71c35c));
        }

        private static void generateLilyPad(DataGenContext<Block, SubmergedLilyBlock> ctx, RegistrateBlockModelGenerator prov) {
            Variant variant = plainModel(ModelLocationUtils.getModelLocation(Blocks.LILY_PAD));
            prov.blockStateOutput.accept(dispatch(ctx.get(), createRotatedVariants(variant)));
        }

        private static void generateWaterBarrierItem(DataGenContext<Item, BlockItem> ctx, RegistrateItemModelGenerator prov) {
            ResourceLocation model = prov.generateLayeredItem(ctx.get(), ResourceLocation.withDefaultNamespace("block/water_still"), ResourceLocation.withDefaultNamespace("item/barrier"));
            prov.generateTintedModel(ctx.get(), model, new Constant(0x3f76e4));
        }

        private static void generateFakeWaterItem(DataGenContext<Item, BlockItem> ctx, RegistrateItemModelGenerator prov) {
            ResourceLocation model = ModelTemplates.FLAT_ITEM.create(ctx.get(), TextureMapping.layer0(ResourceLocation.withDefaultNamespace("block/water_still")), prov.modelOutput);
            prov.generateTintedModel(ctx.get(), model, new Constant(0x3f76e4));
        }

        private static void generatePapyrusStem(DataGenContext<Block, PapyrusStemBlock> ctx, RegistrateBlockModelGenerator prov) {
            prov.blockStateOutput.accept(dispatch(ctx.get())
                    .with(PropertyDispatch.initial(PapyrusStemBlock.TYPE)
                            .generate(type -> {
                                final String typeName = type.getSerializedName();
                                final String modelName = typeName + "_" + ctx.getName();
                                final ResourceLocation texture = prov.modLoc("block/papyrus/" + modelName);
                                ResourceLocation model = prov.getBuilder()
                                        .transformTemplate(template -> {
                                            template.parent(prov.modLoc("block/papyrus_stem"));
                                            template.renderType(prov.mcLoc("cutout"));
                                        }).texture(TextureSlot.ALL, texture).build(prov.modLoc("block/" + modelName));
                                return plainVariant(model);
                            })));
        }

        private static void generateInfertileVine(DataGenContext<Block, VineBlock> ctx, RegistrateBlockModelGenerator prov) {
            Map<Property<Boolean>, VariantMutator> properties = selectMultifaceProperties(ctx.get().defaultBlockState(), MultifaceBlock::getFaceProperty);
            ConditionBuilder emptyCondition = condition();
            properties.forEach((property, p_403916_) -> emptyCondition.term(property, false));
            MultiVariant sideVariant = plainVariant(ModelLocationUtils.getModelLocation(Blocks.VINE));
            MultiPartGenerator multiPart = MultiPartGenerator.multiPart(ctx.get());
            properties.forEach((property, rotation) -> {
                multiPart.with(condition().term(property, true), sideVariant.with(rotation));
                multiPart.with(emptyCondition, sideVariant.with(rotation));
            });
            prov.blockStateOutput.accept(multiPart);
        }

        private static void generateCeilingCarpet(Holder<Block> object, DataGenContext<Block, CeilingCarpetBlock> ctx, RegistrateBlockModelGenerator prov) {
            ResourceLocation model = prov.getBuilder()
                    .transformTemplate(template -> template.parent(prov.mcLoc("block/thin_block"))
                            .element(elementBuilder -> {
                                elementBuilder.from(0, 16, 0)
                                        .to(16, 16, 16)
                                        .face(Direction.DOWN, (faceBuilder) -> faceBuilder.texture(TextureSlot.PARTICLE).uvs(0, 0, 16, 16).cullface(Direction.DOWN).tintindex(0))
                                        .face(Direction.UP, (faceBuilder) -> faceBuilder.texture(TextureSlot.PARTICLE).uvs(0, 0, 16, 16).cullface(Direction.UP).tintindex(0))
                                        .face(Direction.NORTH, (faceBuilder) -> faceBuilder.texture(TextureSlot.PARTICLE).uvs(0, 15, 16, 16).cullface(Direction.NORTH).tintindex(0))
                                        .face(Direction.SOUTH, (faceBuilder) -> faceBuilder.texture(TextureSlot.PARTICLE).uvs(0, 15, 16, 16).cullface(Direction.SOUTH).tintindex(0))
                                        .face(Direction.WEST, (faceBuilder) -> faceBuilder.texture(TextureSlot.PARTICLE).uvs(0, 15, 16, 16).cullface(Direction.WEST).tintindex(0))
                                        .face(Direction.EAST, (faceBuilder) -> faceBuilder.texture(TextureSlot.PARTICLE).uvs(0, 15, 16, 16).cullface(Direction.EAST).tintindex(0));
                            }))
                    .texture(TextureSlot.PARTICLE, ModelLocationUtils.getModelLocation(object.value()))
                    .build(ctx.get());
            prov.create(ctx.get(), model);
        }

        private static void generateRope(DataGenContext<Block, RopeBlock> ctx, RegistrateBlockModelGenerator prov) {
            prov.blockStateOutput.accept(dispatch(ctx.get()).with(PropertyDispatch.initial(RopeBlock.KNOT).generate(knot -> {
                if (knot) {
                    return plainVariant(ModelTemplates.CROSS.create(ModelLocationUtils.getModelLocation(ctx.get(), "_knot"), TextureMapping.cross(prov.blockTexture(ctx.get(), "_knot")), prov.modelOutput));
                }
                return plainVariant(ModelTemplates.CROSS.create(ctx.get(), TextureMapping.cross(ctx.get()), prov.modelOutput));
            })));
        }

        private static void generateLadder(DataGenContext<Block, LadderBlock> ctx, RegistrateBlockModelGenerator prov, String texture) {
            TextureMapping textures = TextureMapping.defaultTexture(prov.modLoc("block/" + texture))
                    .copySlot(TextureSlot.TEXTURE, TextureSlot.PARTICLE);
            MultiVariant model = plainVariant(LADDER.create(ctx.get(), textures, prov.modelOutput));
            prov.blockStateOutput.accept(dispatch(ctx.get(), model).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
        }

        private static void generateInfertileVineItem(DataGenContext<Item, BlockItem> ctx, RegistrateItemModelGenerator prov) {
            ResourceLocation model = prov.generateLayeredItem(ctx.get(), ResourceLocation.withDefaultNamespace("block/vine"), ResourceLocation.withDefaultNamespace("item/barrier"));
            prov.generateTintedModel(ctx.get(), model, new Constant(FoliageColor.FOLIAGE_DEFAULT));
        }

        public static void generateRotatedHorizontalBlock(DataGenContext<Block, ConveyorBeltBlock> ctx, RegistrateBlockModelGenerator prov) {
            MultiVariant multivariant =  plainVariant(TexturedModel.GLAZED_TERRACOTTA.create(ctx.get(), prov.modelOutput));
            prov.blockStateOutput.accept(MultiVariantGenerator.dispatch(ctx.get(), multivariant).with(ROTATION_HORIZONTAL_FACING_ALT));
        }

        public static void generateWarehouseRoadBoosterBlock(DataGenContext<Block, WarehouseRoadBoosterBlock> ctx, RegistrateBlockModelGenerator prov) {
            //MultiVariant variant =  plainVariant(TexturedModel.GLAZED_TERRACOTTA.create(ctx.get(), prov.modelOutput));
            MultiVariant variant = plainVariant(ModelLocationUtils.getModelLocation(ctx.get()));
            prov.blockStateOutput.accept(MultiVariantGenerator.dispatch(ctx.get(), variant).with(ROTATION_HORIZONTAL_FACING_ALT));
        }

        public interface TextureType {
            static TextureType normal() {
                return TextureType.allTexture(block -> TextureMapping.getBlockTexture(block.value()));
            }

            static TextureType allWithSuffix(Block donor, String suffix) {
                return TextureType.allTexture(ignored -> {
                    Holder<Block> block = donor.builtInRegistryHolder();
                    return TextureMapping.getBlockTexture(block.value(), "_" + suffix);
                });
            }

            static TextureType sideTopSuffix() {
                return new TextureType() {
                    @Override
                    public ResourceLocation getModel(Holder<Block> block) {
                        return TextureMapping.getBlockTexture(block.value());
                    }

                    @Override
                    public ResourceLocation getSideTexture(Holder<Block> block) {
                        return TextureMapping.getBlockTexture(block.value(), "_side");
                    }

                    @Override
                    public ResourceLocation getTopTexture(Holder<Block> block) {
                        return TextureMapping.getBlockTexture(block.value(), "_top");
                    }
                };
            }

            static TextureType allTexture(ResourceLocation texture) {
                return allTexture(b -> texture);
            }

            static TextureType allTexture(Function<Holder<Block>, ResourceLocation> texture) {
                return simple(block -> TextureMapping.getBlockTexture(block.value()), texture);
            }

            static TextureType simple(Function<Holder<Block>, ResourceLocation> model, Function<Holder<Block>, ResourceLocation> texture) {
                return new TextureType() {
                    @Override
                    public ResourceLocation getModel(Holder<Block> block) {
                        return model.apply(block);
                    }

                    @Override
                    public ResourceLocation getSideTexture(Holder<Block> block) {
                        return texture.apply(block);
                    }

                    @Override
                    public ResourceLocation getTopTexture(Holder<Block> block) {
                        return texture.apply(block);
                    }
                };
            }

            ResourceLocation getModel(Holder<Block> block);

            ResourceLocation getSideTexture(Holder<Block> block);

            ResourceLocation getTopTexture(Holder<Block> block);
        }
    }
}
