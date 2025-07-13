package com.lovetropics.extras.data;

import com.lovetropics.extras.block.BoringEndRodBlock;
import com.lovetropics.extras.block.GirderBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Holder;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Optional;
import java.util.function.Function;

public class ModelGenUtil {

	public static final TextureSlot BARS = TextureSlot.create("bars");
	public static final TextureSlot GLOW_STICKS = TextureSlot.create("glow_sticks");
	public static final ModelTemplate IRON_BARS_CAP = ModelTemplates.create("iron_bars", "_cap", BARS, TextureSlot.EDGE, TextureSlot.PARTICLE);
	public static final ModelTemplate IRON_BARS_CAP_ALT = ModelTemplates.create("iron_bars", "_cap_alt", BARS, TextureSlot.EDGE, TextureSlot.PARTICLE);
	public static final ModelTemplate IRON_BARS_SIDE = ModelTemplates.create("iron_bars", "_side", BARS, TextureSlot.EDGE, TextureSlot.PARTICLE);
	public static final ModelTemplate IRON_BARS_SIDE_ALT = ModelTemplates.create("iron_bars", "_side_alt", BARS, TextureSlot.EDGE, TextureSlot.PARTICLE);
	public static final ModelTemplate IRON_BARS_POST = ModelTemplates.create("iron_bars", "_post", BARS, TextureSlot.PARTICLE);
	public static final ModelTemplate IRON_BARS_POST_ENDS = ModelTemplates.create("iron_bars", "_post_ends", TextureSlot.EDGE, TextureSlot.PARTICLE);
	public static final ModelTemplate SCAFFOLDING_STABLE = ModelTemplates.create("scaffolding", "_stable", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.PARTICLE);
	public static final ModelTemplate SCAFFOLDING_UNSTABLE = ModelTemplates.create("scaffolding", "_unstable", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.PARTICLE);
	public static final ModelTemplate GIRDER = new ModelTemplate(
			Optional.of(
					ModelLocationUtils.decorateBlockModelLocation("ltextras:girder_straight")
			), Optional.empty(), TextureSlot.TEXTURE);
	public static final ModelTemplate LADDER = ModelTemplates.create("ladder", TextureSlot.TEXTURE);
	public static final ModelTemplate PAPYRUS_STEM = new ModelTemplate(Optional.of(
			ModelLocationUtils.decorateBlockModelLocation("ltextras:papyrus_stem")
	), Optional.empty(), TextureSlot.ALL);

