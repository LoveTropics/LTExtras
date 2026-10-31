package com.lovetropics.extras.zipline;

import com.lovetropics.extras.ExtraLangKeys;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum ZiplineStyle implements StringRepresentable {
    OLD_ROPE("old_rope", ExtraLangKeys.ZIPLINE_STYLE_OLD_ROPE),
    BAMBOO("bamboo", ExtraLangKeys.ZIPLINE_STYLE_BAMBOO),
    ;

    public static final EnumCodec<ZiplineStyle> CODEC = StringRepresentable.fromEnum(ZiplineStyle::values);
    private static final IntFunction<ZiplineStyle> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ZiplineStyle> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    private final String name;
    private final ExtraLangKeys displayName;

    ZiplineStyle(String name, ExtraLangKeys displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public Component displayName() {
        return displayName.get();
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
