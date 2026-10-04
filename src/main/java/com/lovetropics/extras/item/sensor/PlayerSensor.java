package com.lovetropics.extras.item.sensor;

import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.lib.permission.PermissionsApi;
import com.lovetropics.lib.permission.role.Role;
import com.lovetropics.lib.permission.role.RoleReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.CommonColors;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec2;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record PlayerSensor(
        List<String> tags,
        List<String> roles,
        Set<UUID> players,
        Appearance appearance
) {
    public static final Codec<PlayerSensor> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.compactListCodec(Codec.STRING).optionalFieldOf("tag", List.of()).forGetter(PlayerSensor::tags),
            ExtraCodecs.compactListCodec(Codec.STRING).optionalFieldOf("roles", List.of()).forGetter(PlayerSensor::roles),
            ExtraCodecs.compactListCodec(UUIDUtil.STRING_CODEC).xmap(Set::copyOf, List::copyOf).optionalFieldOf("players", Set.of()).forGetter(PlayerSensor::players),
            Appearance.CODEC.fieldOf("appearance").forGetter(PlayerSensor::appearance)
    ).apply(i, PlayerSensor::new));

    public static final StreamCodec<ByteBuf, PlayerSensor> DIRECT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), PlayerSensor::tags,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), PlayerSensor::roles,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), PlayerSensor::players,
            Appearance.STREAM_CODEC, PlayerSensor::appearance,
            PlayerSensor::new
    );

    public static final Codec<Holder<PlayerSensor>> CODEC = RegistryFileCodec.create(ExtraRegistries.PLAYER_SENSOR, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PlayerSensor>> STREAM_CODEC = ByteBufCodecs.holder(ExtraRegistries.PLAYER_SENSOR, DIRECT_STREAM_CODEC);

    public boolean matches(ServerPlayer player) {
        for (String tag : tags) {
            if (player.entityTags().contains(tag)) {
                return true;
            }
        }
        if (!roles.isEmpty()) {
            RoleReader reader = PermissionsApi.lookup().byPlayer(player);
            for (String roleId : roles) {
                Role role = PermissionsApi.provider().get(roleId);
                if (reader.has(role)) {
                    return true;
                }
            }
        }
        if (players.contains(player.getUUID())) {
            return true;
        }
        return false;
    }

    public record Appearance(
            List<Decoration> decorations,
            // TODO: Could this be generalised to a normal decoration?
            Optional<Integer> markerBoxColor
    ) {
        public static final Codec<Appearance> CODEC = RecordCodecBuilder.create(i -> i.group(
                Decoration.CODEC.listOf().optionalFieldOf("decorations", List.of()).forGetter(Appearance::decorations),
                ExtraCodecs.ARGB_COLOR_CODEC.optionalFieldOf("marker_box_color").forGetter(Appearance::markerBoxColor)
        ).apply(i, Appearance::new));

        public static final StreamCodec<ByteBuf, Appearance> STREAM_CODEC = StreamCodec.composite(
                Decoration.STREAM_CODEC.apply(ByteBufCodecs.list()), Appearance::decorations,
                ByteBufCodecs.INT.apply(ByteBufCodecs::optional), Appearance::markerBoxColor,
                Appearance::new
        );
    }

    public record Decoration(
            Optional<Vec2> faceAnchor,
            Identifier sprite,
            Vec2 size,
            Vec2 offset,
            boolean fixedSize,
            int color
    ) {
        private static final StreamCodec<ByteBuf, Vec2> VEC2_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, v -> v.x,
                ByteBufCodecs.FLOAT, v -> v.y,
                Vec2::new
        );

        public static final Codec<Decoration> CODEC = RecordCodecBuilder.create(i -> i.group(
                Vec2.CODEC.optionalFieldOf("face_anchor").forGetter(Decoration::faceAnchor),
                Identifier.CODEC.fieldOf("sprite").forGetter(Decoration::sprite),
                Vec2.CODEC.fieldOf("size").forGetter(Decoration::size),
                Vec2.CODEC.fieldOf("offset").forGetter(Decoration::offset),
                Codec.BOOL.optionalFieldOf("fixed_size", true).forGetter(Decoration::fixedSize),
                ExtraCodecs.ARGB_COLOR_CODEC.optionalFieldOf("color", CommonColors.WHITE).forGetter(Decoration::color)
        ).apply(i, Decoration::new));

        public static final StreamCodec<ByteBuf, Decoration> STREAM_CODEC = StreamCodec.composite(
                VEC2_STREAM_CODEC.apply(ByteBufCodecs::optional), Decoration::faceAnchor,
                Identifier.STREAM_CODEC, Decoration::sprite,
                VEC2_STREAM_CODEC, Decoration::size,
                VEC2_STREAM_CODEC, Decoration::offset,
                ByteBufCodecs.BOOL, Decoration::fixedSize,
                ByteBufCodecs.INT, Decoration::color,
                Decoration::new
        );
    }
}
