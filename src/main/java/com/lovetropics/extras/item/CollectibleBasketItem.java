package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.network.message.ClientboundOpenCollectibleBasketPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class CollectibleBasketItem extends Item {
    public CollectibleBasketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            CollectibleStore collectibles = CollectibleStore.get(serverPlayer);
            if (!collectibles.isLocked()) {
                collectibles.markSeen();
                PacketDistributor.sendToPlayer(serverPlayer, new ClientboundOpenCollectibleBasketPacket());
            } else {
                serverPlayer.sendSystemMessage(ExtraLangKeys.COLLECTIBLES_LOCKED.get().withStyle(ChatFormatting.RED), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
