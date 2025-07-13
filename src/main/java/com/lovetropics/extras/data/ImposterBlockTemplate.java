package com.lovetropics.extras.data;

import com.lovetropics.extras.BlockFactory;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;

public final class ImposterBlockTemplate {
	public final BlockFactory<? extends Block> factory;
	public final Model model;

	public ImposterBlockTemplate(BlockFactory<? extends Block> factory, Model model) {
		this.factory = factory;
		this.model = model;
	}

	public static ImposterBlockTemplate simpleCube() {
		return ImposterBlockTemplate.cube(Block::new);
	}

	public static ImposterBlockTemplate halfTransparentCube() {
		return new ImposterBlockTemplate(HalfTransparentBlock::new, Model.HALF_TRANSPARENT_CUBE);
	}

	public static ImposterBlockTemplate cube(BlockFactory<? extends Block> factory) {
		return new ImposterBlockTemplate(factory, Model.CUBE);
	}

	public static ImposterBlockTemplate cross(BlockFactory<? extends Block> factory) {
		return new ImposterBlockTemplate(factory, Model.CROSS);
	}

	public enum Model {
		CUBE,
		HALF_TRANSPARENT_CUBE,
		CROSS;

		public BlockBuilder<? extends Block, Registrate> apply(BlockBuilder<? extends Block, Registrate> block, ResourceLocation id) {
            return switch (this) {
                case CUBE -> block.blockstate(() -> (ctx, prov) -> prov.create(ctx.getEntry(), id))
                        .simpleItem();
				case HALF_TRANSPARENT_CUBE -> block
						.blockstate(() -> (ctx, prov)
								-> prov.create(ctx.getEntry(),
										prov.getBuilder()
												.transformTemplate(template -> template.parent(id))
												.renderType(prov.mcLoc("translucent"))
												.build(ctx.getEntry())))
//						.addLayer(() -> RenderType::translucent)
						.simpleItem();
                case CROSS -> {
                    ResourceLocation texture = id.withPath("block/" + id.getPath());
                    yield block.blockstate(() -> (ctx, prov) -> {
								prov.createCrossBlock(ctx.getEntry(), BlockModelGenerators.PlantType.NOT_TINTED, TextureMapping.crop(texture));
                            })
                            .item()
                            .model(() -> (ctx, prov) ->
											ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(ctx.getEntry()),
													TextureMapping.layer0(texture), prov.modelOutput))
                            .build();
                }
            };
		}
	}
}
