package com.lovetropics.extras.translation;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.LTExtras;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class TranslationOptions implements ICapabilityProvider {
    public static final ResourceLocation ID = new ResourceLocation(LTExtras.MODID, "translation");

    private final LazyOptional<TranslationOptions> instance = LazyOptional.of(() -> this);

    private boolean translateIncoming = true;
    private boolean translateOutgoing = true;

    @SubscribeEvent
    public static void onAttachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer) {
            event.addCapability(ID, new TranslationOptions());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        final Player oldPlayer = event.getOriginal();
        oldPlayer.reviveCaps();
        try {
            final TranslationOptions oldOptions = getOrNull(oldPlayer);
            final TranslationOptions newOptions = getOrNull(event.getEntity());
            if (oldOptions != null && newOptions != null) {
                newOptions.translateIncoming = oldOptions.translateIncoming;
                newOptions.translateOutgoing = oldOptions.translateOutgoing;
            }
        } finally {
            oldPlayer.invalidateCaps();
        }
    }

    public static void set(final ServerPlayer player, final boolean translateIncoming, final boolean translateOutgoing) {
        final TranslationOptions capability = getOrNull(player);
        if (capability != null) {
            capability.translateIncoming = translateIncoming;
            capability.translateOutgoing = translateOutgoing;
        }
    }

    public static CompletableFuture<TranslationBundle> translate(@Nullable final ServerPlayer player, final PlayerChatMessage message) {
        if (player != null && shouldTranslateOutgoing(player)) {
            return TranslationService.INSTANCE.translate(player.getLanguage(), message.signedContent());
        } else {
            return CompletableFuture.completedFuture(TranslationBundle.EMPTY);
        }
    }

    public static boolean shouldTranslateIncoming(final ServerPlayer player) {
        final TranslationOptions capability = getOrNull(player);
        return capability == null || capability.translateIncoming;
    }

    public static boolean shouldTranslateOutgoing(final ServerPlayer player) {
        final TranslationOptions capability = getOrNull(player);
        return capability == null || capability.translateIncoming;
    }

    @Nullable
    private static TranslationOptions getOrNull(final Player player) {
        return player.getCapability(LTExtras.TRANSLATION).orElse(null);
    }

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
        return LTExtras.TRANSLATION.orEmpty(cap, instance);
    }
}
