package com.lovetropics.extras.zipline;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

/** Short-lived loading/simulation tickets, with the same radius and lifetime as ender pearls. */
public final class ZiplineChunkTickets {
    static final int RADIUS = 2;
    static final Holder<TicketType> TYPE = LTExtras.registrate().simple("zipline", Registries.TICKET_TYPE,
            () -> new TicketType(40, TicketType.FLAG_LOADING | TicketType.FLAG_SIMULATION | TicketType.FLAG_KEEP_DIMENSION_ACTIVE));

    private ZiplineChunkTickets() {
    }

    public static void init() {
    }

    public static void refresh(ServerLevel level, BlockPos start, BlockPos end) {
        var chunks = level.getChunkSource();
        ChunkPos first = ChunkPos.containing(start);
        ChunkPos last = ChunkPos.containing(end);
        ChunkPos middle = ChunkPos.containing(BlockPos.containing(ZiplineEntity.attachment(start).lerp(ZiplineEntity.attachment(end), 0.5)));
        chunks.addTicketWithRadius(TYPE.value(), first, RADIUS);
        if (!last.equals(first)) {
            chunks.addTicketWithRadius(TYPE.value(), last, RADIUS);
        }
        if (!middle.equals(first) && !middle.equals(last)) {
            chunks.addTicketWithRadius(TYPE.value(), middle, RADIUS);
        }
        // Do not explicitly remove shared tickets: another rider may still need the same chunk.
        // They expire naturally after the last active carriage stops refreshing them.
        level.resetEmptyTime();
    }

    public static boolean isLoaded(ServerLevel level, BlockPos pos) {
        ChunkPos chunk = ChunkPos.containing(pos);
        return level.getChunkSource().getChunkNow(chunk.x(), chunk.z()) != null;
    }
}
