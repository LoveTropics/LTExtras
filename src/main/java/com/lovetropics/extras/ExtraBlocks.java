package com.lovetropics.extras;

import com.lovetropics.extras.block.*;
import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.block.entity.ParticleEmitterBlockEntity;
import com.lovetropics.extras.data.ImposterBlockTemplate;
import com.lovetropics.extras.data.ModelGenUtil;
import com.lovetropics.extras.mixin.BlockPropertiesMixin;
import com.lovetropics.lib.block.CustomShapeBlock;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lovetropics.extras.data.ModelGenUtil.*;

public class ExtraBlocks {

	public static final Registrate REGISTRATE = LTExtras.registrate();

	// One-off custom blocks

	public static final BlockEntry<WaterBarrierBlock> WATER_BARRIER = REGISTRATE.block("water_barrier", WaterBarrierBlock::new)
			.initialProperties(() -> Blocks.BARRIER)
			.properties(p -> p.noLootTable())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
					prov.models().getBuilder(ctx.getName()).texture("particle", ResourceLocation.withDefaultNamespace("item/barrier"))))
			.item()
				.model((ctx, prov) -> prov.generated(ctx::getEntry, ResourceLocation.withDefaultNamespace("block/water_still"), ResourceLocation.withDefaultNamespace("item/barrier")))
				.build()
			.register();

	public static final BlockEntry<LightweightBarrierBlock> LIGHTWEIGHT_BARRIER = REGISTRATE.block("lightweight_barrier", LightweightBarrierBlock::new)
			.initialProperties(() -> Blocks.BARRIER)
			.properties(p -> p.strength(0.0F, 3.6e6f).noLootTable())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
					prov.models().getBuilder(ctx.getName()).texture("particle", ResourceLocation.withDefaultNamespace("item/barrier"))))
			.item()
			.model((ctx, prov) -> prov.generated(ctx::getEntry, ResourceLocation.withDefaultNamespace("item/barrier")))
			.build()
			.register();

	public static final BlockEntry<PassableBarrierBlock> PASSABLE_BARRIER = REGISTRATE.block("passable_barrier", PassableBarrierBlock::new)
			.initialProperties(() -> Blocks.BARRIER)
			.properties(p -> p.noLootTable())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
					prov.models().getBuilder(ctx.getName()).texture("particle", ResourceLocation.withDefaultNamespace("item/barrier"))))
			.item()
			.model((ctx, prov) -> prov.generated(ctx::getEntry, ResourceLocation.withDefaultNamespace("item/barrier")))
			.build()
			.register();

	public static final BlockEntry<FakeWaterBlock> FAKE_WATER = REGISTRATE.block("fake_water", FakeWaterBlock::new)
			.initialProperties(() -> Blocks.BARRIER)
			.properties(p -> p.noLootTable())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
					prov.models().getBuilder(ctx.getName()).texture("particle", ResourceLocation.withDefaultNamespace("block/water_still"))))
			.item()
				.model((ctx, prov) -> prov.generated(ctx::getEntry, ResourceLocation.withDefaultNamespace("block/water_still")))
				.build()
			.register();

	public static final BlockEntry<CustomShapeBlock> BUOY = REGISTRATE.block("buoy", p -> new CustomShapeBlock(
					Shapes.or(
							Block.box(2, 0, 2, 14, 3, 14),
							Block.box(3, 3, 3, 13, 14, 13)),
					p))
			.initialProperties(() -> Blocks.BEACON)
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models()
					.withExistingParent(ctx.getName(), ResourceLocation.withDefaultNamespace("block/block"))
						.ao(false)
						.texture("beacon", ResourceLocation.withDefaultNamespace("block/beacon"))
						.texture("base", ResourceLocation.withDefaultNamespace("block/dark_prismarine"))
						.texture("particle", ResourceLocation.withDefaultNamespace("block/dark_prismarine"))
						.element()
							.from(2, 0, 2)
							.to(14, 3, 14)
							.textureAll("#base")
							.face(Direction.DOWN).cullface(Direction.DOWN).end()
							.end()
						.element()
							.from(3, 3, 3)
							.to(13, 14, 13)
							.textureAll("#beacon")
							.end()))
			.item(PlaceOnWaterBlockItem::new).build()
			.register();

	public static final BlockEntry<PanelBlock> GLASS_PANEL = REGISTRATE.block("glass_panel", PanelBlock::new)
			.initialProperties(() -> Blocks.GLASS)
			.blockstate((ctx, prov) -> prov.directionalBlock(ctx.get(), prov.models()
					.trapdoorTop(ctx.getName(), prov.blockTexture(Blocks.GLASS))))
			.loot(RegistrateBlockLootTables::dropWhenSilkTouch)
			.addLayer(() -> RenderType::cutout)
			.item()
				.model((ctx, prov) -> prov.trapdoorBottom(ctx.getName(), prov.mcLoc("block/glass")))
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
			.blockstate(ModelGenUtil::steelGirderBlockstate)
			.simpleItem()
			.register();
	}

	public static final BlockEntry<CheckpointBlock> CHECKPOINT = REGISTRATE.block("checkpoint", CheckpointBlock::new)
			.initialProperties(() -> Blocks.BEDROCK)
			.properties(p -> p.noLootTable())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models()
				.getBuilder(ctx.getName()).texture("particle", prov.mcLoc("item/structure_void"))))
			.item()
				.model((ctx, prov) -> prov.generated(ctx::getEntry, ResourceLocation.withDefaultNamespace("item/structure_void")))
				.build()
			.register();

	public static final BlockEntry<ScaffoldingBlock> METAL_SCAFFOLDING = REGISTRATE.block("metal_scaffolding", p -> (ScaffoldingBlock) new ScaffoldingBlock(p) {
		@Override
		public boolean isScaffolding(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
			return true;
		}
	})
			.initialProperties(() -> Blocks.SCAFFOLDING)
			.blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry())
					.partialState().with(ScaffoldingBlock.BOTTOM, true)
						.addModels(scaffoldingModel(ctx, prov, "unstable"))
					.partialState().with(ScaffoldingBlock.BOTTOM, false)
						.addModels(scaffoldingModel(ctx, prov, "stable")))
			.addLayer(() -> RenderType::cutout)
			.tag(BlockTags.CLIMBABLE)
			.tag(BlockTags.MINEABLE_WITH_PICKAXE)
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.item(ScaffoldingBlockItem::new)
				.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("block/metal_scaffolding_stable")))
				.build()
			.register();

	public static final BlockEntry<IronBarsBlock> RUSTY_IRON_BARS = REGISTRATE.block("rusty_iron_bars", p -> (IronBarsBlock) new IronBarsBlock(p) {})
			.initialProperties(() -> Blocks.IRON_BARS)
			.blockstate((ctx, prov) -> barsBlock(ctx, prov))
			.addLayer(() -> RenderType::cutout)
			.tag(BlockTags.MINEABLE_WITH_PICKAXE)
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.item()
				.model((ctx, prov) -> prov.blockSprite(ctx))
				.build()
			.register();

	public static final BlockEntry<LadderBlock> METAL_LADDER = ladder("metal_ladder", "metal_ladder").register();
	public static final BlockEntry<LadderBlock> RUSTY_METAL_LADDER = ladder("rusty_metal_ladder", "rusty_metal_ladder").register();
	public static final BlockEntry<LadderBlock> FAST_METAL_LADDER = ladder("fast_metal_ladder", "metal_ladder").tag(ExtraTags.Blocks.CLIMBABLE_FAST).register();
	public static final BlockEntry<LadderBlock> FAST_RUSTY_METAL_LADDER = ladder("fast_rusty_metal_ladder", "rusty_metal_ladder").tag(ExtraTags.Blocks.CLIMBABLE_FAST).register();
	private static final BlockBuilder<LadderBlock, Registrate> ladder(String name, String texture) {
		return REGISTRATE.block(name, p -> (LadderBlock) new LadderBlock(p) {}).initialProperties(() -> Blocks.IRON_BARS)
				.tag(BlockTags.CLIMBABLE)
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_IRON_TOOL)
				.blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
						.withExistingParent(ctx.getName(), "block/ladder")
						.texture("texture", prov.modLoc("block/" + texture))
						.texture("particle", "#texture")))
				.addLayer(() -> RenderType::cutout)
				.item()
				.model((ctx, prov) -> prov.blockSprite(ctx, prov.modLoc("block/" + texture)))
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

	public static final BlockEntityEntry<ParticleEmitterBlockEntity> PARTICLE_EMITTER_BE = BlockEntityEntry.cast(PARTICLE_EMITTER.getSibling(Registries.BLOCK_ENTITY_TYPE));

	public static final BlockEntry<Block> BLACK_CONCRETE_POWDER_FAKE = REGISTRATE.block("black_concrete_powder_fake", Block::new)
			.initialProperties(() -> Blocks.DIRT)
			.item()
				.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), ResourceLocation.withDefaultNamespace("block/black_concrete_powder")))
				.build()
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), ConfiguredModel.allYRotations(prov.models().getExistingFile(ResourceLocation.withDefaultNamespace("block/black_concrete_powder")), 0, false)))
			.register();

	public static final BlockEntry<StainedGlassBlock> SMOOTH_LIGHT_GRAY_STAINED_GLASS = REGISTRATE.block("smooth_light_gray_stained_glass", p -> new StainedGlassBlock(DyeColor.LIGHT_GRAY, p))
			.initialProperties(() -> Blocks.LIGHT_GRAY_STAINED_GLASS)
			.loot(RegistrateBlockLootTables::dropWhenSilkTouch)
			.addLayer(() -> RenderType::translucent)
			.simpleItem()
			.register();

	public static final BlockEntry<ReedsBlock> REEDS = REGISTRATE.block("reeds", ReedsBlock::new)
			.initialProperties(() -> Blocks.SUGAR_CANE)
			.properties(p -> p.noLootTable())
			.blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry())
					.forAllStates(state -> {
						ReedsBlock.Type type = state.getValue(ReedsBlock.TYPE);
						ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();

						String[] textures = type.getTextures();
						for (int i = 0; i < textures.length; i++) {
							String texture = textures[i];
							ResourceLocation textureLoc = prov.modLoc("block/" + texture);
							builder.modelFile(prov.models()
									.withExistingParent(texture, "block/crop")
									.texture("crop", textureLoc)
									.texture("particle", textureLoc)
							);
							if (i < textures.length - 1) {
								builder = builder.nextModel();
							}
						}

						return builder.build();
					}))
			.addLayer(() -> RenderType::cutout)
			.item()
				.model((ctx, prov) -> prov.generated(ctx::getEntry, prov.modLoc("block/reeds_top_tall")))
				.build()
			.register();

	public static final BlockEntry<CustomSugarCaneBlock> SUGAR_CANE = REGISTRATE.block("sugar_cane", CustomSugarCaneBlock::new)
			.initialProperties(() -> Blocks.SUGAR_CANE)
			.blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
				CustomSugarCaneBlock.Type type = state.getValue(CustomSugarCaneBlock.TYPE);
				String modelName = ctx.getName() + "_" + type.getSerializedName();
				return ConfiguredModel.builder().modelFile(prov.models()
						.withExistingParent(modelName, "block/crop")
						.texture("crop", prov.modLoc("block/" + modelName))
						.texture("particle", "#crop")
				).build();
			}))
			.addLayer(() -> RenderType::cutout)
			.defaultLoot()
			.item()
			.model((ctx, prov) -> prov.generated(ctx::getEntry, prov.modLoc("block/sugar_cane_top")))
			.build()
			.register();

	public static final BlockEntry<PianguasBlock> PIANGUAS = REGISTRATE.block("pianguas", PianguasBlock::new)
			.properties(p -> p.mapColor(MapColor.STONE).noCollission().instabreak().instrument(NoteBlockInstrument.BASEDRUM))
			.blockstate((ctx, prov) -> {
				BlockModelBuilder model = prov.models().getBuilder("pianguas")
						.ao(false)
						.texture("base", "block/pianguas")
						.texture("particle", "block/pianguas")
						.element()
							.from(0, 0, 0.1F)
							.to(16, 16, 0.1F)
							.face(Direction.SOUTH)
								.uvs(16, 0, 0, 16)
								.texture("#base")
								.end()
						.end();

				MultiPartBlockStateBuilder builder = prov.getMultipartBuilder(ctx.getEntry());
				PianguasBlock.ATTACHMENTS.forEach((direction, property) -> {
					if (direction.getAxis().isHorizontal()) {
						int rotationY = (((int) direction.toYRot()) + 180) % 360;
						builder.part().modelFile(model).rotationY(rotationY).uvLock(true).addModel()
								.condition(property, true);
					} else {
						int rotationX = direction == Direction.DOWN ? 90 : 270;
						builder.part().modelFile(model).rotationX(rotationX).uvLock(true).addModel()
								.condition(property, true);
					}
				});
			})
			.addLayer(() -> RenderType::cutout)
			.item()
				.model((ctx, prov) -> prov.generated(ctx::getEntry, prov.modLoc("block/pianguas")))
				.build()
			.register();

	public static final BlockEntry<RopeBlock> OLD_ROPE = rope("old_rope").register();
	public static final BlockEntry<RopeBlock> PARACORD = rope("paracord").tag(ExtraTags.Blocks.CLIMBABLE_VERY_FAST).register();

	private static BlockBuilder<RopeBlock, Registrate> rope(String name) {
		return REGISTRATE.block(name, RopeBlock::new)
				.properties(p -> p.mapColor(MapColor.WOOL).instabreak().noCollission().sound(SoundType.WOOL).ignitedByLava())
				.blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry())
						.forAllStatesExcept(state -> {
							String modelName = state.getValue(RopeBlock.KNOT) ? name + "_knot" : name;
							return ConfiguredModel.builder()
									.modelFile(prov.models().cross(modelName, prov.modLoc("block/" + modelName)))
									.build();
						}, RopeBlock.WATERLOGGED))
				.addLayer(() -> RenderType::cutout)
				.tag(BlockTags.CLIMBABLE)
				.item()
					.model((ctx, prov) -> prov.generated(ctx::getEntry, prov.modLoc("block/" + name + "_knot")))
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
						.blockstate((ctx, prov) -> {
							BlockModelBuilder model = prov.models().withExistingParent(name, prov.modLoc("block/glow_sticks"))
									.texture("glow_sticks", prov.modLoc("block/glow_sticks/" + dyeName));
							prov.simpleBlock(ctx.get(), ConfiguredModel.allYRotations(model, 0, false));
						})
						.addLayer(() -> RenderType::translucent)
						.simpleItem()
						.register();
			}));

	public static final BlockEntry<VineBlock> INFERTILE_VINE = REGISTRATE.block("infertile_vine", VineBlock::new)
			.initialProperties(() -> Blocks.VINE)
			// Mixin annoyance, accessor setters can't return self
			.properties(p -> { ((BlockPropertiesMixin)p).setIsRandomlyTicking(false); return p; })
			.tag(BlockTags.CLIMBABLE)
			.blockstate((ctx, prov) -> {}) // NO-OP, it's easier to just copy the file out of vanilla...
			.addLayer(() -> RenderType::cutout)
			.color(() -> () -> (state, reader, pos, color) -> reader != null && pos != null
					? BiomeColors.getAverageFoliageColor(reader, pos)
					: FoliageColor.getDefaultColor())
			.item()
				.model((ctx, prov) -> prov.generated(ctx, ResourceLocation.withDefaultNamespace("block/vine"), ResourceLocation.withDefaultNamespace("item/barrier")))
				.color(() -> () -> ($, layer) -> layer == 0 ? FoliageColor.getDefaultColor() : -1)
				.build()
			.register();

	public static final BlockEntry<DoorBlock> HEAVY_SPRUCE_DOOR = REGISTRATE.block("heavy_spruce_door", p -> new DoorBlock(BlockSetType.IRON, p))
			.initialProperties(() -> Blocks.SPRUCE_DOOR)
			.blockstate((ctx, prov) -> {})
			.addLayer(() -> RenderType::cutout)
			.tag(BlockTags.MINEABLE_WITH_AXE)
			.item()
			.model((ctx, prov) -> prov.generated(ctx, ResourceLocation.withDefaultNamespace("item/spruce_door")))
			.build()
			.register();

	public static final BlockEntry<ThornStemBlock> THORN_STEM = REGISTRATE.block("thorn_stem", ThornStemBlock::new)
			.initialProperties(() -> Blocks.ACACIA_LEAVES)
			.properties(p -> p.noOcclusion().isRedstoneConductor((state, world, pos) -> false))
			.blockstate((ctx, prov) -> {
				ModelFile.ExistingModelFile core = prov.models().getExistingFile(prov.modLoc("block/thorn_stem"));
				ModelFile.ExistingModelFile connection = prov.models().getExistingFile(prov.modLoc("block/thorn_stem_connection"));
				MultiPartBlockStateBuilder multipart = prov.getMultipartBuilder(ctx.get())
						.part().modelFile(core).addModel().end();
				PipeBlock.PROPERTY_BY_DIRECTION.forEach((direction, value) -> {
					ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> part = multipart.part()
							.modelFile(connection).uvLock(true);

					if (direction.getAxis().isHorizontal()) {
						int angleY = (int) direction.toYRot() % 360;
						part.rotationY(angleY);
						part.rotationX(90);
					} else {
						part.rotationX(direction == Direction.DOWN ? 0 : 180);
					}

					part
							.addModel()
							.condition(value, true);
				});
			})
			.addLayer(() -> RenderType::cutout)
			.simpleItem()
			.register();

    public static final BlockEntry<BoringEndRodBlock> BORING_END_ROD = REGISTRATE.block("boring_end_rod", BoringEndRodBlock::new)
			.initialProperties(() -> Blocks.END_ROD)
			.defaultBlockstate()
			.blockstate((ctx, prov) -> rodBlock(ctx, prov))
			.item()
			.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.mcLoc("block/end_rod")))
			.build()
            .register();

	public static final BlockEntry<Block> LIME_BLOCK = REGISTRATE.block("lime_block", Block::new)
			.initialProperties(() -> Blocks.MELON)
			.properties(p -> p.sound(SoundType.SLIME_BLOCK))
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().cubeColumn(ctx.getName(), prov.modLoc("block/lime_side"), prov.modLoc("block/lime_top"))))
			.lang("Block of Lime")
			.simpleItem()
			.recipe((ctx, prov) -> {
				DataIngredient lime = DataIngredient.tag(ExtraTags.Items.LIME);
				ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ctx.get())
						.requires(lime.toVanilla(), 9)
						.unlockedBy("has_lime", RegistrateRecipeProvider.has(ExtraTags.Items.LIME))
						.save(prov);
			})
			.defaultLoot()
			.register();

	public static final BlockEntry<SlabBlock> SLICED_LIME = REGISTRATE.block("sliced_lime", SlabBlock::new)
			.initialProperties(LIME_BLOCK)
			.blockstate((ctx, prov) -> {
				ResourceLocation side = prov.modLoc("block/lime_side");
				ResourceLocation end = prov.modLoc("block/lime_top");
				ResourceLocation inside = prov.modLoc("block/lime_inside");
				prov.slabBlock(ctx.get(),
						prov.models().slab(ctx.getName(), side, end, inside),
						prov.models().slabTop(ctx.getName() + "_top", side, inside, end),
						prov.models().getExistingFile(prov.modLoc("block/lime_block"))
				);
			})
			.loot((loot, block) -> loot.add(block, loot.createSlabItemTable(block)))
			.simpleItem()
			.recipe((ctx, prov) ->
					prov.slab(DataIngredient.items((NonNullSupplier<? extends ItemLike>) LIME_BLOCK), RecipeCategory.BUILDING_BLOCKS, ctx, "lime_slab", true)
			)
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
			.add(Blocks.SPRUCE_SLAB, SpeedyBlock::slab)
			.add(Blocks.PACKED_MUD, SpeedyBlock::opaque)
			.add(Blocks.MUD_BRICKS, SpeedyBlock::opaque)
			.add(Blocks.MUD_BRICK_SLAB, SpeedyBlock::opaque)
			.add(ResourceLocation.fromNamespaceAndPath("tropicraft", "chunk"), SpeedyBlock::opaque);

	public static final Map<Holder<Block>, BlockEntry<? extends SpeedyBlock>> SPEEDY_BLOCKS = SPEEDY_BLOCK_TEMPLATES
			.build((object, factory) -> REGISTRATE
					.block("speedy_" + getName(object), factory)
					.initialProperties(object::value)
					.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().getExistingFile(getId(object))))
					.simpleItem()
					.register()
			);

	// Imposter blocks

	public static final BlockEntry<Block> DELIGHTED_OBSIDIAN = REGISTRATE.block("delighted_obsidian", Block::new)
			.initialProperties(() -> Blocks.CRYING_OBSIDIAN)
			.properties(p -> p.lightLevel(value -> 0))
			.tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().withExistingParent(ctx.getName(), "block/crying_obsidian")))
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
			.add(Blocks.TUBE_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
			.add(CONCRETE_POWDERS, ImposterBlockTemplate.simpleCube())
			.add(Blocks.BRAIN_CORAL, ImposterBlockTemplate.cross(ImposterCoralBlock::new))
			.add(Blocks.BUBBLE_CORAL, ImposterBlockTemplate.cross(ImposterCoralBlock::new))
			.add(Blocks.HORN_CORAL, ImposterBlockTemplate.cross(ImposterCoralBlock::new))
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

	private static final TemplateBuilder<StairBlock, TextureType> STAIR_TEMPLATES = new TemplateBuilder<StairBlock, TextureType>()
			.add(Blocks.GOLD_BLOCK, TextureType.normal())
			.add(Blocks.CRACKED_STONE_BRICKS, TextureType.normal())
			.add(CONCRETE_POWDERS, TextureType.normal())
			.add(RUSTY_PAINTED_METAL, TextureType.normal())
			.add(Blocks.MOSS_BLOCK, TextureType.normal())
			.add(TERRACOTTA_BLOCKS, TextureType.normal());

	private static final TemplateBuilder<SlabBlock, TextureType> SLAB_TEMPLATES = new TemplateBuilder<SlabBlock, TextureType>()
			.add(Blocks.GOLD_BLOCK, TextureType.normal())
			.add(Blocks.CRACKED_STONE_BRICKS, TextureType.normal())
			.add(CONCRETE_POWDERS, TextureType.normal())
			.add(RUSTY_PAINTED_METAL, TextureType.normal())
			.add(Blocks.MOSS_BLOCK, TextureType.normal())
			.add(TERRACOTTA_BLOCKS, TextureType.normal());

	private static final TemplateBuilder<FenceBlock, TextureType> FENCE_TEMPLATES = new TemplateBuilder<FenceBlock, TextureType>()
			.add(Blocks.GOLD_BLOCK, TextureType.normal())
			.add(Blocks.QUARTZ_BLOCK, TextureType.sideTopSuffix())
			.add(Blocks.STONE, TextureType.normal())
			.add(Blocks.STONE_BRICKS, TextureType.normal())
			.add(Blocks.CRACKED_STONE_BRICKS, TextureType.normal())
			.add(RUSTY_PAINTED_METAL, TextureType.normal());

	private static final TemplateBuilder<WallBlock, TextureType> WALL_TEMPLATES = new TemplateBuilder<WallBlock, TextureType>()
			.add(Blocks.GOLD_BLOCK, TextureType.normal())
			.add(Blocks.QUARTZ_BLOCK, TextureType.sideTopSuffix())
			.add(Blocks.STONE, TextureType.normal())
			.add(Blocks.CRACKED_STONE_BRICKS, TextureType.normal())
			.add(Blocks.POLISHED_ANDESITE, TextureType.normal())
			.add(Blocks.POLISHED_GRANITE, TextureType.normal())
			.add(Blocks.POLISHED_DIORITE, TextureType.normal())
			.add(RUSTY_PAINTED_METAL, TextureType.normal())
			.add(TERRACOTTA_BLOCKS, TextureType.normal());

	public static final Map<Holder<Block>, BlockEntry<? extends StairBlock>> STAIRS = STAIR_TEMPLATES
			.build((object, textureType) -> REGISTRATE
					.block(getName(object) + "_stairs", p -> new StairBlock(object.value().defaultBlockState(), p))
					.initialProperties(object::value)
					.tag(BlockTags.STAIRS)
					.blockstate(stairsBlock(object, textureType))
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
					.blockstate(slabBlock(object, textureType))
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
					.blockstate(fenceBlock(block, textureType))
					.item()
						.tag(ItemTags.FENCES)
						.model((ctx, prov) -> prov.fenceInventory(ctx.getName(), getMainTexture(block, textureType)))
						.build()
					.register()
			);

	public static final Map<Holder<Block>, BlockEntry<? extends WallBlock>> WALLS = WALL_TEMPLATES
			.build((block, textureType) -> REGISTRATE
					.block(getName(block) + "_wall", WallBlock::new)
					.initialProperties(block::value)
					.tag(BlockTags.WALLS)
					.blockstate(wallBlock(block, textureType))
					.item()
						.tag(ItemTags.WALLS)
						.model((ctx, prov) -> prov.wallInventory(ctx.getName(), getMainTexture(block, textureType)))
						.build()
					.register()
			);

	public static final BlockEntry<Block> GREEN_ANEMONE = anemoneBlock("green_anemone");
	public static final BlockEntry<Block> PURPLE_ANEMONE = anemoneBlock("purple_anemone");

	private static BlockEntry<Block> anemoneBlock(String name) {
		return REGISTRATE.block(name, Block::new)
				.initialProperties(() -> Blocks.MELON)
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
						.cubeColumn(ctx.getName(),
								prov.modLoc("block/" + ctx.getName() + "_side"),
								prov.modLoc("block/" + ctx.getName() + "_top"))))
				.simpleItem()
				.register();
	}

	public static final BlockEntry<BaseCoralFanBlock> ANEMONE_TENTACLES = REGISTRATE.block("anemone_tentacles", BaseCoralFanBlock::new)
			.initialProperties(() -> Blocks.BRAIN_CORAL_FAN)
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
					.withExistingParent(ctx.getName(), "block/coral_fan")
					.texture("fan", prov.modLoc("block/" + ctx.getName()))))
			.addLayer(() -> RenderType::cutout)
			.item()
				.model((ctx, prov) -> prov.blockSprite(ctx))
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
				.addLayer(() -> RenderType::cutout)
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models()
						.withExistingParent(ctx.getName(), "block/template_seagrass")
						.texture("texture", prov.blockTexture(ctx.getEntry()))
						.texture("particle", prov.blockTexture(ctx.getEntry()))))
				.addLayer(() -> RenderType::cutout)
				.item()
					.model((ctx, prov) -> prov.blockSprite(ctx))
				.build()
				.register();
	}

	public static final BlockEntry<CustomSeagrassBlock> CYMODOCEA_ROTUNDATA = seagrass("cymodocea_rotundata");
	public static final BlockEntry<CustomSeagrassBlock> CYMODOCEA_SERRULATA = seagrass("cymodocea_serrulata");

	public static final BlockEntry<CustomSeagrassBlock> ENHALUS_ACOROIDES = seagrass("enhalus_acoroides", () -> ExtraBlocks.TALL_ENHALUS_ACOROIDES);
	public static final BlockEntry<CustomTallSeagrassBlock> TALL_ENHALUS_ACOROIDES = CustomTallSeagrassBlock.dropping(ENHALUS_ACOROIDES).register();
	public static final BlockEntry<Block> MATTED_ENHALUS_ACOROIDES = mattedSeagrassBlock("enhalus_acoroides");
	public static final BlockEntry<Block> ENHALUS_ACOROIDES_BLOCK = seagrassBlock("enhalus_acoroides");

	public static final BlockEntry<CustomSeagrassBlock> HALODULE_PINIFOLIA = seagrass("halodule_pinifolia");

	public static final BlockEntry<CustomSeagrassBlock> HALODULE_UNINERVIS = seagrass("halodule_uninervis", () -> ExtraBlocks.TALL_HALODULE_UNINERVIS);
	public static final BlockEntry<CustomTallSeagrassBlock> TALL_HALODULE_UNINERVIS = CustomTallSeagrassBlock.dropping(HALODULE_UNINERVIS).register();
	public static final BlockEntry<Block> MATTED_HALODULE_UNINERVIS = mattedSeagrassBlock("halodule_uninervis");
	public static final BlockEntry<Block> HALODULE_UNINERVIS_BLOCK = seagrassBlock("halodule_uninervis");

	public static final BlockEntry<CustomSeagrassBlock> HALOPHILA_OVALIS = seagrass("halophila_ovalis");

	public static final BlockEntry<CustomSeagrassBlock> HALOPHILA_SPINULOSA = seagrass("halophila_spinulosa", () -> ExtraBlocks.TALL_HALOPHILA_SPINULOSA);
	public static final BlockEntry<CustomTallSeagrassBlock> TALL_HALOPHILA_SPINULOSA = CustomTallSeagrassBlock.dropping(HALOPHILA_SPINULOSA).register();
	public static final BlockEntry<Block> MATTED_HALOPHILA_SPINULOSA = mattedSeagrassBlock("halophila_spinulosa");
	public static final BlockEntry<Block> HALOPHILA_SPINULOSA_BLOCK = seagrassBlock("halophila_spinulosa");

	public static final BlockEntry<CustomSeagrassBlock> SYRINGODIUM_ISOETIFOLIUM = seagrass("syringodium_isoetifolium");
	public static final BlockEntry<Block> MATTED_SYRINGODIUM_ISOETIFOLIUM = mattedSeagrassBlock("syringodium_isoetifolium");
	public static final BlockEntry<Block> SYRINGODIUM_ISOETIFOLIUM_BLOCK = seagrassBlock("syringodium_isoetifolium");

	public static final BlockEntry<CustomSeagrassBlock> THALASSIA_HEMPRICHII = seagrass("thalassia_hemprichii");

	public static final BlockEntry<CustomSeagrassBlock> THALASSODENDRON_CILIATUM = seagrass("thalassodendron_ciliatum", () -> ExtraBlocks.TALL_THALASSODENDRON_CILIATUM);
	public static final BlockEntry<CustomTallSeagrassBlock> TALL_THALASSODENDRON_CILIATUM = CustomTallSeagrassBlock.dropping(THALASSODENDRON_CILIATUM).register();
	public static final BlockEntry<Block> MATTED_THALASSODENDRON_CILIATUM = mattedSeagrassBlock("thalassodendron_ciliatum");
	public static final BlockEntry<Block> THALASSODENDRON_CILIATUM_BLOCk = seagrassBlock("thalassodendron_ciliatum");

	public static final BlockEntry<SubmergedLilyBlock> SUBMERGED_LILY_PAD = REGISTRATE.block("submerged_lily_pad", SubmergedLilyBlock::new)
			.lang("Submerged Lily Pad")
			.initialProperties(() -> Blocks.LILY_PAD)
			.color(() -> () -> (state, level, pos, index) -> level != null && pos != null ? 2129968 : 7455580)
			.addLayer(() -> RenderType::cutout)
			.blockstate((ctx, prov) -> {
				var model = prov.models().getBuilder("submerged_lily_pad")
							.ao(false)
							.texture("particle", "minecraft:block/lily_pad")
							.texture("texture", "minecraft:block/lily_pad")
						.element()
							.from(0, 16f, 0)
							.to(16f, 16f, 16f)
							.face(Direction.DOWN)
								.uvs(0, 16f, 16f, 0f)
								.texture("#texture")
								.tintindex(0)
							.end()
							.face(Direction.UP)
								.uvs(0, 0f, 16f, 16f)
								.texture("#texture")
								.tintindex(0)
							.end()
						.end();
				prov.getVariantBuilder(ctx.getEntry())
						.partialState().setModels(
							ConfiguredModel.builder()
										.modelFile(model)
									.nextModel()
										.modelFile(model)
										.rotationY(90)
									.nextModel()
										.modelFile(model)
										.rotationY(180)
									.nextModel()
										.modelFile(model)
										.rotationY(270)
									.build()
						);
			})
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
				public InteractionResultHolder<ItemStack> use(Level leve, Player player, InteractionHand hand) {
					BlockHitResult fluidHit = getPlayerPOVHitResult(leve, player, ClipContext.Fluid.SOURCE_ONLY);
					InteractionResult result = super.useOn(new UseOnContext(player, hand, fluidHit));
					return new InteractionResultHolder<>(result, player.getItemInHand(hand));
				}
			})
			.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), ResourceLocation.parse("minecraft:item/generated")).texture("layer0", "minecraft:block/lily_pad"))
			.build()
			.register();

	public static final BlockEntry<Block> GRASS_GRASS = REGISTRATE.block("grass_grass", Block::new)
			.initialProperties(() -> Blocks.GRASS_BLOCK)
			.blockstate((ctx, prov) -> {
				MultiPartBlockStateBuilder builder = prov.getMultipartBuilder(ctx.getEntry());
				ModelFile grassModel = prov.models().withExistingParent(ctx.getName(), prov.mcLoc("block/grass_block"))
						.texture("down", prov.mcLoc("block/grass_block_top"))
						.texture("up", prov.mcLoc("block/grass_block_top"))
						.texture("north", prov.mcLoc("block/grass_block_top"))
						.texture("south", prov.mcLoc("block/grass_block_top"))
						.texture("west", prov.mcLoc("block/grass_block_top"))
						.texture("east", prov.mcLoc("block/grass_block_top"))
						.element()
						.from(0, 0, 0)
						.to(16, 16, 16)
						.allFaces((direction, faceBuilder) -> faceBuilder.texture("#top").uvs(0, 0, 16, 16).cullface(direction).tintindex(0))
						.end();

				builder.part()
						.modelFile(grassModel).addModel()
						.end();
			})
			.color(() -> () -> (state, reader, pos, color) -> reader != null && pos != null
					? BiomeColors.getAverageGrassColor(reader, pos)
					: FoliageColor.getDefaultColor())
			.item()
			.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.mcLoc("block/grass_block"))
					.texture("down", prov.mcLoc("block/grass_block_top"))
					.texture("up", prov.mcLoc("block/grass_block_top"))
					.texture("north", prov.mcLoc("block/grass_block_top"))
					.texture("south", prov.mcLoc("block/grass_block_top"))
					.texture("west", prov.mcLoc("block/grass_block_top"))
					.texture("east", prov.mcLoc("block/grass_block_top"))
					.element()
					.from(0, 0, 0)
					.to(16, 16, 16)
					.allFaces((direction, faceBuilder) -> faceBuilder.texture("#top").uvs(0, 0, 16, 16).tintindex(0).cullface(direction))
					.end())
			.build()
			.register();

    public static final BlockEntry<CurtainBlock> CURTAIN = REGISTRATE.block("curtain", p -> (CurtainBlock) new CurtainBlock(p) {
            })
            .initialProperties(() -> Blocks.GLASS_PANE)
			.tag(ExtraTags.Blocks.CREATE_MOVABLE_EMPTY_COLLIDER)
            .properties(p -> p.sound(SoundType.WOOL))
            .blockstate((ctx, prov) -> {
            }) // NO-OP, it's easier to just copy the file out of vanilla...
            .addLayer(() -> RenderType::solid)
            .item()
            .model((ctx, prov) -> prov.blockSprite(ctx))
            .build()
            .register();


	private static final TemplateBuilder<CeilingCarpetBlock, BlockFactory<CeilingCarpetBlock>> CEILING_CARPET_TEMPLATES = new TemplateBuilder<CeilingCarpetBlock, BlockFactory<CeilingCarpetBlock>>()
			.add(Blocks.SAND, CeilingCarpetBlock::new)
			.add(Blocks.GREEN_WOOL, CeilingCarpetBlock::new)
			.add(Blocks.MOSS_BLOCK, CeilingCarpetBlock::new);

	public static final Map<Holder<Block>, BlockEntry<? extends CeilingCarpetBlock>> CEILING_CARPET_BLOCKS = CEILING_CARPET_TEMPLATES
			.build((object, factory) -> REGISTRATE.block(getName(object) + "_ceiling_carpet", CeilingCarpetBlock::new)
					.initialProperties(() -> Blocks.WHITE_CARPET)
					.blockstate((ctx, prov) -> {
						final MultiPartBlockStateBuilder builder = prov.getMultipartBuilder(ctx.getEntry());
						final BlockModelBuilder end = prov.models().withExistingParent(ctx.getName(), prov.mcLoc("block/thin_block"))
								.texture("particle", prov.blockTexture(object.value()))
								.element()
								.from(0, 15, 0)
								.to(16, 16, 16)
								.face(Direction.DOWN).texture("#particle").uvs(0, 0, 16, 16).cullface(Direction.DOWN).tintindex(0).end()
								.face(Direction.UP).texture("#particle").uvs(0, 0, 16, 16).cullface(Direction.UP).tintindex(0).end()
								.face(Direction.NORTH).texture("#particle").uvs(0, 15, 16, 16).cullface(Direction.NORTH).tintindex(0).end()
								.face(Direction.SOUTH).texture("#particle").uvs(0, 15, 16, 16).cullface(Direction.SOUTH).tintindex(0).end()
								.face(Direction.WEST).texture("#particle").uvs(0, 15, 16, 16).cullface(Direction.WEST).tintindex(0).end()
								.face(Direction.EAST).texture("#particle").uvs(0, 15, 16, 16).cullface(Direction.EAST).tintindex(0).end()
								.end();
						builder.part()
								.modelFile(end).addModel()
								.end();
					})
					.simpleItem()
					.register()
			);

    public static final Set<BlockEntry<SeatBlock>> SEAT_BLOCKS = Stream.of(DyeColor.values())
            .map(ExtraBlocks::seat)
            .collect(Collectors.toSet());

	private static BlockEntry<SeatBlock> seat(DyeColor dyeColor) {
		final String color = dyeColor.getName();
		return REGISTRATE.block(dyeColor.getName() + "_seat", SeatBlock::new)
				.initialProperties(() -> Blocks.OAK_SLAB)
				.properties(p -> p.sound(SoundType.WOOL))
				.blockstate((ctx, prov) -> {
					BlockModelBuilder model = prov.models().withExistingParent(color + "_seat", prov.mcLoc("block/oak_slab"))
							.texture("side", prov.modLoc("block/seat/side_" + color))
							.texture("top", prov.modLoc("block/seat/top_" + color));
					prov.simpleBlock(ctx.get(), ConfiguredModel.allYRotations(model, 0, false));
				})
				.color(() -> () -> (state, level, pos, index) -> dyeColor.getTextColor())
				.addLayer(() -> RenderType::cutout)
				.simpleItem()
				.register();
	}

	private static BlockEntry<Block> seagrassBlock(String name) {
		String scientificName = RegistrateLangProvider.toEnglishName(name);
		return REGISTRATE.<Block>block(name + "_block", properties -> new ScientificNameBlock(properties, scientificName))
				.initialProperties(() -> Blocks.SAND)
				.lang("Seagrass Block")
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
						.cubeAll(ctx.getName(), prov.modLoc("block/matted_" + name + "_top"))))
				.simpleItem()
				.register();
	}

	private static BlockEntry<Block> mattedSeagrassBlock(String name) {
		String scientificName = RegistrateLangProvider.toEnglishName(name);
		return REGISTRATE.<Block>block("matted_" + name, properties -> new ScientificNameBlock(properties, scientificName))
				.initialProperties(() -> Blocks.SAND)
				.lang("Matted Seagrass Block")
				.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
						.cubeBottomTop(ctx.getName(),
								prov.modLoc("block/" + ctx.getName() + "_side"),
								prov.modLoc("block/purified_sand"),
								prov.modLoc("block/" + ctx.getName() + "_top"))))
				.simpleItem()
				.register();
	}

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
}
