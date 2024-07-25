package com.lovetropics.extras.entity;

import com.lovetropics.extras.network.message.ClientboundSetHologramTextPacket;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.StaticPreParser;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HologramEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(HologramEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_FORWARD_X = SynchedEntityData.defineId(HologramEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_FORWARD_Y = SynchedEntityData.defineId(HologramEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_FORWARD_Z = SynchedEntityData.defineId(HologramEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_FULLBRIGHT = SynchedEntityData.defineId(HologramEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int UPDATE_INTERVAL = SharedConstants.TICKS_PER_SECOND;

    private static final Vector3f GLOBAL_FORWARD = new Vector3f(0.0f, 0.0f, -1.0f);

    private static final NodeParser TEXT_PARSER = NodeParser.merge(TextParserV1.DEFAULT, Placeholders.DEFAULT_PLACEHOLDER_PARSER, StaticPreParser.INSTANCE);

    private String templateText = "";
    private TextNode parsedTemplate = TextNode.empty();

    private Component displayText = CommonComponents.EMPTY;

    @Nullable
    private DisplayInfo displayCache;

    private final Map<ServerPlayer, Component> trackingPlayers = new Reference2ObjectOpenHashMap<>();

    public HologramEntity(EntityType<?> type, Level level) {
        super(type, level);
        noCulling = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SCALE, 1.0f / 16.0f);
        builder.define(DATA_FORWARD_X, 0.0f);
        builder.define(DATA_FORWARD_Y, 0.0f);
        builder.define(DATA_FORWARD_Z, 0.0f);
        builder.define(DATA_FULLBRIGHT, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_FORWARD_X.equals(key) || DATA_FORWARD_Y.equals(key) || DATA_FORWARD_Z.equals(key)) {
            displayCache = null;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && !trackingPlayers.isEmpty()) {
            if (tickCount % UPDATE_INTERVAL == 0) {
                sendTextUpdatesToPlayers();
            }
        }
    }

    private void sendTextUpdatesToPlayers() {
        for (Map.Entry<ServerPlayer, Component> entry : trackingPlayers.entrySet()) {
            ServerPlayer player = entry.getKey();
            Component text = resolveTextForPlayer(player);
            if (!text.equals(entry.getValue())) {
                entry.setValue(text);
                sendTextToPlayer(player, text);
            }
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        Component text = resolveTextForPlayer(player);
        trackingPlayers.put(player, text);
        sendTextToPlayer(player, text);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        trackingPlayers.remove(player);
    }

    private Component resolveTextForPlayer(ServerPlayer player) {
        PlaceholderContext context = PlaceholderContext.of(player);
        return parsedTemplate.toText(context);
    }

    private void sendTextToPlayer(ServerPlayer player, Component text) {
        PacketDistributor.sendToPlayer(player, new ClientboundSetHologramTextPacket(getId(), text));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("text", Tag.TAG_LIST)) {
            ListTag lines = tag.getList("text", Tag.TAG_STRING);
            setTemplateText(lines.stream().map(Tag::getAsString).collect(Collectors.joining("<r>\n")));
        } else if (tag.contains("text", Tag.TAG_STRING)) {
            setTemplateText(tag.getString("text"));
        }

        if (tag.contains("scale", Tag.TAG_FLOAT)) {
            setScale(tag.getFloat("scale"));
        }

        if (tag.contains("forward", Tag.TAG_LIST)) {
            ListTag forward = tag.getList("forward", Tag.TAG_FLOAT);
            setForward(new Vector3f(forward.getFloat(0), forward.getFloat(1), forward.getFloat(2)));
        }

        if (tag.contains("fullbright", Tag.TAG_BYTE)) {
            setFullbright(tag.getBoolean("fullbright"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("text", templateText);
        tag.putFloat("scale", scale());

        Vector3f forward = forward();
        if (forward != null) {
            ListTag forwardList = new ListTag();
            forwardList.add(FloatTag.valueOf(forward.x()));
            forwardList.add(FloatTag.valueOf(forward.y()));
            forwardList.add(FloatTag.valueOf(forward.z()));
            tag.put("forward", forwardList);
        }

        tag.putBoolean("fullbright", fullbright());
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
        parsedTemplate = TEXT_PARSER.parseNode(templateText);
    }

    public void setDisplayText(Component text) {
        displayText = text;
        displayCache = null;
    }

    public void setScale(float scale) {
        entityData.set(DATA_SCALE, scale);
    }

    public float scale() {
        return entityData.get(DATA_SCALE);
    }

    public void setForward(@Nullable Vector3f forward) {
        if (forward != null) {
            entityData.set(DATA_FORWARD_X, forward.x());
            entityData.set(DATA_FORWARD_Y, forward.y());
            entityData.set(DATA_FORWARD_Z, forward.z());
        } else {
            entityData.set(DATA_FORWARD_X, 0.0f);
            entityData.set(DATA_FORWARD_Y, 0.0f);
            entityData.set(DATA_FORWARD_Z, 0.0f);
        }
        displayCache = null;
    }

    @Nullable
    public Vector3f forward() {
        float x = entityData.get(DATA_FORWARD_X);
        float y = entityData.get(DATA_FORWARD_Y);
        float z = entityData.get(DATA_FORWARD_Z);
        if (Mth.equal(x, 0.0f) && Mth.equal(y, 0.0f) && Mth.equal(z, 0.0f)) {
            return null;
        }
        return new Vector3f(x, y, z);
    }

    public void setFullbright(boolean fullbright) {
        entityData.set(DATA_FULLBRIGHT, fullbright);
    }

    public boolean fullbright() {
        return entityData.get(DATA_FULLBRIGHT);
    }

    public DisplayInfo display(Function<Component, List<Line>> splitter) {
        DisplayInfo display = displayCache;
        if (display == null) {
            Vector3f forward = forward();
            displayCache = display = computeDisplay(splitter, forward);
        }
        return display;
    }

    private DisplayInfo computeDisplay(Function<Component, List<Line>> splitter, Vector3f forward) {
        return new DisplayInfo(
                forward != null ? new Quaternionf().rotationTo(GLOBAL_FORWARD, forward) : null,
                splitter.apply(displayText)
        );
    }

    public record DisplayInfo(@Nullable Quaternionf rotation, List<Line> lines) {
    }

    public record Line(FormattedCharSequence text, int width) {
    }
}
