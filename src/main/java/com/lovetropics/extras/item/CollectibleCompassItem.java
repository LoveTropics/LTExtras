package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.entity.CollectibleEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CollectibleCompassItem extends Item {
    private static final String TAG_TARGET = "target";
    private static final int COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND * 5;
    private static final double SEARCH_RANGE = 80.0;

    public CollectibleCompassItem(final Properties properties) {
        super(properties);
    }

    @Nullable
    public static GlobalPos getTarget(final ItemStack stack) {
        if (!stack.is(ExtraItems.COLLECTIBLE_COMPASS.get())) {
            return null;
        }
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_TARGET)) {
            return GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.getCompound(TAG_TARGET)).result().orElse(null);
        }
        return null;
    }

    private static void setTarget(final ItemStack stack, final GlobalPos pos) {
        final CompoundTag tag = stack.getOrCreateTag();
        tag.put(TAG_TARGET, Util.getOrThrow(GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos), IllegalStateException::new));
    }

    private static boolean hasTarget(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(TAG_TARGET);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        if (hasTarget(stack)) {
            player.sendSystemMessage(ExtraLangKeys.COLLECTIBLE_COMPASS_ALREADY_USED.get().withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(stack);
        }
        final GlobalPos pos = tryLocateCollectible(level, player);
        if (pos != null) {
            setTarget(stack, pos);
            player.sendSystemMessage(ExtraLangKeys.COLLECTIBLE_COMPASS_SUCCESS.get().withStyle(ChatFormatting.GOLD));
        } else {
            player.sendSystemMessage(ExtraLangKeys.COLLECTIBLE_COMPASS_FAIL.get().withStyle(ChatFormatting.RED));
        }
        return InteractionResultHolder.consume(stack);
    }

    @Nullable
    private static GlobalPos tryLocateCollectible(final Level level, final Player player) {
        final CollectibleStore collectibles = CollectibleStore.getNullable(player);
        if (collectibles == null) {
            return null;
        }

        final List<CollectibleEntity> candidates = level.getEntitiesOfClass(CollectibleEntity.class, player.getBoundingBox().inflate(SEARCH_RANGE), entity -> {
            final Collectible collectible = entity.getCollectible();
            return collectible != null && !collectibles.contains(collectible);
        });

        return Util.getRandomSafe(candidates, player.getRandom())
                .map(entity -> GlobalPos.of(level.dimension(), entity.blockPosition()))
                .orElse(null);
    }

    @Override
    public boolean isFoil(final ItemStack stack) {
        return hasTarget(stack);
    }
}
