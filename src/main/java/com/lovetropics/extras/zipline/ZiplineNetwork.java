package com.lovetropics.extras.zipline;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Every rope in a level. It is saved with the level and synced in full to all of its players, rather than stored in the
 * poles, so that ropes and the entities riding them keep working whether or not the chunks of the poles are loaded.
 * <p>
 * Changes are only made on the server, which syncs them to clients.
 */
public final class ZiplineNetwork {
    public static final int MAX_CONNECTIONS = 2;

    private static final Codec<List<PoleEntry>> POLES_CODEC = PoleEntry.CODEC.listOf();
    private static final Codec<List<RopeEntry>> ROPES_CODEC = RopeEntry.CODEC.listOf();
    private static final StreamCodec<ByteBuf, List<PoleEntry>> POLES_STREAM_CODEC = PoleEntry.STREAM_CODEC.apply(ByteBufCodecs.list());
    private static final StreamCodec<ByteBuf, List<RopeEntry>> ROPES_STREAM_CODEC = RopeEntry.STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final IAttachmentSerializer<ZiplineNetwork> SERIALIZER = new IAttachmentSerializer<>() {
        @Override
        public ZiplineNetwork read(IAttachmentHolder holder, ValueInput input) {
            ZiplineNetwork network = new ZiplineNetwork(holder);
            network.load(input.read("poles", POLES_CODEC).orElse(List.of()), input.read("ropes", ROPES_CODEC).orElse(List.of()));
            return network;
        }

        @Override
        public boolean write(ZiplineNetwork network, ValueOutput output) {
            if (network.poles.isEmpty()) {
                return false;
            }
            output.store("poles", POLES_CODEC, network.poleEntries());
            output.store("ropes", ROPES_CODEC, network.ropeEntries());
            return true;
        }
    };

