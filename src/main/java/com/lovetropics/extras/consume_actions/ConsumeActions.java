package com.lovetropics.extras.consume_actions;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.lib.codec.CodecRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class ConsumeActions {

    public static final CodecRegistry<Identifier, MapCodec<? extends ConsumeAction>> REGISTRY = Util.make(CodecRegistry.idKeys(), registry -> {
        registry.register(LTExtras.id("mount"), MountConsumeAction.CODEC);
        registry.register(LTExtras.id("disguise"), DisguiseConsumeAction.CODEC);
    });

    public static final Codec<ConsumeAction> CODEC = REGISTRY.dispatch(ConsumeAction::getCodec, mapCodec -> mapCodec);

}
