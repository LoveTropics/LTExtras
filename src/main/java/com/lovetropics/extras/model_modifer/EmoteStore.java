package com.lovetropics.extras.model_modifer;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@EventBusSubscriber
public class EmoteStore {
    public static final MapCodec<EmoteStore> MAP_CODEC = Emote.CODEC.optionalFieldOf("emote")
            .xmap(EmoteStore::new, emote -> Optional.ofNullable(emote.activeEmote));

    private @Nullable Emote activeEmote;

    public EmoteStore() {
    }

    private EmoteStore(Optional<Emote> activeEmote) {
        this.activeEmote = activeEmote.orElse(null);
    }

    public static void set(Entity entity, @Nullable Emote activeEmote) {
        EmoteStore store = getOrDefault(entity);
        if (store.activeEmote != null) {
            for (Holder<ModelModifier> modifier : store.activeEmote.modifiers()) {
                ModelModifierStore.removeModifier(entity, modifier);
            }
        }
        store.activeEmote = activeEmote;
        if (activeEmote != null) {
            for (Holder<ModelModifier> modifier : activeEmote.modifiers()) {
                ModelModifierStore.addModifier(entity, modifier);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getKnownMovement().lengthSqr() > 0.0) {
                set(serverPlayer, null);
            }
        }
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            Emote emote = event.getItemStack().get(ExtraDataComponents.EMOTE);
            if (emote != null) {
                set(serverPlayer, emote);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    public static EmoteStore getOrDefault(Entity entity) {
        return entity.getData(ExtraAttachments.EMOTE);
    }
}
