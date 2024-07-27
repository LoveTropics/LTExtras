package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.entity.CollectibleEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
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
    private static final String ENTITY_TAG_IGNORE = "compass_hidden";

    private static final int COOLDOWN_TICKS = SharedConstants.TICKS_PER_SECOND * 5;
    private static final double SEARCH_RANGE = 80.0;

    public CollectibleCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        if (stack.has(ExtraDataComponents.COLLECTIBLE_TARGET)) {
            player.sendSystemMessage(ExtraLangKeys.COLLECTIBLE_COMPASS_ALREADY_USED.get().withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(stack);
        }
        Target target = tryLocateCollectible(level, player);
        if (target != null) {
            stack.set(ExtraDataComponents.COLLECTIBLE_TARGET, target);
            player.sendSystemMessage(ExtraLangKeys.COLLECTIBLE_COMPASS_SUCCESS.get().withStyle(ChatFormatting.GOLD));
        } else {
            player.sendSystemMessage(ExtraLangKeys.COLLECTIBLE_COMPASS_FAIL.get().withStyle(ChatFormatting.RED));
        }
        return InteractionResultHolder.consume(stack);
    }

    @Nullable
    private static Target tryLocateCollectible(Level level, Player player) {
        CollectibleStore collectibles = CollectibleStore.get(player);

        List<CollectibleEntity> candidates = level.getEntitiesOfClass(CollectibleEntity.class, player.getBoundingBox().inflate(SEARCH_RANGE), entity -> {
            if (entity.getTags().contains(ENTITY_TAG_IGNORE)) {
                return false;
            }
            Holder<Collectible> collectible = entity.getCollectible();
            return collectible != null && !collectibles.contains(collectible);
        });

        return Util.getRandomSafe(candidates, player.getRandom())
                .map(entity -> new Target(GlobalPos.of(level.dimension(), entity.blockPosition()), entity.getUUID()))
                .orElse(null);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(ExtraDataComponents.COLLECTIBLE_TARGET);
    }

    public record Target(GlobalPos pos, UUID id) {
        public static final Codec<Target> CODEC = RecordCodecBuilder.create(i -> i.group(
        		GlobalPos.CODEC.fieldOf("pos").forGetter(Target::pos),
                UUIDUtil.CODEC.fieldOf("id").forGetter(Target::id)
        ).apply(i, Target::new));

        public static final StreamCodec<ByteBuf, Target> STREAM_CODEC = StreamCodec.composite(
                GlobalPos.STREAM_CODEC, Target::pos,
                UUIDUtil.STREAM_CODEC, Target::id,
                Target::new
        );
    }
}
