package com.lovetropics.extras;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.lovetropics.extras.block.CustomShapeBlock;
import com.lovetropics.extras.block.WaterBarrierBlock;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.registries.IRegistryDelegate;

public class ExtraBlocks {
	
	public static final Registrate REGISTRATE = LTExtras.registrate();

    public static final BlockEntry<WaterBarrierBlock> WATER_BARRIER = REGISTRATE.block("water_barrier", WaterBarrierBlock::new)
            .properties(p -> Block.Properties.from(Blocks.BARRIER).noDrops())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), 
                    prov.models().getBuilder(ctx.getName()).texture("particle", new ResourceLocation("item/barrier"))))
            .item()
                .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation("block/water_still"), new ResourceLocation("item/barrier")))
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
    
    private enum TextureType {
    	NORMAL,
    	SIDE_TOP,
    	;
    }
    
    private static final Map<IRegistryDelegate<Block>, TextureType> STAIR_TEMPLATES = ImmutableMap.<IRegistryDelegate<Block>, TextureType>builder()
    		.put(Blocks.GOLD_BLOCK.delegate, TextureType.NORMAL)
    		.build();
    
    private static final Map<IRegistryDelegate<Block>, TextureType> FENCE_WALL_TEMPLATES = ImmutableMap.<IRegistryDelegate<Block>, TextureType>builder()
    		.put(Blocks.GOLD_BLOCK.delegate, TextureType.NORMAL)
    		.put(Blocks.QUARTZ_BLOCK.delegate, TextureType.SIDE_TOP)
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
    
    public static final Map<IRegistryDelegate<Block>, BlockEntry<? extends FenceBlock>> FENCES = FENCE_WALL_TEMPLATES.entrySet().stream()
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
    
    public static final Map<IRegistryDelegate<Block>, BlockEntry<? extends WallBlock>> WALLS = FENCE_WALL_TEMPLATES.entrySet().stream()
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
