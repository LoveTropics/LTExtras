package com.lovetropics.extras.data;

import com.lovetropics.extras.NamedSupplier;
import com.lovetropics.extras.block.GirderBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;

public class ModelGenUtil {

    public static void steelGirderBlockstate(DataGenContext<Block, GirderBlock> ctx, RegistrateBlockstateProvider prov) {
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

    public static ConfiguredModel scaffoldingModel(DataGenContext<Block, ScaffoldingBlock> ctx, RegistrateBlockstateProvider prov, String suffix) {
    	return new ConfiguredModel(prov.models()
			.withExistingParent(ctx.getName() + "_" + suffix, "scaffolding" + "_" + suffix)
				.texture("bottom", prov.modLoc("block/metal_scaffolding_bottom"))
				.texture("top", prov.modLoc("block/metal_scaffolding_top"))
				.texture("side", prov.modLoc("block/metal_scaffolding_side"))
				.texture("particle", prov.modLoc("block/metal_scaffolding_top")));
    }

    public static void barsBlock(DataGenContext<Block, PaneBlock> ctx, RegistrateBlockstateProvider prov) {
    	MultiPartBlockStateBuilder builder = prov.getMultipartBuilder(ctx.getEntry());

    	ModelFile cap = barsModel(prov, ctx, "cap");
    	ModelFile capAlt = barsModel(prov, ctx, "cap_alt");
    	ModelFile side = barsModel(prov, ctx, "side");
    	ModelFile sideAlt = barsModel(prov, ctx, "side_alt");

    	builder
    		.part()
	    		.modelFile(barsModel(prov, ctx, "post_ends")).addModel()
	    		.end()
	    	.part()
	    		.modelFile(barsModel(prov, ctx, "post")).addModel()
	    		.condition(PaneBlock.NORTH, false).condition(PaneBlock.EAST, false).condition(PaneBlock.SOUTH, false).condition(PaneBlock.WEST, false)
	    		.end()
	    	.part()
	    		.modelFile(cap).addModel()
	    		.condition(PaneBlock.NORTH, true).condition(PaneBlock.EAST, false).condition(PaneBlock.SOUTH, false).condition(PaneBlock.WEST, false)
	    		.end()
	    	.part()
				.modelFile(cap).rotationY(90).addModel()
				.condition(PaneBlock.NORTH, false).condition(PaneBlock.EAST, true).condition(PaneBlock.SOUTH, false).condition(PaneBlock.WEST, false)
				.end()
	    	.part()
				.modelFile(capAlt).addModel()
				.condition(PaneBlock.NORTH, false).condition(PaneBlock.EAST, false).condition(PaneBlock.SOUTH, true).condition(PaneBlock.WEST, false)
				.end()
	    	.part()
				.modelFile(capAlt).rotationY(90).addModel()
				.condition(PaneBlock.NORTH, false).condition(PaneBlock.EAST, false).condition(PaneBlock.SOUTH, false).condition(PaneBlock.WEST, true)
				.end()
			.part()
				.modelFile(side).addModel()
				.condition(PaneBlock.NORTH, true)
				.end()
			.part()
				.modelFile(side).rotationY(90).addModel()
				.condition(PaneBlock.EAST, true)
				.end()
			.part()
				.modelFile(sideAlt).addModel()
				.condition(PaneBlock.SOUTH, true)
				.end()
			.part()
				.modelFile(sideAlt).rotationY(90).addModel()
				.condition(PaneBlock.WEST, true)
				.end();
    }

    private static ModelFile barsModel(RegistrateBlockstateProvider prov, DataGenContext<Block, ?> ctx, String suffix) {
    	ResourceLocation tex = prov.blockTexture(ctx.getEntry());
    	return prov.models()
			.withExistingParent(ctx.getName() + "_" + suffix, "block/iron_bars_" + suffix)
			.texture("bars", tex).texture("edge", tex).texture("particle", tex);
    }

    private static ResourceLocation blockTexture(NamedSupplier<Block> block) {
    	ResourceLocation base = block.getId();
    	return new ResourceLocation(base.getNamespace(), "block/" + base.getPath());
    }

    private static ResourceLocation blockTexture(NamedSupplier<Block> block, String suffix) {
    	ResourceLocation base = blockTexture(block);
    	return new ResourceLocation(base.getNamespace(), base.getPath() + "_" + suffix);
    }

    public static ResourceLocation getMainTexture(NamedSupplier<Block> block, TextureType texture) {
    	switch (texture) {
    	case NORMAL:
    		return blockTexture(block);
    	case SIDE_TOP:
    		return blockTexture(block, "side");
    	default:
    		throw new IllegalArgumentException();
    	}
    }

    public static <T extends StairsBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> stairsBlock(NamedSupplier<Block> object, TextureType textureType) {
		switch (textureType) {
		case NORMAL:
			return (ctx, prov) -> prov.stairsBlock(ctx.getEntry(), blockTexture(object));
		case SIDE_TOP:
			return (ctx, prov) -> prov.stairsBlock(ctx.getEntry(), blockTexture(object, "side"), blockTexture(object, "top"), blockTexture(object, "top"));
		default:
			throw new IllegalArgumentException();
		}
	}

    public static <T extends SlabBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> slabBlock(NamedSupplier<Block> object, TextureType textureType) {
		switch (textureType) {
		case NORMAL:
			return (ctx, prov) -> prov.slabBlock(ctx.getEntry(), blockTexture(object), blockTexture(object));
		case SIDE_TOP:
			return (ctx, prov) -> prov.slabBlock(ctx.getEntry(), blockTexture(object, "side"), blockTexture(object, "side"), blockTexture(object, "top"), blockTexture(object, "top"));
		default:
			throw new IllegalArgumentException();
		}
	}

    public static <T extends FenceBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> fenceBlock(NamedSupplier<Block> object, TextureType textureType) {
		switch (textureType) {
		case NORMAL:
			return (ctx, prov) -> prov.fenceBlock(ctx.getEntry(), blockTexture(object));
		case SIDE_TOP:
			return (ctx, prov) -> prov.fenceBlock(ctx.getEntry(), getMainTexture(object, textureType));
		default:
			throw new IllegalArgumentException();
		}
	}

    public static <T extends WallBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> wallBlock(NamedSupplier<Block> object, TextureType textureType) {
		switch (textureType) {
		case NORMAL:
			return (ctx, prov) -> prov.wallBlock(ctx.getEntry(), blockTexture(object));
		case SIDE_TOP:
			return (ctx, prov) -> prov.wallBlock(ctx.getEntry(), getMainTexture(object, textureType));
		default:
			throw new IllegalArgumentException();
		}
	}
}
