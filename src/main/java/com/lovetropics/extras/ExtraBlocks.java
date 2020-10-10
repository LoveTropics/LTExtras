package com.lovetropics.extras;

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
import com.lovetropics.lib.block.CustomShapeBlock;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
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
            .properties(p -> Block.Properties.from(Blocks.BARRIER).noDrops())
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
            .simpleItem()
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
			.blockstate(ExtraBlocks::steelGirderBlockstate)
			.simpleItem()
			.register();
    }

    private static void steelGirderBlockstate(DataGenContext<Block, GirderBlock> ctx, RegistrateBlockstateProvider prov) {
		ResourceLocation template = prov.modLoc("block/girder_straight");
		ModelFile model = prov.models().singleTexture(ctx.getName(), template, prov.modLoc("block/" + ctx.getName()));

		MultiPartBlockStateBuilder builder = prov.getMultipartBuilder(ctx.get());

		// Add variants for each single axis with one condition on that axis
		addSteelGirderVariants(builder.part(), model, 90, 90)
			.addModel().condition(GirderBlock.PROPS.get(Axis.X), true).end();
		addSteelGirderVariants(builder.part(), model, 0, 0)
			.addModel().condition(GirderBlock.PROPS.get(Axis.Y), true).end();
		addSteelGirderVariants(builder.part(), model, 90, 0)
			.addModel().condition(GirderBlock.PROPS.get(Axis.Z), true).end();

		// Add fallback variant when all axis properties are false
		ConfiguredModel.Builder<PartBuilder> allModels =
				    addSteelGirderVariants(builder.part(), model, 90, 90);
		allModels = addSteelGirderVariants(allModels, model, 0, 0);
		allModels = addSteelGirderVariants(allModels, model, 90, 0);

		PartBuilder allParts = allModels.addModel();
		for (Axis a : Axis.values()) {
			allParts = allParts.condition(GirderBlock.PROPS.get(a), false);
		}
		allParts.end();	
    }

    private static ConfiguredModel.Builder<PartBuilder> addSteelGirderVariants(ConfiguredModel.Builder<PartBuilder> builder, ModelFile model, int xRot, int yRot) {
    	return builder.modelFile(model).rotationX(xRot).rotationY(yRot).weight(1).uvLock(true);
    }

    public static final BlockEntry<CheckpointBlock> CHECKPOINT = REGISTRATE.block("checkpoint", CheckpointBlock::new)
            .properties(p -> Block.Properties.from(Blocks.BARRIER).noDrops())
    		.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models()
				.getBuilder(ctx.getName()).texture("particle", prov.mcLoc("item/structure_void"))))
            .item()
	            .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("item/structure_void")))
	            .build()
    		.register();

    // Speedy blocks

    public static final BlockEntry<SpeedyBlock> SPEEDY_QUARTZ = speedyBlock(Blocks.QUARTZ_BLOCK.delegate, SpeedyBlock::opaque);
    public static final BlockEntry<SpeedyBlock> SPEEDY_STONE_BRICKS = speedyBlock(Blocks.STONE_BRICKS.delegate, SpeedyBlock::opaque);

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

    private enum TextureType {
    	NORMAL,
    	SIDE_TOP,
    	;
    }

    private static final Map<IRegistryDelegate<Block>, TextureType> STAIR_TEMPLATES = ImmutableMap.<IRegistryDelegate<Block>, TextureType>builder()
    		.put(Blocks.GOLD_BLOCK.delegate, TextureType.NORMAL)
    		.build();

    private static final Map<IRegistryDelegate<Block>, TextureType> FENCE_TEMPLATES = ImmutableMap.<IRegistryDelegate<Block>, TextureType>builder()
    		.put(Blocks.GOLD_BLOCK.delegate, TextureType.NORMAL)
    		.put(Blocks.QUARTZ_BLOCK.delegate, TextureType.SIDE_TOP)
    		.put(Blocks.STONE.delegate, TextureType.NORMAL)
    		.put(Blocks.STONE_BRICKS.delegate, TextureType.NORMAL)
    		.build();

    private static final Map<IRegistryDelegate<Block>, TextureType> WALL_TEMPLATES = ImmutableMap.<IRegistryDelegate<Block>, TextureType>builder()
    		.put(Blocks.GOLD_BLOCK.delegate, TextureType.NORMAL)
    		.put(Blocks.QUARTZ_BLOCK.delegate, TextureType.SIDE_TOP)
    		.put(Blocks.STONE.delegate, TextureType.NORMAL)
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

    private static ResourceLocation blockTexture(ModelProvider<?> prov, Block block) {
    	ResourceLocation base = block.getRegistryName();
    	return new ResourceLocation(base.getNamespace(), "block/" + base.getPath());
    }

    private static ResourceLocation blockTexture(ModelProvider<?> prov, Block block, String suffix) {
    	ResourceLocation base = blockTexture(prov, block);
    	return new ResourceLocation(base.getNamespace(), base.getPath() + "_" + suffix);
    }

    private static ResourceLocation getMainTexture(ModelProvider<?> prov, Block block, TextureType texture) {
    	switch (texture) {
    	case NORMAL:
    		return blockTexture(prov, block);
    	case SIDE_TOP:
    		return blockTexture(prov, block, "side");
    	default:
    		throw new IllegalArgumentException();
    	}
    }

    private static <T extends StairsBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> stairsBlock(Map.Entry<IRegistryDelegate<Block>, TextureType> entry) {
		switch (entry.getValue()) {
		case NORMAL:
			return (ctx, prov) -> prov.stairsBlock(ctx.getEntry(), prov.blockTexture(entry.getKey().get()));
		case SIDE_TOP:
			return (ctx, prov) -> prov.stairsBlock(ctx.getEntry(), blockTexture(prov.models(), entry.getKey().get(), "side"), blockTexture(prov.models(), entry.getKey().get(), "top"), blockTexture(prov.models(), entry.getKey().get(), "top"));
		default:
			throw new IllegalArgumentException();
		}
	}

    private static <T extends FenceBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> fenceBlock(Map.Entry<IRegistryDelegate<Block>, TextureType> entry) {
		switch (entry.getValue()) {
		case NORMAL:
			return (ctx, prov) -> prov.fenceBlock(ctx.getEntry(), prov.blockTexture(entry.getKey().get()));
		case SIDE_TOP:
			return (ctx, prov) -> prov.fenceBlock(ctx.getEntry(), getMainTexture(prov.models(), entry.getKey().get(), entry.getValue()));
		default:
			throw new IllegalArgumentException();
		}
	}

    private static <T extends WallBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> wallBlock(Map.Entry<IRegistryDelegate<Block>, TextureType> entry) {
		switch (entry.getValue()) {
		case NORMAL:
			return (ctx, prov) -> prov.wallBlock(ctx.getEntry(), prov.blockTexture(entry.getKey().get()));
		case SIDE_TOP:
			return (ctx, prov) -> prov.wallBlock(ctx.getEntry(), getMainTexture(prov.models(), entry.getKey().get(), entry.getValue()));
		default:
			throw new IllegalArgumentException();
		}
	}

	public static void init() {
	}
}
