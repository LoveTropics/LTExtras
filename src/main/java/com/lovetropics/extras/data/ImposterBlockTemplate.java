package com.lovetropics.extras.data;

import com.lovetropics.extras.BlockFactory;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.block.Block;
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

        public void apply(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, ResourceLocation id) {
            switch (this) {
                case CUBE: {
                    prov.simpleBlock(ctx.getEntry(), prov.models().getExistingFile(id));
                    break;
                }
                case CROSS: {
                    ResourceLocation texture = new ResourceLocation(id.getNamespace(), "block/" + id.getPath());
                    prov.simpleBlock(ctx.getEntry(), prov.models().cross(ctx.getName(), texture));
                    break;
                }
                default: throw new UnsupportedOperationException();
            }
        }
    }
}
