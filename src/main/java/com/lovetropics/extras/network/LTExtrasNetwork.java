package com.lovetropics.extras.network;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.message.ClientboundCollectiblesListPacket;
import com.lovetropics.extras.network.message.ClientboundOpenCollectibleBasketPacket;
import com.lovetropics.extras.network.message.ClientboundPoiPacket;
import com.lovetropics.extras.network.message.ClientboundSetHologramTextPacket;
import com.lovetropics.extras.network.message.ClientboundSetSkyColorPacket;
import com.lovetropics.extras.network.message.ClientboundWorldParticleEffectsPacket;
import com.lovetropics.extras.network.message.ServerboundPickCollectibleItemPacket;
import com.lovetropics.extras.network.message.ServerboundReturnCollectibleItemPacket;
import com.lovetropics.extras.network.message.ServerboundSetTimeZonePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = LTExtras.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LTExtrasNetwork {
    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(LTExtras.getCompatVersion());

        registrar.playToClient(ClientboundCollectiblesListPacket.TYPE, ClientboundCollectiblesListPacket.STREAM_CODEC, ClientboundCollectiblesListPacket::handle);
        registrar.playToServer(ServerboundPickCollectibleItemPacket.TYPE, ServerboundPickCollectibleItemPacket.STREAM_CODEC, ServerboundPickCollectibleItemPacket::handle);
        registrar.playToServer(ServerboundReturnCollectibleItemPacket.TYPE, ServerboundReturnCollectibleItemPacket.STREAM_CODEC, ServerboundReturnCollectibleItemPacket::handle);
        registrar.playToServer(ServerboundSetTimeZonePacket.TYPE, ServerboundSetTimeZonePacket.STREAM_CODEC, ServerboundSetTimeZonePacket::handle);
        registrar.playToClient(ClientboundSetHologramTextPacket.TYPE, ClientboundSetHologramTextPacket.STREAM_CODEC, ClientboundSetHologramTextPacket::handle);
        registrar.playToClient(ClientboundSetSkyColorPacket.TYPE, ClientboundSetSkyColorPacket.STREAM_CODEC, ClientboundSetSkyColorPacket::handle);
        registrar.playToClient(ClientboundWorldParticleEffectsPacket.TYPE, ClientboundWorldParticleEffectsPacket.STREAM_CODEC, ClientboundWorldParticleEffectsPacket::handle);
        registrar.playToClient(ClientboundPoiPacket.TYPE, ClientboundPoiPacket.STREAM_CODEC, ClientboundPoiPacket::handle);
        registrar.playToClient(ClientboundOpenCollectibleBasketPacket.TYPE, ClientboundOpenCollectibleBasketPacket.STREAM_CODEC, ClientboundOpenCollectibleBasketPacket::handle);
    }
}