	public static void steelGirderBlockstate(DataGenContext<Block, GirderBlock> ctx, RegistrateBlockModelGenerator prov) {
		ResourceLocation girderModel = GIRDER.create(ctx.getEntry(), TextureMapping.defaultTexture(prov.modLoc("block/" + ctx.getName())), prov.modelOutput);

		MultiVariant variant = BlockModelGenerators.plainVariant(girderModel)
				.with(BlockModelGenerators.UV_LOCK);

		MultiPartGenerator multiPartGenerator = MultiPartGenerator.multiPart(ctx.get())
				.with(BlockModelGenerators.condition()
								.term(GirderBlock.PROPS.get(Axis.X), true)
								.build(),
						variant.with(BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90)))
				.with(BlockModelGenerators.condition()
								.term(GirderBlock.PROPS.get(Axis.Y), true)
								.build(),
						variant)
				.with(BlockModelGenerators.condition()
								.term(GirderBlock.PROPS.get(Axis.Z), true)
								.build(),
						variant.with(BlockModelGenerators.X_ROT_90))
				.with(BlockModelGenerators.condition()
								.term(GirderBlock.PROPS.get(Axis.Z), false)
								.term(GirderBlock.PROPS.get(Axis.Y), false)
								.term(GirderBlock.PROPS.get(Axis.Z), false)
								.build(),
						variant.with(BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90))
								.with(BlockModelGenerators.X_ROT_90));
		prov.blockStateOutput.accept(multiPartGenerator);
	}

	public static MultiVariant scaffoldingModel(DataGenContext<Block, ScaffoldingBlock> ctx, RegistrateBlockModelGenerator prov, ModelTemplate modelTemplate) {
		return BlockModelGenerators.plainVariant(modelTemplate.create(ctx.getEntry(),
				new TextureMapping()
						.put(TextureSlot.BOTTOM,  prov.modLoc("block/metal_scaffolding_bottom"))
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
				MultiPartGenerator.multiPart(Blocks.IRON_BARS)
						.with(postEnds)
						.with(
								BlockModelGenerators.condition()
										.term(BlockStateProperties.NORTH, false)
										.term(BlockStateProperties.EAST, false)
										.term(BlockStateProperties.SOUTH, false)
										.term(BlockStateProperties.WEST, false),
								post
						)
						.with(
								BlockModelGenerators.condition()
										.term(BlockStateProperties.NORTH, true)
										.term(BlockStateProperties.EAST, false)
										.term(BlockStateProperties.SOUTH, false)
										.term(BlockStateProperties.WEST, false),
								cap
						)
						.with(
								BlockModelGenerators.condition()
										.term(BlockStateProperties.NORTH, false)
										.term(BlockStateProperties.EAST, true)
										.term(BlockStateProperties.SOUTH, false)
										.term(BlockStateProperties.WEST, false),
								cap.with(BlockModelGenerators.Y_ROT_90)
						)
						.with(
								BlockModelGenerators.condition()
										.term(BlockStateProperties.NORTH, false)
										.term(BlockStateProperties.EAST, false)
										.term(BlockStateProperties.SOUTH, true)
										.term(BlockStateProperties.WEST, false),
								capAlt
						)
						.with(
								BlockModelGenerators.condition()
										.term(BlockStateProperties.NORTH, false)
										.term(BlockStateProperties.EAST, false)
										.term(BlockStateProperties.SOUTH, false)
										.term(BlockStateProperties.WEST, true),
								capAlt.with(BlockModelGenerators.Y_ROT_90)
						)
						.with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), side)
						.with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), side.with(BlockModelGenerators.Y_ROT_90))
						.with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), sideAlt)
						.with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), sideAlt.with(BlockModelGenerators.Y_ROT_90))
		);
		prov.registerSimpleFlatItemModel(ctx.getEntry());
	}

	private static MultiVariant barsModel(RegistrateBlockModelGenerator prov, DataGenContext<Block, ?> ctx, ModelTemplate modelTemplate) {
		ResourceLocation tex = ModelLocationUtils.getModelLocation(ctx.getEntry());
		return BlockModelGenerators.plainVariant(modelTemplate.create(ctx.getEntry(),
				new TextureMapping()
						.put(BARS, tex)
						.put(TextureSlot.EDGE, tex)
						.put(TextureSlot.PARTICLE, tex), prov.modelOutput));
	}

	private static ResourceLocation blockTexture(Holder<Block> block) {
		ResourceLocation base = block.unwrapKey().orElseThrow().location();
		return base.withPrefix("block/");
	}

	private static ResourceLocation blockTexture(Holder<Block> block, String suffix) {
		ResourceLocation base = blockTexture(block);
		return base.withSuffix("_" + suffix);
	}

	public static ResourceLocation getMainTexture(Holder<Block> block, TextureType texture) {
		return texture.getSideTexture(block);
	}

	public static void rodBlock(DataGenContext<Block, BoringEndRodBlock> ctx, RegistrateBlockModelGenerator prov) {
		prov.blockStateOutput
				.accept(MultiVariantGenerator.dispatch(ctx.getEntry(),
						BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.END_ROD))).with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));
	}

	public static <T extends StairBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockModelGenerator> stairsBlock(Holder<Block> object, TextureType textureType) {
		return (ctx, prov) -> {
			ResourceLocation side = textureType.getSideTexture(object);
			ResourceLocation top = textureType.getTopTexture(object);
			MultiVariant sideVariant = BlockModelGenerators.plainVariant(side);
			MultiVariant topVariant = BlockModelGenerators.plainVariant(top);
			prov.blockStateOutput.accept(BlockModelGenerators.createStairs(ctx.getEntry(), sideVariant, topVariant, topVariant));
		};
	}

	public static <T extends SlabBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockModelGenerator> slabBlock(Holder<Block> object, TextureType textureType) {
		return (ctx, prov) -> {
			ResourceLocation model = textureType.getModel(object);
			ResourceLocation side = textureType.getSideTexture(object);
			ResourceLocation top = textureType.getTopTexture(object);
			MultiVariant modelVariant = BlockModelGenerators.plainVariant(model);
			MultiVariant sideVariant = BlockModelGenerators.plainVariant(side);
			MultiVariant topVariant = BlockModelGenerators.plainVariant(top);
			prov.blockStateOutput.accept(
					BlockModelGenerators.createSlab(ctx.getEntry(), sideVariant, modelVariant, topVariant)
			);
		};
	}

	public static <T extends FenceBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockModelGenerator> fenceBlock(Holder<Block> object, TextureType textureType) {
		return (ctx, prov) -> {
			ResourceLocation topTexture = textureType.getTopTexture(object);
			MultiVariant topVariant = BlockModelGenerators.plainVariant(topTexture);
			prov.blockStateOutput.accept(BlockModelGenerators.createFence(ctx.getEntry(), topVariant, topVariant));
		};
	}

	public static <T extends WallBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockModelGenerator> wallBlock(Holder<Block> object, TextureType textureType) {
		return (ctx, prov) -> {
			ResourceLocation sideTexture = textureType.getSideTexture(object);
			MultiVariant sideVariant = BlockModelGenerators.plainVariant(sideTexture);
			prov.blockStateOutput.accept(BlockModelGenerators.createWall(ctx.getEntry(), sideVariant, sideVariant, sideVariant));
		};
	}
	public interface TextureType {
		static TextureType normal() {
			return TextureType.allTexture(ModelGenUtil::blockTexture);
		}

		static TextureType allWithSuffix(Block donor, String suffix) {
			return TextureType.allTexture(ignored -> blockTexture(donor.builtInRegistryHolder(), suffix));
		}

		static TextureType sideTopSuffix() {
			return new TextureType() {
				@Override
				public ResourceLocation getModel(Holder<Block> block) {
					return ModelGenUtil.blockTexture(block);
				}

				@Override
				public ResourceLocation getSideTexture(Holder<Block> block) {
					return ModelGenUtil.blockTexture(block, "side");
				}

				@Override
				public ResourceLocation getTopTexture(Holder<Block> block) {
					return ModelGenUtil.blockTexture(block, "top");
				}
			};
		}

		static TextureType allTexture(ResourceLocation texture) {
			return allTexture(b -> texture);
		}

		static TextureType allTexture(Function<Holder<Block>, ResourceLocation> texture) {
			return simple(ModelGenUtil::blockTexture, texture);
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
