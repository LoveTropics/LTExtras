package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.environmentattribute.ClientDynamicEasManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ClientboundEnvironmentAttributesPacket(
        Identifier layerId,
        List<Modifier<?, ?>> modifyAttributes,
        List<EnvironmentAttribute<?>> clearAttributes,
        int transitionTicks
) implements CustomPacketPayload {
    // Probably a better way to go about this? Neo doesn't mark this registry as synced, so we can't use integer IDs
    private static final StreamCodec<RegistryFriendlyByteBuf, EnvironmentAttribute<?>> ATTRIBUTE_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(EnvironmentAttributes.CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundEnvironmentAttributesPacket> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, ClientboundEnvironmentAttributesPacket::layerId,
            Modifier.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundEnvironmentAttributesPacket::modifyAttributes,
            ATTRIBUTE_STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundEnvironmentAttributesPacket::clearAttributes,
            ByteBufCodecs.VAR_INT, ClientboundEnvironmentAttributesPacket::transitionTicks,
            ClientboundEnvironmentAttributesPacket::new
    );

    public static final Type<ClientboundEnvironmentAttributesPacket> TYPE = new Type<>(LTExtras.id("environment_attribute"));

    public static void handle(ClientboundEnvironmentAttributesPacket packet, IPayloadContext ctx) {
        for (Modifier<?, ?> modifier : packet.modifyAttributes) {
            modifier.apply(packet.layerId, packet.transitionTicks);
        }
        for (EnvironmentAttribute<?> attribute : packet.clearAttributes) {
            ClientDynamicEasManager.clearAttribute(packet.layerId, attribute, packet.transitionTicks);
        }
    }

    @Override
    public Type<ClientboundEnvironmentAttributesPacket> type() {
        return TYPE;
    }

    public record Modifier<Value, Argument>(
            EnvironmentAttribute<Value> attribute,
            AttributeModifier<Value, Argument> modifier,
            Argument argument
    ) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Modifier<?, ?>> STREAM_CODEC = ATTRIBUTE_STREAM_CODEC.dispatch(
                Modifier::attribute,
                Util.memoize(Modifier::createStreamCodec)
        );

        private static <Value> StreamCodec<RegistryFriendlyByteBuf, Modifier<Value, ?>> createStreamCodec(EnvironmentAttribute<Value> attribute) {
            StreamCodec<RegistryFriendlyByteBuf, AttributeModifier<Value, ?>> modifierCodec = ByteBufCodecs.fromCodecWithRegistriesTrusted(attribute.type().modifierCodec());
            return modifierCodec.dispatch(Modifier::modifier, Util.memoize(modifier ->
                    createStreamCodecWithModifier(attribute, modifier)
            ));
        }

        private static <Value, Argument> StreamCodec<RegistryFriendlyByteBuf, Modifier<Value, Argument>> createStreamCodecWithModifier(EnvironmentAttribute<Value> attribute, AttributeModifier<Value, Argument> modifier) {
            return ByteBufCodecs.fromCodecWithRegistriesTrusted(modifier.argumentCodec(attribute))
                    .map(argument -> new Modifier<>(attribute, modifier, argument), Modifier::argument);
        }

        public void apply(Identifier layerId, int transitionTicks) {
            ClientDynamicEasManager.modifyAttribute(layerId, attribute, modifier, argument, transitionTicks);
        }
    }
}
