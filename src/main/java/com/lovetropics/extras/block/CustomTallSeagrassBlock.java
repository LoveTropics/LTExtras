package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class CustomTallSeagrassBlock extends TallSeagrassBlock {

	public static BlockBuilder<CustomTallSeagrassBlock, Registrate> dropping(RegistryEntry<? extends SeagrassBlock> drop) {
		return ExtraBlocks.REGISTRATE.block("tall_" + drop.getId().getPath(), p -> new CustomTallSeagrassBlock(p, drop))
				.initialProperties(drop)
				.loot((p, b) -> p.dropOther(b, drop.get()))
				.lang("Tall Seagrass")
				.addLayer(() -> RenderType::cutout)
				.blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.get())
						.partialState().with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER).addModels(new ConfiguredModel(prov.models()
								.singleTexture(ctx.getName(), prov.mcLoc("block/template_seagrass"), prov.modLoc("block/" + ctx.getName() + "_top"))))
						.partialState().with(TallSeagrassBlock.HALF, DoubleBlockHalf.LOWER).addModels(new ConfiguredModel(prov.models()
								.singleTexture(ctx.getName(), prov.mcLoc("block/template_seagrass"), prov.modLoc("block/" + ctx.getName() + "_bottom")))));
	}

	private final NonNullSupplier<? extends SeagrassBlock> drop;

	public CustomTallSeagrassBlock(Properties p, NonNullSupplier<? extends SeagrassBlock> drop) {
		super(p);
		this.drop = drop;
 	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter p_154749_, BlockPos p_154750_, BlockState p_154751_) {
		return new ItemStack(drop.get());
	}
}
