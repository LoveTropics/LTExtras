package com.lovetropics.extras.data;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = LTExtras.MODID)
public class TropiCoinsStore {
    public static final MapCodec<TropiCoinsStore> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("amount").forGetter(TropiCoinsStore::getAmount)
    ).apply(i, TropiCoinsStore::new));
    public static final StreamCodec<ByteBuf, TropiCoinsStore> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TropiCoinsStore::getAmount,
            TropiCoinsStore::new
    );

    private int amount;

    protected TropiCoinsStore(int amount) {
        this.amount = amount;
    }

    public TropiCoinsStore() {
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

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
