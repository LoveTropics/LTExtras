package com.lovetropics.extras.data;

import com.lovetropics.extras.BlockFactory;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

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

    public static ImposterBlockTemplate cube(BlockFactory<? extends Block> factory) {
        return new ImposterBlockTemplate(factory, Model.CUBE);
    }

    public static ImposterBlockTemplate cross(BlockFactory<? extends Block> factory) {
        return new ImposterBlockTemplate(factory, Model.CROSS);
    }

    public enum Model {
        CUBE,
        CROSS;

        public BlockBuilder<? extends Block, Registrate> apply(BlockBuilder<? extends Block, Registrate> block, ResourceLocation id) {
            switch (this) {
                case CUBE: {
                    return block.blockstate((ctx, prov) -> {
                                prov.simpleBlock(ctx.getEntry(), prov.models().getExistingFile(id));
                            })
                            .simpleItem();
                }
                case CROSS: {
                    ResourceLocation texture = new ResourceLocation(id.getNamespace(), "block/" + id.getPath());
                    return block.blockstate((ctx, prov) -> {
                                prov.simpleBlock(ctx.getEntry(), prov.models().cross(ctx.getName(), texture));
                            })
                            .item()
                            .model((ctx, prov) -> prov.generated(ctx, texture))
                            .build()
                            .addLayer(() -> RenderType::cutout);
                }
                default: return block;
            }
        }
    }
}
