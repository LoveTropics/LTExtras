package com.lovetropics.extras;

import static com.lovetropics.extras.data.ModelGenUtil.barsBlock;
import static com.lovetropics.extras.data.ModelGenUtil.fenceBlock;
import static com.lovetropics.extras.data.ModelGenUtil.getMainTexture;
import static com.lovetropics.extras.data.ModelGenUtil.scaffoldingModel;
import static com.lovetropics.extras.data.ModelGenUtil.slabBlock;
import static com.lovetropics.extras.data.ModelGenUtil.stairsBlock;
import static com.lovetropics.extras.data.ModelGenUtil.wallBlock;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.lovetropics.extras.block.CheckpointBlock;
import com.lovetropics.extras.block.FakeWaterBlock;
import com.lovetropics.extras.block.GirderBlock;
import com.lovetropics.extras.block.PanelBlock;
import com.lovetropics.extras.block.SpeedyBlock;
import com.lovetropics.extras.block.WaterBarrierBlock;
import com.lovetropics.extras.data.ModelGenUtil;
import com.lovetropics.extras.data.TextureType;
import com.lovetropics.extras.item.BouyBlockItem;
import com.lovetropics.lib.block.CustomShapeBlock;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ScaffoldingItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.registries.IRegistryDelegate;

public class ExtraBlocks {

	public static final Registrate REGISTRATE = LTExtras.registrate();

	// One-off custom blocks

