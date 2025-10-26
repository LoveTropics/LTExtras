package com.lovetropics.extras.data;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = LTExtras.MODID)
public class TropiCoinsStore {
    public static final MapCodec<TropiCoinsStore> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("amount").forGetter(TropiCoinsStore::getAmount)
    ).apply(instance, TropiCoinsStore::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, TropiCoinsStore> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TropiCoinsStore::getAmount,
            TropiCoinsStore::new);

    private int tropiCoinsCount;

    protected TropiCoinsStore(int amount) {
        this.tropiCoinsCount = amount;
    }

    public TropiCoinsStore() {
        this.tropiCoinsCount = 0;
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player player = event.getEntity();
        if (event.isWasDeath()) {
            player.getData(ExtraAttachments.TROPICOINS_STORE).setAmount(original.getData(ExtraAttachments.TROPICOINS_STORE).getAmount());
            player.syncData(ExtraAttachments.TROPICOINS_STORE);
        }
    }

    public static void setAmountServerbound(Player player, int amount) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getData(ExtraAttachments.TROPICOINS_STORE.get()).setAmount(amount);
            serverPlayer.syncData(ExtraAttachments.TROPICOINS_STORE);
        }
    }

    public void setAmount(int amount) {
        this.tropiCoinsCount = amount;
    }

    public int getAmount() {
        return this.tropiCoinsCount;
    }
}
