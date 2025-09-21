package com.lovetropics.extras.data;

import com.lovetropics.extras.BlockFactory;
import com.lovetropics.extras.block.PapyrusUmbelBlock;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;

import static net.minecraft.client.data.models.BlockModelGenerators.createSimpleBlock;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;
import static net.minecraft.client.data.models.blockstates.MultiVariantGenerator.dispatch;

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

        public static void generatePapyrusUmbel(DataGenContext<Block, PapyrusUmbelBlock> ctx, RegistrateBlockModelGenerator prov) {
            prov.blockStateOutput.accept(dispatch(ctx.get())
                    .with(PropertyDispatch.initial(PapyrusUmbelBlock.TYPE)
                            .generate((type) -> {
                                final String typeName = type.getSerializedName();
                                final String modelName = typeName + "_" + ctx.getName();
                                final ResourceLocation texture = prov.modLoc("block/papyrus/" + modelName);
                                ResourceLocation model = prov.getBuilder()
                                        .transformTemplate(template -> {
                                            template.parent(prov.mcLoc("block/sugar_cane"));
                                            template.renderType(prov.mcLoc("cutout"));
                                        }).texture(TextureSlot.CROSS, texture).build(prov.modLoc("block/" + modelName));
                                return plainVariant(model);
                            })));
        }

        public BlockBuilder<? extends Block, Registrate> apply(BlockBuilder<? extends Block, Registrate> block, ResourceLocation id) {
            return switch (this) {
                case CUBE -> block.blockstate(() -> (ctx, prov) -> prov.create(ctx.getEntry(), id.withPrefix("block/")))
                        .simpleItem();
                case HALF_TRANSPARENT_CUBE -> block
                        .blockstate(() -> (ctx, prov) ->
                                prov.blockStateOutput.accept(createSimpleBlock(ctx.get(), plainVariant(id.withPrefix("block/"))))
                        )
                        .addLayer(() -> () -> ChunkSectionLayer.TRANSLUCENT)
                        .simpleItem();
                case CROSS -> {
                    ResourceLocation texture = id.withPrefix("block/");
                    yield block
                            .blockstate(() -> (ctx, prov) -> {
                                prov.createCrossBlock(ctx.get(), BlockModelGenerators.PlantType.NOT_TINTED, TextureMapping.cross(texture));
                            })
                            .addLayer(() -> () -> ChunkSectionLayer.CUTOUT)
                            .item()
                            .model(() -> (ctx, prov) -> prov.generateFlatItem(ctx.get(), texture))
                            .build();
                }
            };
        }
    }
}
