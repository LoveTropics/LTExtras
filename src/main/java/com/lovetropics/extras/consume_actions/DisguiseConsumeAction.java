package com.lovetropics.extras.consume_actions;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lovetropics.peekaboo.api.Disguise;
import org.lovetropics.peekaboo.api.EntityDisguiseHolder;

import java.util.Optional;

@EventBusSubscriber
public record DisguiseConsumeAction(
        Disguise disguise,
        Optional<Integer> length,
        Optional<Integer> cooldown) implements ConsumeAction {

    public static final MapCodec<DisguiseConsumeAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Disguise.CODEC.fieldOf("disguise").forGetter(DisguiseConsumeAction::disguise),
            Codec.INT.optionalFieldOf("length").forGetter(DisguiseConsumeAction::length),
            Codec.INT.optionalFieldOf("cooldown").forGetter(DisguiseConsumeAction::cooldown)
    ).apply(i, DisguiseConsumeAction::new));


    @Override
    public void onConsume(ServerPlayer serverPlayer, ItemStack itemStack) {
        EntityDisguiseHolder disguiseHolder = EntityDisguiseHolder.getOrNull(serverPlayer);
        if (disguiseHolder == null) {
            return;
        }

        disguiseHolder.set(disguise);

        length.ifPresent(integer -> serverPlayer.setData(ExtraAttachments.DISGUISE_TIMER_STORE, new DisguiseTimerStore(Optional.of(disguise), serverPlayer.level().getServer().getTickCount() + integer)));

        cooldown.ifPresent(integer -> serverPlayer.getCooldowns().addCooldown(itemStack, integer));
    }

    @Override
    public MapCodec<? extends ConsumeAction> getCodec() {
        return CODEC;
    }

    public record DisguiseTimerStore(Optional<Disguise> disguise, long removeTick) {

        public static final DisguiseTimerStore EMPTY = new DisguiseTimerStore(Optional.empty(), 0);

        public static final MapCodec<DisguiseTimerStore> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Disguise.CODEC.optionalFieldOf("disguise").forGetter(DisguiseTimerStore::disguise),
                Codec.LONG.fieldOf("remove_tick").forGetter(DisguiseTimerStore::removeTick)
        ).apply(i, DisguiseTimerStore::new));

        public void tryForClearing(ServerPlayer serverPlayer) {
            if (serverPlayer.level().getServer().getTickCount() < removeTick) {
                return;
            }

            if (disguise.isEmpty()) {
                return;
            }

            EntityDisguiseHolder disguiseHolder = EntityDisguiseHolder.getOrNull(serverPlayer);
            if (disguiseHolder != null) {
                disguiseHolder.clear(disguise.get());
            }

            serverPlayer.removeData(ExtraAttachments.DISGUISE_TIMER_STORE);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            DisguiseTimerStore disguiseTimerStore = serverPlayer.getExistingDataOrNull(ExtraAttachments.DISGUISE_TIMER_STORE);
            if (disguiseTimerStore != null) {
                disguiseTimerStore.tryForClearing(serverPlayer);
            }
        }
    }
}
