package com.lovetropics.extras.client;

import com.lovetropics.extras.network.message.ServerboundRideZiplinePacket;
import com.lovetropics.extras.zipline.ZiplineEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(Dist.CLIENT)
public final class ZiplineInteraction {
    @SubscribeEvent
    public static void onUse(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!event.isUseItem() || minecraft.player == null
                || minecraft.player.isPassenger() || minecraft.player.isSpectator()) {
            return;
        }
        ZiplineEntity cable = ZiplineEntity.pick(minecraft.player);
        if (cable != null) {
            if (event.getHand() == InteractionHand.MAIN_HAND) {
                ClientPacketDistributor.sendToServer(new ServerboundRideZiplinePacket(cable.getId()));
            }
            event.setCanceled(true);
        }
    }
}
