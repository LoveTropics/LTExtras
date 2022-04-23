package com.lovetropics.extras;

import com.lovetropics.extras.block.*;
import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.data.ImposterBlockTemplate;
import com.lovetropics.extras.data.ModelGenUtil;
import com.lovetropics.extras.item.BouyBlockItem;
import com.lovetropics.extras.item.EntityWandItem;
import com.lovetropics.extras.mixin.BlockPropertiesMixin;
import com.lovetropics.lib.block.CustomShapeBlock;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.TileEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ScaffoldingBlockItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.fml.DatagenModLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lovetropics.extras.data.ModelGenUtil.*;

import com.lovetropics.extras.data.ModelGenUtil.TextureType;

public class ExtraBlocks {

	public static final Registrate REGISTRATE = LTExtras.registrate();

	// One-off custom blocks

    public static final BlockEntry<WaterBarrierBlock> WATER_BARRIER = REGISTRATE.block("water_barrier", WaterBarrierBlock::new)
            .properties(p -> Block.Properties.copy(Blocks.BARRIER).noDrops())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().getBuilder(ctx.getName()).texture("particle", new ResourceLocation("item/barrier"))))
            .item()
                .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("block/water_still"), new ResourceLocation("item/barrier")))
                .build()
            .register();

	public static final BlockEntry<LightweightBarrierBlock> LIGHTWEIGHT_BARRIER = REGISTRATE.block("lightweight_barrier", LightweightBarrierBlock::new)
			.properties(p -> Block.Properties.copy(Blocks.BARRIER).strength(0.0F, 3.6e6f).noDrops())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
					prov.models().getBuilder(ctx.getName()).texture("particle", new ResourceLocation("item/barrier"))))
			.item()
			.model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("item/barrier")))
			.build()
			.register();

	public static final BlockEntry<PassableBarrierBlock> PASSABLE_BARRIER = REGISTRATE.block("passable_barrier", PassableBarrierBlock::new)
			.properties(p -> Block.Properties.copy(Blocks.BARRIER).noDrops())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
					prov.models().getBuilder(ctx.getName()).texture("particle", new ResourceLocation("item/barrier"))))
			.item()
			.model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("item/barrier")))
			.build()
			.register();

    public static final BlockEntry<FakeWaterBlock> FAKE_WATER = REGISTRATE.block("fake_water", FakeWaterBlock::new)
            .properties(p -> Block.Properties.copy(Blocks.BARRIER).noDrops())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().getBuilder(ctx.getName()).texture("particle", new ResourceLocation("block/water_still"))))
            .item()
                .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("block/water_still")))
                .build()
            .register();

    public static final BlockEntry<CustomShapeBlock> BUOY = REGISTRATE.block("buoy", p -> new CustomShapeBlock(
                    Shapes.or(
                            Block.box(2, 0, 2, 14, 3, 14),
                            Block.box(3, 3, 3, 13, 14, 13)),
                    p))
            .properties(p -> Block.Properties.copy(Blocks.BEACON))
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models()
                    .withExistingParent(ctx.getName(), new ResourceLocation("block/block"))
                        .ao(false)
                        .texture("beacon", new ResourceLocation("block/beacon"))
                        .texture("base", new ResourceLocation("block/dark_prismarine"))
                        .texture("particle", new ResourceLocation("block/dark_prismarine"))
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
            .item(BouyBlockItem::new).build()
            .register();

    public static final BlockEntry<PanelBlock> GLASS_PANEL = REGISTRATE.block("glass_panel", PanelBlock::new)
    		.initialProperties(() -> Blocks.GLASS)
    		.blockstate((ctx, prov) -> prov.directionalBlock(ctx.get(), prov.models()
    				.trapdoorTop(ctx.getName(), prov.blockTexture(Blocks.GLASS))))
    		.loot(BlockLoot::dropWhenSilkTouch)
    		.addLayer(() -> RenderType::cutout)
    		.item()
    			.model((ctx, prov) -> prov.trapdoorBottom(ctx.getName(), prov.mcLoc("block/glass")))
    			.build()
    		.register();

    public static final Tag.Named<Block> TAG_STEEL_GIRDERS = BlockTags.bind(LTExtras.MODID +":steel_girders");

    public static final BlockEntry<GirderBlock> STEEL_GIRDER = steelGirder("");
    public static final BlockEntry<GirderBlock> RUSTING_STEEL_GIRDER = steelGirder("rusting");
    public static final BlockEntry<GirderBlock> RUSTED_STEEL_GIRDER = steelGirder("rusted");

    private static BlockEntry<GirderBlock> steelGirder(String name) {
    	return REGISTRATE.block((name.isEmpty() ? name : (name + "_")) + "steel_girder", p -> new GirderBlock(TAG_STEEL_GIRDERS, p))
			.initialProperties(() -> Blocks.IRON_BARS)
			.tag(TAG_STEEL_GIRDERS)
			.blockstate(ModelGenUtil::steelGirderBlockstate)
			.simpleItem()
			.register();
    }

    public static final BlockEntry<CheckpointBlock> CHECKPOINT = REGISTRATE.block("checkpoint", CheckpointBlock::new)
            .properties(p -> Block.Properties.copy(Blocks.BARRIER).noDrops())
    		.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models()
				.getBuilder(ctx.getName()).texture("particle", prov.mcLoc("item/structure_void"))))
            .item()
	            .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("item/structure_void")))
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
    		.item(ScaffoldingBlockItem::new)
    			.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("block/metal_scaffolding_stable")))
    			.build()
    		.register();

    public static final BlockEntry<IronBarsBlock> RUSTY_IRON_BARS = REGISTRATE.block("rusty_iron_bars", p -> (IronBarsBlock) new IronBarsBlock(p) {})
    		.initialProperties(() -> Blocks.IRON_BARS)
    		.blockstate((ctx, prov) -> barsBlock(ctx, prov))
    		.addLayer(() -> RenderType::cutout)
    		.item()
    			.model((ctx, prov) -> prov.blockSprite(ctx))
    			.build()
    		.register();

    public static final BlockEntry<LadderBlock> METAL_LADDER = REGISTRATE.block("metal_ladder", p -> (LadderBlock) new LadderBlock(p) {})
    		.initialProperties(() -> Blocks.IRON_BARS)
			.tag(BlockTags.CLIMBABLE)
			.blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
    				.withExistingParent(ctx.getName(), "block/ladder")
    				.texture("texture", prov.blockTexture(ctx.getEntry()))
    				.texture("particle", prov.blockTexture(ctx.getEntry()))))
    		.addLayer(() -> RenderType::cutout)
    		.item()
    			.model((ctx, prov) -> prov.blockSprite(ctx))
    			.build()
    		.register();

    public static final BlockEntry<Block> RUSTY_PAINTED_METAL = REGISTRATE.block("rusty_painted_metal", Block::new)
    		.initialProperties(() -> Blocks.IRON_BLOCK)
    		.simpleItem()
    		.register();

	public static final BlockEntry<MobControllerBlock> MOB_CONTROLLER = REGISTRATE.block("mob_controller", MobControllerBlock::new)
			.initialProperties(() -> Blocks.IRON_BLOCK)
			.simpleItem()
			.tileEntity(MobControllerBlockEntity::new)
				.build()
			.register();

	public static final TileEntityEntry<MobControllerBlockEntity> MOB_CONTROLLER_BE = TileEntityEntry.cast(MOB_CONTROLLER.getSibling(ForgeRegistries.TILE_ENTITIES));

	public static final BlockEntry<Block> BLACK_CONCRETE_POWDER_FAKE = REGISTRATE.block("black_concrete_powder_fake", Block::new)
			.initialProperties(() -> Blocks.DIRT)
			.item()
				.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), new ResourceLocation("block/black_concrete_powder")))
				.build()
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), ConfiguredModel.allYRotations(prov.models().getExistingFile(new ResourceLocation("block/black_concrete_powder")), 0, false)))
			.register();

    public static final BlockEntry<StainedGlassBlock> SMOOTH_LIGHT_GRAY_STAINED_GLASS = REGISTRATE.block("smooth_light_gray_stained_glass", p -> new StainedGlassBlock(DyeColor.LIGHT_GRAY, p))
    		.initialProperties(() -> Blocks.LIGHT_GRAY_STAINED_GLASS)
    		.loot(RegistrateBlockLootTables::dropWhenSilkTouch)
    		.addLayer(() -> RenderType::translucent)
    		.simpleItem()
    		.register();

	public static final BlockEntry<ReedsBlock> REEDS = REGISTRATE.block("reeds", ReedsBlock::new)
			.properties(p -> Block.Properties.copy(Blocks.SUGAR_CANE).noDrops())
			.blockstate((ctx, prov) -> {
				prov.getVariantBuilder(ctx.getEntry())
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
						});
			})
			.addLayer(() -> RenderType::cutout)
			.item()
				.model((ctx, prov) -> prov.generated(ctx::getEntry, prov.modLoc("block/reeds_top_tall")))
				.build()
			.register();

	public static final BlockEntry<PianguasBlock> PIANGUAS = REGISTRATE.block("pianguas", PianguasBlock::new)
			.properties(p -> BlockBehaviour.Properties.of(Material.STONE).noCollission().instabreak())
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

	public static final BlockEntry<RopeBlock> OLD_ROPE = rope("old_rope");
	public static final BlockEntry<RopeBlock> PARACORD = rope("paracord");

	private static BlockEntry<RopeBlock> rope(String name) {
		return REGISTRATE.block(name, Material.WOOL, RopeBlock::new)
				.properties(p -> p.instabreak().noCollission().sound(SoundType.WOOL))
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
					.build()
				.register();
	}

	private static final List<DyeColor> GLOW_STICKS_DYES = Arrays.stream(DyeColor.values())
			.filter(color -> color != DyeColor.BLACK && color != DyeColor.LIGHT_GRAY && color != DyeColor.GRAY)
			.collect(Collectors.toList());

	public static final Map<DyeColor, BlockEntry<GlowSticksBlock>> GLOW_STICKS = GLOW_STICKS_DYES.stream()
			.collect(Collectors.toMap(Function.identity(), dyeColor -> {
				String dyeName = dyeColor.getSerializedName();
				String name = dyeName + "_glow_sticks";
				return REGISTRATE.block(name, Material.GLASS, GlowSticksBlock::new)
						.properties(p -> p.instabreak().noCollission().sound(SoundType.GLASS).noOcclusion()
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
				.model((ctx, prov) -> prov.generated(ctx, new ResourceLocation("block/vine"), new ResourceLocation("item/barrier")))
				.color(() -> () -> ($, layer) -> layer == 0 ? FoliageColor.getDefaultColor() : -1)
				.build()
			.register();

	public static final BlockEntry<HeavyDoorBlock> HEAVY_SPRUCE_DOOR = REGISTRATE.block("heavy_spruce_door", HeavyDoorBlock::new)
			.initialProperties(() -> Blocks.SPRUCE_DOOR)
			.blockstate((ctx, prov) -> {})
			.addLayer(() -> RenderType::cutout)
			.item()
			.model((ctx, prov) -> prov.generated(ctx, new ResourceLocation("item/spruce_door")))
			.build()
			.register();

	public static final ItemEntry<EntityWandItem> ENTITY_WAND = REGISTRATE.item("entity_wand", EntityWandItem::new)
			.initialProperties(() -> new Item.Properties().stacksTo(1))
			.defaultModel()
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

	// Speedy blocks

	private static final ResourceLocation WEATHERED_LIMESTONE_ID = new ResourceLocation("create", "weathered_limestone");
	private static final ResourceLocation POLISHED_WEATHERED_LIMESTONE_ID = new ResourceLocation("create", "polished_weathered_limestone");

	private static final ResourceLocation WEATHERED_LIMESTONE_TEXTURE = new ResourceLocation("create", "block/palettes/weathered_limestone/plain");
	private static final ResourceLocation POLISHED_WEATHERED_LIMESTONE_TEXTURE = new ResourceLocation("create", "block/palettes/weathered_limestone/polished");

	private static final VoxelShape PATH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

	private static final TemplateBuilder<SpeedyBlock, BlockFactory<SpeedyBlock>> SPEEDY_BLOCK_TEMPLATES = new TemplateBuilder<SpeedyBlock, BlockFactory<SpeedyBlock>>()
			.add(Blocks.QUARTZ_BLOCK, SpeedyBlock::opaque)
			.add(Blocks.STONE_BRICKS, SpeedyBlock::opaque)
			.add(Blocks.CRACKED_STONE_BRICKS, SpeedyBlock::opaque)
			.add(Blocks.CRACKED_STONE_BRICKS, SpeedyBlock::opaque)
			.add(Blocks.SMOOTH_STONE, SpeedyBlock::opaque)
			.add(Blocks.GRAVEL, SpeedyBlock::opaque)
			.add(Blocks.GRASS_PATH, p -> SpeedyBlock.transparent(PATH_SHAPE, p))
			.add(WEATHERED_LIMESTONE_ID, SpeedyBlock::opaque)
			.add(POLISHED_WEATHERED_LIMESTONE_ID, SpeedyBlock::opaque);

	public static final Map<NamedSupplier<Block>, BlockEntry<? extends SpeedyBlock>> SPEEDY_BLOCKS = SPEEDY_BLOCK_TEMPLATES
			.build((object, factory) -> REGISTRATE
					.block("speedy_" + object.getId().getPath(), factory)
					.initialProperties(NonNullSupplier.of(object))
					.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().getExistingFile(object.getId())))
					.simpleItem()
					.register()
			);

	// Imposter blocks

	private static final TemplateBuilder<Block, ImposterBlockTemplate> IMPOSTER_BLOCK_TEMPLATES = new TemplateBuilder<Block, ImposterBlockTemplate>()
			.add(Blocks.BRAIN_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
			.add(Blocks.BUBBLE_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
			.add(Blocks.HORN_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
			.add(Blocks.TUBE_CORAL_BLOCK, ImposterBlockTemplate.simpleCube())
			.add(Blocks.BRAIN_CORAL, ImposterBlockTemplate.cross(BaseCoralPlantTypeBlock::new))
			.add(Blocks.BUBBLE_CORAL, ImposterBlockTemplate.cross(BaseCoralPlantTypeBlock::new))
			.add(Blocks.HORN_CORAL, ImposterBlockTemplate.cross(BaseCoralPlantTypeBlock::new))
			.add(Blocks.TUBE_CORAL, ImposterBlockTemplate.cross(BaseCoralPlantTypeBlock::new));

	public static final Map<NamedSupplier<Block>, BlockEntry<? extends Block>> IMPOSTER_BLOCKS = IMPOSTER_BLOCK_TEMPLATES
			.build((object, template) -> {
						BlockBuilder<? extends Block, Registrate> block = REGISTRATE
								.block("imposter_" + object.getId().getPath(), template.factory)
								.initialProperties(NonNullSupplier.of(object));
						return template.model.apply(block, object.getId()).register();
					}
			);

    // Custom stairs/fences/walls/etc

	private static final TemplateBuilder<StairBlock, TextureType> STAIR_TEMPLATES = new TemplateBuilder<StairBlock, TextureType>()
			.add(Blocks.GOLD_BLOCK, TextureType.normal())
			.add(Blocks.CRACKED_STONE_BRICKS, TextureType.normal())
			.add(Blocks.BLACK_CONCRETE_POWDER, TextureType.normal())
			.add(RUSTY_PAINTED_METAL, TextureType.normal())
			.add(WEATHERED_LIMESTONE_ID, TextureType.allTexture(WEATHERED_LIMESTONE_TEXTURE))
			.add(POLISHED_WEATHERED_LIMESTONE_ID, TextureType.allTexture(POLISHED_WEATHERED_LIMESTONE_TEXTURE));

	private static final TemplateBuilder<SlabBlock, TextureType> SLAB_TEMPLATES = new TemplateBuilder<SlabBlock, TextureType>()
			.add(Blocks.GOLD_BLOCK, TextureType.normal())
			.add(Blocks.CRACKED_STONE_BRICKS, TextureType.normal())
			.add(Blocks.BLACK_CONCRETE_POWDER, TextureType.normal())
			.add(RUSTY_PAINTED_METAL, TextureType.normal())
			.add(WEATHERED_LIMESTONE_ID, TextureType.allTexture(WEATHERED_LIMESTONE_TEXTURE))
			.add(POLISHED_WEATHERED_LIMESTONE_ID, TextureType.allTexture(POLISHED_WEATHERED_LIMESTONE_TEXTURE));

	private static final TemplateBuilder<FenceBlock, TextureType> FENCE_TEMPLATES = new TemplateBuilder<FenceBlock, TextureType>()
			.add(Blocks.GOLD_BLOCK, TextureType.normal())
			.add(Blocks.QUARTZ_BLOCK, TextureType.sideTopSuffix())
			.add(Blocks.STONE, TextureType.normal())
			.add(Blocks.STONE_BRICKS, TextureType.normal())
			.add(Blocks.CRACKED_STONE_BRICKS, TextureType.normal())
			.add(RUSTY_PAINTED_METAL, TextureType.normal())
			.add(WEATHERED_LIMESTONE_ID, TextureType.allTexture(WEATHERED_LIMESTONE_TEXTURE))
			.add(POLISHED_WEATHERED_LIMESTONE_ID, TextureType.allTexture(POLISHED_WEATHERED_LIMESTONE_TEXTURE));

	private static final TemplateBuilder<WallBlock, TextureType> WALL_TEMPLATES = new TemplateBuilder<WallBlock, TextureType>()
			.add(Blocks.GOLD_BLOCK, TextureType.normal())
			.add(Blocks.QUARTZ_BLOCK, TextureType.sideTopSuffix())
			.add(Blocks.STONE, TextureType.normal())
			.add(Blocks.CRACKED_STONE_BRICKS, TextureType.normal())
			.add(Blocks.POLISHED_ANDESITE, TextureType.normal())
			.add(Blocks.POLISHED_GRANITE, TextureType.normal())
			.add(Blocks.POLISHED_DIORITE, TextureType.normal())
			.add(RUSTY_PAINTED_METAL, TextureType.normal())
			.add(WEATHERED_LIMESTONE_ID, TextureType.allTexture(WEATHERED_LIMESTONE_TEXTURE))
			.add(POLISHED_WEATHERED_LIMESTONE_ID, TextureType.allTexture(POLISHED_WEATHERED_LIMESTONE_TEXTURE));

	public static final Map<NamedSupplier<Block>, BlockEntry<? extends StairBlock>> STAIRS = STAIR_TEMPLATES
			.build((object, textureType) -> REGISTRATE
					.block(object.getId().getPath() + "_stairs", p -> new StairBlock(() -> object.get().defaultBlockState(), p))
					.initialProperties(NonNullSupplier.of(object))
					.tag(BlockTags.STAIRS)
					.blockstate(stairsBlock(object, textureType))
					.item()
						.tag(ItemTags.STAIRS)
						.build()
					.register()
			);

	public static final Map<NamedSupplier<Block>, BlockEntry<? extends SlabBlock>> SLABS = SLAB_TEMPLATES
			.build((object, textureType) -> REGISTRATE
					.block(object.getId().getPath() + "_slab", SlabBlock::new)
					.initialProperties(NonNullSupplier.of(object))
					.tag(BlockTags.STAIRS)
					.blockstate(slabBlock(object, textureType))
					.item()
						.tag(ItemTags.STAIRS)
						.build()
					.register()
			);

	public static final Map<NamedSupplier<Block>, BlockEntry<? extends FenceBlock>> FENCES = FENCE_TEMPLATES
			.build((block, textureType) -> REGISTRATE
					.block(block.getId().getPath() + "_fence", FenceBlock::new)
					.initialProperties(NonNullSupplier.of(block))
					.tag(BlockTags.FENCES)
					.blockstate(fenceBlock(block, textureType))
					.item()
						.tag(ItemTags.FENCES)
						.model((ctx, prov) -> prov.fenceInventory(ctx.getName(), getMainTexture(block, textureType)))
						.build()
					.register()
			);

	public static final Map<NamedSupplier<Block>, BlockEntry<? extends WallBlock>> WALLS = WALL_TEMPLATES
			.build((block, textureType) -> REGISTRATE
					.block(block.getId().getPath() + "_wall", WallBlock::new)
					.initialProperties(NonNullSupplier.of(block))
					.tag(BlockTags.WALLS)
					.blockstate(wallBlock(block, textureType))
					.item()
						.tag(ItemTags.WALLS)
						.model((ctx, prov) -> prov.wallInventory(ctx.getName(), getMainTexture(block, textureType)))
						.build()
					.register()
			);

	public static void init() {
	}

	public static final class TemplateBuilder<T extends Block, P> {
		private final Map<NamedSupplier<Block>, P> templates = new Object2ObjectOpenHashMap<>();

		public TemplateBuilder<T, P> add(Block block, P parameter) {
			return this.add(NamedSupplier.of(block), parameter);
		}

		public TemplateBuilder<T, P> add(ResourceLocation id, P parameter) {
			NamedSupplier<Block> block = NamedSupplier.of(ForgeRegistries.BLOCKS, id);
			return this.add(block, parameter);
		}

		public TemplateBuilder<T, P> add(BlockEntry<Block> block, P parameter) {
			return this.add(NamedSupplier.of(block), parameter);
		}

		public TemplateBuilder<T, P> add(NamedSupplier<Block> block, P parameter) {
			String namespace = block.getId().getNamespace();
			if (ModList.get().isLoaded(namespace)) {
				this.templates.put(block, parameter);
			} else {
				if (DatagenModLoader.isRunningDataGen()) {
					throw new UnsupportedOperationException("All soft-dependent mods must be present for datagen! Missing: " + namespace);
				}
			}
			return this;
		}

		public Map<NamedSupplier<Block>, BlockEntry<? extends T>> build(
				BiFunction<NamedSupplier<Block>, P, BlockEntry<? extends T>> factory
		) {
			return this.templates.entrySet().stream()
					.collect(Collectors.toMap(
							Entry::getKey,
							entry -> factory.apply(entry.getKey(), entry.getValue())
					));
		}
	}
}
