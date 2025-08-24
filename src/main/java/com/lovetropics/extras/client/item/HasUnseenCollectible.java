package com.lovetropics.extras.client.item;

import com.lovetropics.extras.client.ClientCollectiblesList;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record HasUnseenCollectible() implements ConditionalItemModelProperty {
	public static final MapCodec<HasUnseenCollectible> MAP_CODEC = MapCodec.unit(HasUnseenCollectible::new);

	@Override
	public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
		ClientCollectiblesList collectibles = ClientCollectiblesList.getOrNull();
		return collectibles != null && collectibles.hasUnseen();
	}

	@Override
	public MapCodec<HasUnseenCollectible> type() {
		return MAP_CODEC;
	}
}
