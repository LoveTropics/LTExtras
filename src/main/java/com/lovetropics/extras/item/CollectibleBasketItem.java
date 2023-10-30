package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.lovetropics.extras.network.OpenCollectibleBasketPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class CollectibleBasketItem extends Item {
    public CollectibleBasketItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        if (!level.isClientSide() && player instanceof final ServerPlayer serverPlayer) {
            final CollectibleStore collectibles = CollectibleStore.getNullable(serverPlayer);
            if (collectibles != null && !collectibles.isLocked()) {
                LTExtrasNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new OpenCollectibleBasketPacket());
            } else {
                serverPlayer.sendSystemMessage(ExtraLangKeys.COLLECTIBLES_LOCKED.get().withStyle(ChatFormatting.RED), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
