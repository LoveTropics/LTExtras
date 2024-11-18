package com.lovetropics.extras.network;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.message.ClientboundCollectiblesListPacket;
import com.lovetropics.extras.network.message.ClientboundOpenCollectibleBasketPacket;
import com.lovetropics.extras.network.message.ClientboundPoiFacesPacket;
import com.lovetropics.extras.network.message.ClientboundRemovePoiPacket;
import com.lovetropics.extras.network.message.ClientboundUpdatePackControl;
import com.lovetropics.extras.network.message.ClientboundUpdatePoiPacket;
import com.lovetropics.extras.network.message.ClientboundSetAutoRejoinIntent;
import com.lovetropics.extras.network.message.ClientboundSetDisplayTextPacket;
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
        registrar.playToClient(ClientboundSetDisplayTextPacket.TYPE, ClientboundSetDisplayTextPacket.STREAM_CODEC, ClientboundSetDisplayTextPacket::handle);
        registrar.playToClient(ClientboundSetSkyColorPacket.TYPE, ClientboundSetSkyColorPacket.STREAM_CODEC, ClientboundSetSkyColorPacket::handle);
        registrar.playToClient(ClientboundWorldParticleEffectsPacket.TYPE, ClientboundWorldParticleEffectsPacket.STREAM_CODEC, ClientboundWorldParticleEffectsPacket::handle);
        registrar.playToClient(ClientboundSetAutoRejoinIntent.TYPE, ClientboundSetAutoRejoinIntent.STREAM_CODEC, ClientboundSetAutoRejoinIntent::handle);
        registrar.playToClient(ClientboundUpdatePoiPacket.TYPE, ClientboundUpdatePoiPacket.STREAM_CODEC, ClientboundUpdatePoiPacket::handle);
        registrar.playToClient(ClientboundRemovePoiPacket.TYPE, ClientboundRemovePoiPacket.STREAM_CODEC, ClientboundRemovePoiPacket::handle);
        registrar.playToClient(ClientboundPoiFacesPacket.TYPE, ClientboundPoiFacesPacket.STREAM_CODEC, ClientboundPoiFacesPacket::handle);
        registrar.playToClient(ClientboundOpenCollectibleBasketPacket.TYPE, ClientboundOpenCollectibleBasketPacket.STREAM_CODEC, ClientboundOpenCollectibleBasketPacket::handle);
        registrar.playToClient(ClientboundUpdatePackControl.TYPE, ClientboundUpdatePackControl.STREAM_CODEC, ClientboundUpdatePackControl::handle);
    }
}