    public static final AttachmentSyncHandler<ZiplineNetwork> SYNC_HANDLER = new AttachmentSyncHandler<>() {
        @Override
        public void write(RegistryFriendlyByteBuf buf, ZiplineNetwork network, boolean initialSync) {
            POLES_STREAM_CODEC.encode(buf, network.poleEntries());
            ROPES_STREAM_CODEC.encode(buf, network.ropeEntries());
        }

        @Override
        public ZiplineNetwork read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable ZiplineNetwork previousValue) {
            ZiplineNetwork network = previousValue != null ? previousValue : new ZiplineNetwork(holder);
            network.load(POLES_STREAM_CODEC.decode(buf), ROPES_STREAM_CODEC.decode(buf));
            return network;
        }
    };

    private final Level level;
    // Only poles that have ropes tied to them: both poles of a rope know about it
    private final Map<BlockPos, Pole> poles = new HashMap<>();
    private @Nullable List<ZiplineSegment> ropes;

    public ZiplineNetwork(IAttachmentHolder holder) {
        level = (Level) holder;
    }

    public static ZiplineNetwork get(Level level) {
        return level.getData(ExtraAttachments.ZIPLINE_NETWORK);
    }

    public @Nullable ZiplineSegment getSegment(BlockPos from, BlockPos to) {
        Pole fromPole = poles.get(from);
        Pole toPole = poles.get(to);
        if (fromPole == null || toPole == null) {
            return null;
        }
        ZiplineSettings settings = fromPole.connections.get(to);
        return settings != null ? new ZiplineSegment(from, fromPole.anchor(from), to, toPole.anchor(to), settings) : null;
    }

    public List<ZiplineSegment> getSegmentsFrom(BlockPos pos) {
        Pole pole = poles.get(pos);
        if (pole == null) {
            return List.of();
        }
        List<ZiplineSegment> segments = new ArrayList<>(pole.connections.size());
        for (BlockPos target : pole.connections.keySet()) {
            ZiplineSegment segment = getSegment(pos, target);
            if (segment != null) {
                segments.add(segment);
            }
        }
        return segments;
    }

    /**
     * @return the segment leaving the given pole that is best aligned with the given direction
     */
    public @Nullable ZiplineSegment pickSegmentFrom(BlockPos pos, Vec3 direction) {
        ZiplineSegment best = null;
        double bestAlignment = Double.NEGATIVE_INFINITY;
        for (ZiplineSegment segment : getSegmentsFrom(pos)) {
            double alignment = direction.dot(segment.tangent(0.0));
            if (alignment > bestAlignment) {
                best = segment;
                bestAlignment = alignment;
            }
        }
        return best;
    }

    /**
     * @return the segment that leads on from the given pole, away from the pole that the rider arrived from
     */
    public @Nullable ZiplineSegment getContinuation(BlockPos pos, BlockPos arrivedFrom) {
        for (ZiplineSegment segment : getSegmentsFrom(pos)) {
            if (!segment.to().equals(arrivedFrom)) {
                return segment;
            }
        }
        return null;
    }

    public boolean isConnected(BlockPos from, BlockPos to) {
        Pole pole = poles.get(from);
        return pole != null && pole.connections.containsKey(to);
    }

    public boolean isFull(BlockPos pos) {
        Pole pole = poles.get(pos);
        return pole != null && pole.connections.size() >= MAX_CONNECTIONS;
    }

    /**
     * @return every rope in the level, each oriented from the lower of its two poles
     */
    public List<ZiplineSegment> getRopes() {
        List<ZiplineSegment> ropes = this.ropes;
        if (ropes == null) {
            ropes = new ArrayList<>();
            for (Map.Entry<BlockPos, Pole> entry : poles.entrySet()) {
                BlockPos pos = entry.getKey();
                for (BlockPos target : entry.getValue().connections.keySet()) {
                    ZiplineSegment segment = pos.compareTo(target) < 0 ? getSegment(pos, target) : null;
                    if (segment != null) {
                        ropes.add(segment);
                    }
                }
            }
            this.ropes = ropes;
        }
        return ropes;
    }

    public ZiplineSegment.@Nullable Hit raycast(Vec3 start, Vec3 end, double radius) {
        AABB rayBounds = new AABB(start, end).inflate(radius);
        ZiplineSegment.Hit closest = null;
        for (ZiplineSegment segment : getRopes()) {
            if (!segment.bounds().inflate(radius).intersects(rayBounds)) {
                continue;
            }
            ZiplineSegment.Hit hit = segment.raycast(start, end, radius);
            if (hit != null && (closest == null || hit.distance() < closest.distance())) {
                closest = hit;
            }
        }
        return closest;
    }

    /**
     * @param fromAnchorOffset where the rope is tied on the pole at {@code from}, relative to it
     * @param toAnchorOffset   where the rope is tied on the pole at {@code to}, relative to it
     */
    public void connect(BlockPos from, Vec3 fromAnchorOffset, BlockPos to, Vec3 toAnchorOffset, ZiplineSettings settings) {
        if (from.equals(to)) {
            return;
        }
        poles.computeIfAbsent(from, p -> new Pole(fromAnchorOffset)).connections.put(to, settings);
        poles.computeIfAbsent(to, p -> new Pole(toAnchorOffset)).connections.put(from, settings);
        onChanged();
    }

    public void disconnect(BlockPos from, BlockPos to) {
        boolean removed = removeConnection(from, to);
        removed |= removeConnection(to, from);
        if (removed) {
            onChanged();
        }
    }

    public void setSettings(BlockPos from, BlockPos to, ZiplineSettings settings) {
        Pole fromPole = poles.get(from);
        Pole toPole = poles.get(to);
        if (fromPole != null && toPole != null && fromPole.connections.containsKey(to)) {
            fromPole.connections.put(to, settings);
            toPole.connections.put(from, settings);
            onChanged();
        }
    }

    /**
     * Takes down all the ropes tied to a pole that is no longer there.
     */
    public void removePole(BlockPos pos) {
        Pole pole = poles.remove(pos);
        if (pole == null) {
            return;
        }
        for (BlockPos target : pole.connections.keySet()) {
            removeConnection(target, pos);
        }
        onChanged();
    }

    private boolean removeConnection(BlockPos pos, BlockPos target) {
        Pole pole = poles.get(pos);
        if (pole == null || pole.connections.remove(target) == null) {
            return false;
        }
        if (pole.connections.isEmpty()) {
            poles.remove(pos);
        }
        return true;
    }

    private void onChanged() {
        ropes = null;
        level.syncData(ExtraAttachments.ZIPLINE_NETWORK);
    }

    private List<PoleEntry> poleEntries() {
        List<PoleEntry> entries = new ArrayList<>(poles.size());
        poles.forEach((pos, pole) -> entries.add(new PoleEntry(pos, pole.anchorOffset)));
        return entries;
    }

    private List<RopeEntry> ropeEntries() {
        List<RopeEntry> entries = new ArrayList<>();
        poles.forEach((pos, pole) -> pole.connections.forEach((target, settings) -> {
            if (pos.compareTo(target) < 0) {
                entries.add(new RopeEntry(pos, target, settings));
            }
        }));
        return entries;
    }

    private void load(List<PoleEntry> poleEntries, List<RopeEntry> ropeEntries) {
        poles.clear();
        for (PoleEntry entry : poleEntries) {
            poles.put(entry.pos(), new Pole(entry.anchorOffset()));
        }
        for (RopeEntry entry : ropeEntries) {
            Pole from = poles.get(entry.from());
            Pole to = poles.get(entry.to());
            if (from != null && to != null && !entry.from().equals(entry.to())) {
                from.connections.put(entry.to(), entry.settings());
                to.connections.put(entry.from(), entry.settings());
            }
        }
        poles.values().removeIf(pole -> pole.connections.isEmpty());
        ropes = null;
    }

    private static final class Pole {
        private final Vec3 anchorOffset;
        // In the order the ropes were tied, so that riders take the same way on to the next rope every time
        private final Map<BlockPos, ZiplineSettings> connections = new LinkedHashMap<>(MAX_CONNECTIONS);

        private Pole(Vec3 anchorOffset) {
            this.anchorOffset = anchorOffset;
        }

        private Vec3 anchor(BlockPos pos) {
            return Vec3.atLowerCornerOf(pos).add(anchorOffset);
        }
    }

    private record PoleEntry(BlockPos pos, Vec3 anchorOffset) {
        private static final Codec<PoleEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(PoleEntry::pos),
                Vec3.CODEC.fieldOf("anchor").forGetter(PoleEntry::anchorOffset)
        ).apply(i, PoleEntry::new));
        private static final StreamCodec<ByteBuf, PoleEntry> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, PoleEntry::pos,
                Vec3.STREAM_CODEC, PoleEntry::anchorOffset,
                PoleEntry::new
        );
    }

    private record RopeEntry(BlockPos from, BlockPos to, ZiplineSettings settings) {
        private static final Codec<RopeEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockPos.CODEC.fieldOf("from").forGetter(RopeEntry::from),
                BlockPos.CODEC.fieldOf("to").forGetter(RopeEntry::to),
                ZiplineSettings.MAP_CODEC.forGetter(RopeEntry::settings)
        ).apply(i, RopeEntry::new));
        private static final StreamCodec<ByteBuf, RopeEntry> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, RopeEntry::from,
                BlockPos.STREAM_CODEC, RopeEntry::to,
                ZiplineSettings.STREAM_CODEC, RopeEntry::settings,
                RopeEntry::new
        );
    }
}
