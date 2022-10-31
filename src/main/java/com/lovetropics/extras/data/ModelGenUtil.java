package com.lovetropics.extras.data;

import com.lovetropics.extras.NamedSupplier;
import com.lovetropics.extras.block.GirderBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;

import java.util.function.Function;

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

	public static void barsBlock(DataGenContext<Block, IronBarsBlock> ctx, RegistrateBlockstateProvider prov) {
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
				.condition(IronBarsBlock.NORTH, false).condition(IronBarsBlock.EAST, false).condition(IronBarsBlock.SOUTH, false).condition(IronBarsBlock.WEST, false)
				.end()
				.part()
				.modelFile(cap).addModel()
				.condition(IronBarsBlock.NORTH, true).condition(IronBarsBlock.EAST, false).condition(IronBarsBlock.SOUTH, false).condition(IronBarsBlock.WEST, false)
				.end()
				.part()
				.modelFile(cap).rotationY(90).addModel()
				.condition(IronBarsBlock.NORTH, false).condition(IronBarsBlock.EAST, true).condition(IronBarsBlock.SOUTH, false).condition(IronBarsBlock.WEST, false)
				.end()
				.part()
				.modelFile(capAlt).addModel()
				.condition(IronBarsBlock.NORTH, false).condition(IronBarsBlock.EAST, false).condition(IronBarsBlock.SOUTH, true).condition(IronBarsBlock.WEST, false)
				.end()
				.part()
				.modelFile(capAlt).rotationY(90).addModel()
				.condition(IronBarsBlock.NORTH, false).condition(IronBarsBlock.EAST, false).condition(IronBarsBlock.SOUTH, false).condition(IronBarsBlock.WEST, true)
				.end()
				.part()
				.modelFile(side).addModel()
				.condition(IronBarsBlock.NORTH, true)
				.end()
				.part()
				.modelFile(side).rotationY(90).addModel()
				.condition(IronBarsBlock.EAST, true)
				.end()
				.part()
				.modelFile(sideAlt).addModel()
				.condition(IronBarsBlock.SOUTH, true)
				.end()
				.part()
				.modelFile(sideAlt).rotationY(90).addModel()
				.condition(IronBarsBlock.WEST, true)
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
		return texture.getSideTexture(block);
	}

	public static <T extends StairBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> stairsBlock(NamedSupplier<Block> object, TextureType textureType) {
		return (ctx, prov) -> {
			ResourceLocation side = textureType.getSideTexture(object);
			ResourceLocation top = textureType.getTopTexture(object);
			prov.stairsBlock(ctx.getEntry(), side, top, top);
		};
	}

	public static <T extends SlabBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> slabBlock(NamedSupplier<Block> object, TextureType textureType) {
		return (ctx, prov) -> {
			ResourceLocation model = textureType.getModel(object);
			ResourceLocation side = textureType.getSideTexture(object);
			ResourceLocation top = textureType.getTopTexture(object);
			prov.slabBlock(ctx.getEntry(), model, side, top, top);
		};
	}

	public static <T extends FenceBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> fenceBlock(NamedSupplier<Block> object, TextureType textureType) {
		return (ctx, prov) -> prov.fenceBlock(ctx.getEntry(), textureType.getTopTexture(object));
	}

	public static <T extends WallBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> wallBlock(NamedSupplier<Block> object, TextureType textureType) {
		return (ctx, prov) -> prov.wallBlock(ctx.getEntry(), textureType.getSideTexture(object));
	}

	public interface TextureType {
		static TextureType normal() {
			return TextureType.allTexture(ModelGenUtil::blockTexture);
		}

		static TextureType sideTopSuffix() {
			return new TextureType() {
				@Override
				public ResourceLocation getModel(NamedSupplier<Block> block) {
					return ModelGenUtil.blockTexture(block);
				}

				@Override
				public ResourceLocation getSideTexture(NamedSupplier<Block> block) {
					return ModelGenUtil.blockTexture(block, "side");
				}

				@Override
				public ResourceLocation getTopTexture(NamedSupplier<Block> block) {
					return ModelGenUtil.blockTexture(block, "top");
				}
			};
		}

		static TextureType allTexture(ResourceLocation texture) {
			return allTexture(b -> texture);
		}

		static TextureType allTexture(Function<NamedSupplier<Block>, ResourceLocation> texture) {
			return simple(ModelGenUtil::blockTexture, texture);
		}

		static TextureType simple(Function<NamedSupplier<Block>, ResourceLocation> model, Function<NamedSupplier<Block>, ResourceLocation> texture) {
			return new TextureType() {
				@Override
				public ResourceLocation getModel(NamedSupplier<Block> block) {
					return model.apply(block);
				}

				@Override
				public ResourceLocation getSideTexture(NamedSupplier<Block> block) {
					return texture.apply(block);
				}

				@Override
				public ResourceLocation getTopTexture(NamedSupplier<Block> block) {
					return texture.apply(block);
				}
			};
		}

		ResourceLocation getModel(NamedSupplier<Block> block);

		ResourceLocation getSideTexture(NamedSupplier<Block> block);

		ResourceLocation getTopTexture(NamedSupplier<Block> block);
	}
}
