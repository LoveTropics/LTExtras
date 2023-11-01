package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.entity.CollectibleEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class CollectibleCompassItem extends Item {
    private static final String TAG_TARGET = "target";
    private static final String COIN_COUNT = "coin_count";
    private static final String ENTITY_TAG_IGNORE = "compass_hidden";

    private static final int COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND * 5;
    private static final double SEARCH_RANGE = 80.0;

    public CollectibleCompassItem(final Properties properties) {
        super(properties);
    }

    @Nullable
    public static Target getTarget(final ItemStack stack) {
        if (!stack.is(ExtraItems.COLLECTIBLE_COMPASS.get())) {
            return null;
        }
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_TARGET)) {
            return Target.CODEC.parse(NbtOps.INSTANCE, tag.getCompound(TAG_TARGET)).result().orElse(null);
        }
        return null;
    }

    public static int getCoinCount(final ItemStack stack) {
        if (stack.getTag() == null) {
            return 0;
        }
        return stack.getTag().getInt(COIN_COUNT);
    }

    private static void setTarget(final ItemStack stack, final Target target) {
        final CompoundTag tag = stack.getOrCreateTag();
        tag.put(TAG_TARGET, Util.getOrThrow(Target.CODEC.encodeStart(NbtOps.INSTANCE, target), IllegalStateException::new));
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
        final Target target = tryLocateCollectible(level, player);
        if (target != null) {
            setTarget(stack, target);
            player.sendSystemMessage(ExtraLangKeys.COLLECTIBLE_COMPASS_SUCCESS.get().withStyle(ChatFormatting.GOLD));
        } else {
            player.sendSystemMessage(ExtraLangKeys.COLLECTIBLE_COMPASS_FAIL.get().withStyle(ChatFormatting.RED));
        }
        return InteractionResultHolder.consume(stack);
    }

    @Nullable
    private static Target tryLocateCollectible(final Level level, final Player player) {
        final CollectibleStore collectibles = CollectibleStore.getNullable(player);
        if (collectibles == null) {
            return null;
        }

        final List<CollectibleEntity> candidates = level.getEntitiesOfClass(CollectibleEntity.class, player.getBoundingBox().inflate(SEARCH_RANGE), entity -> {
            if (entity.getTags().contains(ENTITY_TAG_IGNORE)) {
                return false;
            }
            final Collectible collectible = entity.getCollectible();
            return collectible != null && !collectibles.contains(collectible);
        });

        return Util.getRandomSafe(candidates, player.getRandom())
                .map(entity -> new Target(GlobalPos.of(level.dimension(), entity.blockPosition()), entity.getUUID()))
                .orElse(null);
    }

    @Override
    public boolean isFoil(final ItemStack stack) {
        return hasTarget(stack);
    }

    public record Target(GlobalPos pos, UUID id) {
        public static final Codec<Target> CODEC = RecordCodecBuilder.create(i -> i.group(
        		GlobalPos.CODEC.fieldOf("pos").forGetter(Target::pos),
                UUIDUtil.CODEC.fieldOf("id").forGetter(Target::id)
        ).apply(i, Target::new));
    }
}