    public static final BlockEntry<WaterBarrierBlock> WATER_BARRIER = REGISTRATE.block("water_barrier", WaterBarrierBlock::new)
            .properties(p -> Block.Properties.from(Blocks.BARRIER).noDrops())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), 
                    prov.models().getBuilder(ctx.getName()).texture("particle", new ResourceLocation("item/barrier"))))
            .item()
                .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("block/water_still"), new ResourceLocation("item/barrier")))
                .build()
            .register();

    public static final BlockEntry<FakeWaterBlock> FAKE_WATER = REGISTRATE.block("fake_water", FakeWaterBlock::new)
            .properties(p -> Block.Properties.from(Blocks.BARRIER).noDrops())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), 
                    prov.models().getBuilder(ctx.getName()).texture("particle", new ResourceLocation("block/water_still"))))
            .item()
                .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("block/water_still")))
                .build()
            .register();

    public static final BlockEntry<CustomShapeBlock> BUOY = REGISTRATE.block("buoy", p -> new CustomShapeBlock(
                    VoxelShapes.or(
                            Block.makeCuboidShape(2, 0, 2, 14, 3, 14),
                            Block.makeCuboidShape(3, 3, 3, 13, 14, 13)),
                    p))
            .properties(p -> Block.Properties.from(Blocks.BEACON))
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
                            .textureAll("beacon")
                            .end()))
            .item(BouyBlockItem::new).build()
            .register();

    public static final BlockEntry<PanelBlock> GLASS_PANEL = REGISTRATE.block("glass_panel", PanelBlock::new)
    		.initialProperties(() -> Blocks.GLASS)
    		.blockstate((ctx, prov) -> prov.directionalBlock(ctx.get(), prov.models()
    				.trapdoorTop(ctx.getName(), prov.blockTexture(Blocks.GLASS))))
    		.loot(BlockLootTables::registerSilkTouch)
    		.addLayer(() -> RenderType::getCutout)
    		.item()
    			.model((ctx, prov) -> prov.trapdoorBottom(ctx.getName(), prov.mcLoc("block/glass")))
    			.build()
    		.register();
    
    public static final Tag<Block> TAG_STEEL_GIRDERS = new BlockTags.Wrapper(new ResourceLocation(LTExtras.MODID, "steel_girders"));

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
            .properties(p -> Block.Properties.from(Blocks.BARRIER).noDrops())
    		.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models()
				.getBuilder(ctx.getName()).texture("particle", prov.mcLoc("item/structure_void"))))
            .item()
	            .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("item/structure_void")))
	            .build()
    		.register();

    public static final BlockEntry<ScaffoldingBlock> METAL_SCAFFOLDING = REGISTRATE.block("metal_scaffolding", p -> (ScaffoldingBlock) new ScaffoldingBlock(p) {})
    		.initialProperties(() -> Blocks.SCAFFOLDING)
    		.blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry())
    				.partialState().with(ScaffoldingBlock.field_220120_c, true)
    					.addModels(scaffoldingModel(ctx, prov, "unstable"))
    				.partialState().with(ScaffoldingBlock.field_220120_c, false)
    					.addModels(scaffoldingModel(ctx, prov, "stable")))
    		.addLayer(() -> RenderType::getCutout)
    		.item(ScaffoldingItem::new)
    			.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("block/metal_scaffolding_stable")))
    			.build()
    		.register();

    public static final BlockEntry<PaneBlock> RUSTY_IRON_BARS = REGISTRATE.block("rusty_iron_bars", p -> (PaneBlock) new PaneBlock(p) {})
    		.initialProperties(() -> Blocks.IRON_BARS)
    		.blockstate((ctx, prov) -> barsBlock(ctx, prov))
    		.addLayer(() -> RenderType::getCutout)
    		.item()
    			.model((ctx, prov) -> prov.blockSprite(ctx))
    			.build()
    		.register();

    public static final BlockEntry<LadderBlock> METAL_LADDER = REGISTRATE.block("metal_ladder", p -> (LadderBlock) new LadderBlock(p) {})
    		.initialProperties(() -> Blocks.IRON_BARS)
    		.blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
    				.withExistingParent(ctx.getName(), "block/ladder")
    				.texture("texture", prov.blockTexture(ctx.getEntry()))
    				.texture("particle", prov.blockTexture(ctx.getEntry()))))
    		.addLayer(() -> RenderType::getCutout)
    		.item()
    			.model((ctx, prov) -> prov.blockSprite(ctx))
    			.build()
    		.register();

    public static final BlockEntry<Block> RUSTY_PAINTED_METAL = REGISTRATE.block("rusty_painted_metal", Block::new)
    		.initialProperties(() -> Blocks.IRON_BLOCK)
    		.simpleItem()
    		.register();

	public static final BlockEntry<Block> BLACK_CONCRETE_POWDER_FAKE = REGISTRATE.block("black_concrete_powder_fake", Block::new)
			.initialProperties(() -> Blocks.DIRT)
			.simpleItem()
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(new ResourceLocation("block/black_concrete_powder"))))
			.register();

    public static final BlockEntry<StainedGlassBlock> SMOOTH_LIGHT_GRAY_STAINED_GLASS = REGISTRATE.block("smooth_light_gray_stained_glass", p -> new StainedGlassBlock(DyeColor.LIGHT_GRAY, p))
    		.initialProperties(() -> Blocks.LIGHT_GRAY_STAINED_GLASS)
    		.loot(RegistrateBlockLootTables::registerSilkTouch)
    		.addLayer(() -> RenderType::getTranslucent)
    		.simpleItem()
    		.register();

    // Speedy blocks

    public static final BlockEntry<SpeedyBlock> SPEEDY_QUARTZ = speedyBlock(Blocks.QUARTZ_BLOCK.delegate, SpeedyBlock::opaque);
    public static final BlockEntry<SpeedyBlock> SPEEDY_STONE_BRICKS = speedyBlock(Blocks.STONE_BRICKS.delegate, SpeedyBlock::opaque);
    public static final BlockEntry<SpeedyBlock> SPEEDY_CRACKED_STONE_BRICKS = speedyBlock(Blocks.CRACKED_STONE_BRICKS.delegate, SpeedyBlock::opaque);
    public static final BlockEntry<SpeedyBlock> SPEEDY_SMOOTH_STONE = speedyBlock(Blocks.SMOOTH_STONE.delegate, SpeedyBlock::opaque);

    private static final VoxelShape PATH_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    public static final BlockEntry<SpeedyBlock> SPEEDY_GRASS_PATH = speedyBlock(Blocks.GRASS_PATH.delegate, p -> SpeedyBlock.transparent(PATH_SHAPE, p));

    private static <T extends SpeedyBlock> BlockEntry<T> speedyBlock(IRegistryDelegate<Block> source, NonNullFunction<Block.Properties, T> creator) {
    	return REGISTRATE 
			.block("speedy_" + source.name().getPath(), creator)
			.initialProperties(source::get)
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().getExistingFile(source.name())))
			.simpleItem()
			.register();
    }

    // Custom stairs/fences/walls/etc

    private static final Map<IRegistryDelegate<Block>, TextureType> STAIR_TEMPLATES = ImmutableMap.<IRegistryDelegate<Block>, TextureType>builder()
    		.put(Blocks.GOLD_BLOCK.delegate, TextureType.NORMAL)
    		.put(Blocks.CRACKED_STONE_BRICKS.delegate, TextureType.NORMAL)
    		.put(Blocks.BLACK_CONCRETE_POWDER.delegate, TextureType.NORMAL)
    		.build();

    private static final Map<IRegistryDelegate<Block>, TextureType> SLAB_TEMPLATES = ImmutableMap.<IRegistryDelegate<Block>, TextureType>builder()
    		.put(Blocks.CRACKED_STONE_BRICKS.delegate, TextureType.NORMAL)
    		.put(Blocks.BLACK_CONCRETE_POWDER.delegate, TextureType.NORMAL)
    		.build();

    private static final Map<IRegistryDelegate<Block>, TextureType> FENCE_TEMPLATES = ImmutableMap.<IRegistryDelegate<Block>, TextureType>builder()
    		.put(Blocks.GOLD_BLOCK.delegate, TextureType.NORMAL)
    		.put(Blocks.QUARTZ_BLOCK.delegate, TextureType.SIDE_TOP)
    		.put(Blocks.STONE.delegate, TextureType.NORMAL)
    		.put(Blocks.STONE_BRICKS.delegate, TextureType.NORMAL)
    		.put(Blocks.CRACKED_STONE_BRICKS.delegate, TextureType.NORMAL)
    		.build();

    private static final Map<IRegistryDelegate<Block>, TextureType> WALL_TEMPLATES = ImmutableMap.<IRegistryDelegate<Block>, TextureType>builder()
    		.put(Blocks.GOLD_BLOCK.delegate, TextureType.NORMAL)
    		.put(Blocks.QUARTZ_BLOCK.delegate, TextureType.SIDE_TOP)
    		.put(Blocks.STONE.delegate, TextureType.NORMAL)
    		.put(Blocks.CRACKED_STONE_BRICKS.delegate, TextureType.NORMAL)
    		.build();

    public static final Map<IRegistryDelegate<Block>, BlockEntry<? extends StairsBlock>> STAIRS = STAIR_TEMPLATES.entrySet().stream()
    		.collect(Collectors.toMap(Entry::getKey, e -> REGISTRATE
    				.block(e.getKey().name().getPath() + "_stairs", p -> new StairsBlock(() -> e.getKey().get().getDefaultState(), p))
    				.initialProperties(NonNullSupplier.of(e.getKey()))
    				.tag(BlockTags.STAIRS)
    				.blockstate(stairsBlock(e))
    				.item()
    					.tag(ItemTags.STAIRS)
    					.build()
    				.register()));

    public static final Map<IRegistryDelegate<Block>, BlockEntry<? extends SlabBlock>> SLABS = SLAB_TEMPLATES.entrySet().stream()
    		.collect(Collectors.toMap(Entry::getKey, e -> REGISTRATE
    				.block(e.getKey().name().getPath() + "_slab", SlabBlock::new)
    				.initialProperties(NonNullSupplier.of(e.getKey()))
    				.tag(BlockTags.STAIRS)
    				.blockstate(slabBlock(e))
    				.item()
    					.tag(ItemTags.STAIRS)
    					.build()
    				.register()));

    public static final Map<IRegistryDelegate<Block>, BlockEntry<? extends FenceBlock>> FENCES = FENCE_TEMPLATES.entrySet().stream()
    		.collect(Collectors.toMap(Entry::getKey, e -> REGISTRATE
    				.block(e.getKey().name().getPath() + "_fence", FenceBlock::new)
    				.initialProperties(NonNullSupplier.of(e.getKey()))
    				.tag(BlockTags.FENCES)
    				.blockstate(fenceBlock(e))
    				.item()
    					.tag(ItemTags.FENCES)
    					.model((ctx, prov) -> prov.fenceInventory(ctx.getName(), getMainTexture(prov, e.getKey().get(), e.getValue())))
    					.build()
    				.register()));

    public static final Map<IRegistryDelegate<Block>, BlockEntry<? extends WallBlock>> WALLS = WALL_TEMPLATES.entrySet().stream()
    		.collect(Collectors.toMap(Entry::getKey, e -> REGISTRATE
    				.block(e.getKey().name().getPath() + "_wall", WallBlock::new)
    				.initialProperties(NonNullSupplier.of(e.getKey()))
    				.tag(BlockTags.WALLS)
    				.blockstate(wallBlock(e))
    				.item()
    					.tag(ItemTags.WALLS)
						.model((ctx, prov) -> prov.wallInventory(ctx.getName(), getMainTexture(prov, e.getKey().get(), e.getValue())))
						.build()
					.register()));

	public static void init() {
	}
}
